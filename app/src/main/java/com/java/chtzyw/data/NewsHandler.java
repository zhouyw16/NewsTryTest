package com.java.chtzyw.data;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.java.chtzyw.MainApplication;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsHandler {

    /*单例模式*/
    private static NewsHandler handler;
    public static NewsHandler getHandler(){
        if(handler==null){
            handler=new NewsHandler();
        }
        return handler;
    }
    private NewsHandler(){
        Log.d("MainActivity", "Construct");
        mContext= MainApplication.getContextObject();
        favorList=new LinkedList<>();
        newsHash=new HashSet<>();
        pageList=new ArrayList<>();
        for(int i=0;i<12;i++)
            pageList.add(1);
        initAllNewsList();
    }
    /*将全部本地内容读取至缓存，可以考虑以下两点：
     * 程序启动时直接创建NewsHandler对象
     * 设置本地文件读取上限，暂定为LOCAL_LOAD_MAX条
     * */
    private void initAllNewsList(){
        /*获取本地文件目录*/
        if(mContext==null)
            Log.d("MainActivity", "initAllNewsList: mContext=null");
        File path=mContext.getDir("news",Context.MODE_PRIVATE);
        File[] files=path.listFiles();
        /*创建分类链表*/
        allNewsList=new ArrayList<>();
        for(int i=0;i<12;i++){
            allNewsList.add(new LinkedList<News>());
        }
        /*缓存本地新闻*/
        Gson gson=new Gson();
        int loadNum=files.length<LOCAL_LOAD_MAX?files.length:LOCAL_LOAD_MAX;
        for(int i=0;i<loadNum;i++){
            News news=gson.fromJson(fileLoad(files[i]),News.class);
            allNewsList.get(0).addLast(news);
            allNewsList.get(Category.getCategoryId(news.getCategory())).addLast(news);
            newsHash.add(news.getNewsID());
        }
    }

    private Context mContext;                   //文本内容，用于文件读写
    private List<LinkedList<News>> allNewsList; //各分类列表
    private LinkedList<News> favorList;         //收藏列表
    private HashSet<String> newsHash;           //新闻Id哈希表，用于判重
    private List<Integer> pageList;             //类别页数
    private final int LOCAL_LOAD_MAX=200;       //本地新闻加载上限

    public List<LinkedList<News>> getAllNewsList(){
        return allNewsList;
    }

    /*各分类初始加载*/
    public LinkedList<News> sendInitNewsList(int categoryId){
        return allNewsList.get(categoryId);
    }

    /*各分类下拉刷新*/
    public void sendRefreshRequest(int categoryId,int needNum,ResultListener resultListener){
        HttpClient httpClient=new HttpClient.Builder()
                .setSize(needNum)
                .setCategories(categoryId)
                .build();
        Log.d("MainActivity", "onClick: "+httpClient.getNewsUrl());
        Request request=new Request.Builder().url(httpClient.getNewsUrl()).build();
        httpClient.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                resultListener.onFailure(-1);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int pastSize=allNewsList.get(categoryId).size();
                String jsonData=response.body().string();
                Gson gson=new Gson();
                NewsJson newsJson=gson.fromJson(jsonData,NewsJson.class);
                for(News news:newsJson.getData()){
                    if(!newsHash.contains(news.getNewsID())){
                        clearContent(news);
                        clearImage(news);
                        allNewsList.get(categoryId).addFirst(news);
                    }
                }
                int nowSize=allNewsList.get(categoryId).size();
                resultListener.onSuccess(allNewsList.get(categoryId),nowSize-pastSize);
            }
        });
    }

    /*各分类上拉加载*/
    public void sendLoadRequest(int categoryId,int needNum,ResultListener resultListener){

        pageList.set(categoryId,pageList.get(categoryId)+1);
        HttpClient httpClient=new HttpClient.Builder()
                .setSize(needNum)
                .setCategories(categoryId)
                .setPage(pageList.get(categoryId))
                .build();
        Log.d("MainActivity", "onClick: "+httpClient.getNewsUrl());
        Request request=new Request.Builder().url(httpClient.getNewsUrl()).build();
        httpClient.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                resultListener.onFailure(-1);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int pastSize=allNewsList.get(categoryId).size();
                String jsonData=response.body().string();
                Gson gson=new Gson();
                NewsJson newsJson=gson.fromJson(jsonData,NewsJson.class);
                for(News news:newsJson.getData()){
                    if(!newsHash.contains(news.getNewsID())){
                        clearContent(news);
                        clearImage(news);
                        allNewsList.get(categoryId).addLast(news);
                    }
                }
                int nowSize=allNewsList.get(categoryId).size();
                resultListener.onSuccess(allNewsList.get(categoryId),nowSize-pastSize);
            }
        });
    }

    /*保存新闻请求*/
    public void sendNewsSaveRequest(News news){
        File path=mContext.getDir("news",Context.MODE_PRIVATE);
        File file=new File(path,news.getNewsID());
        Gson gson=new Gson();
        String jsonData=gson.toJson(news);
        fileSave(file,jsonData);
    }

    /*保存收藏请求*/
    public void sendFavorSaveRequest(News news){
        File path=mContext.getDir("favor",Context.MODE_PRIVATE);
        //可以换成收藏时间+新闻ID
        File file=new File(path,news.getNewsID());
        Gson gson=new Gson();
        String jsonData=gson.toJson(news);
        fileSave(file,jsonData);
    }

    /*加载收藏请求*/
    public LinkedList<News> sendFavorLoadRequest(){
        File path=mContext.getDir("favor",Context.MODE_PRIVATE);
        File[] files=path.listFiles();
        Gson gson=new Gson();
        if(files.length==0)
            return null;
        for(File file:files){
            String jsonData=fileLoad(file);
            News news=gson.fromJson(jsonData,News.class);
            favorList.addLast(news);
        }
        return favorList;
    }

    /*新闻内容过滤*/
    private void clearContent(News news){
        news.setContent(news.getContent().replaceAll("\\\n","\n"));
    }

    /*新闻图片过滤*/
    private void clearImage(News news){
        news.setImage(news.getImage().replaceAll("[\\[\\]\\s]",""));
    }

    /*文件读取*/
    private String fileLoad(File file){
        FileReader in=null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        try{
            in=new FileReader(file);
            reader=new BufferedReader(in);
            String line="";
            while((line=reader.readLine())!=null)
                content.append(line);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                if(reader!=null)
                    reader.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        return content.toString();
    }

    /*文件保存*/
    private void fileSave(File file,String inputText){
        if(file.exists()){
            return;
        }

        FileWriter out=null;
        BufferedWriter writer=null;
        try {
            out=new FileWriter(file);
            writer=new BufferedWriter(out);
            writer.write(inputText);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                if(writer!=null)
                    writer.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
