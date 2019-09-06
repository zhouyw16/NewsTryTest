package com.java.chtzyw.data;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.java.chtzyw.MainApplication;

import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
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
        mContext= MainApplication.getContextObject();
        allNewsList=new ArrayList<>();
        favorList=new LinkedList<>();
        favorHash=new HashSet<>();
        tagNewsHash=new HashSet<>();
        allNewsHash=new HashSet<>();
        recNewsHash=new HashSet<>();
        pageList=new ArrayList<>();
        for(int i=0;i<12;i++)
            pageList.add(1);
        initAllNewsList();
        initFavorList();
    }
    /*将全部本地内容读取至缓存，可以考虑以下两点：
     * 程序启动时直接创建NewsHandler对象
     * 设置本地文件读取上限，暂定为LOCAL_LOAD_MAX条
     * */
    private void initAllNewsList(){
        /*获取本地文件目录*/
        File path=mContext.getDir("news",Context.MODE_PRIVATE);
        File[] files=path.listFiles();
        /*创建分类链表*/
        for(int i=0;i<12;i++){
            allNewsList.add(new LinkedList<News>());
        }
        /*缓存本地新闻*/
        Gson gson=new Gson();
        int loadNum=files.length<LOCAL_LOAD_MAX?files.length:LOCAL_LOAD_MAX;
        for(int i=0;i<loadNum;i++){
            News news=gson.fromJson(fileLoad(files[i]),News.class);
            allNewsList.get(0).addLast(news);
            allNewsList.get(1).addLast(news);
            allNewsList.get(TagManager.getTagId(news.getCategory())).addLast(news);
            allNewsHash.add(news.getNewsID());
            tagNewsHash.add(news.getNewsID());
            recNewsHash.add(news.getNewsID());
        }
    }
    /*将本地收藏读至缓存*/
    private void initFavorList(){
        File path=mContext.getDir("favor",Context.MODE_PRIVATE);
        File[] files=path.listFiles();
        Gson gson=new Gson();
        if(files.length==0)
            return;
        for(File file:files){
            String jsonData=fileLoad(file);
            News news=gson.fromJson(jsonData,News.class);
            favorList.addLast(news);
            favorHash.add(news.getNewsID());
        }
    }

    private Context mContext;                   //文本内容，用于文件读写
    private List<LinkedList<News>> allNewsList; //各分类列表
    private LinkedList<News> favorList;         //收藏列表
    private HashSet<String> favorHash;          //收藏新闻Id哈希表
    private HashSet<String> tagNewsHash;        //分类新闻Id哈希表
    private HashSet<String> allNewsHash;        //综合新闻Id哈希表
    private HashSet<String> recNewsHash;        //推荐新闻Id哈希表
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

        HttpClient httpClient;
        /*推荐页单独处理*/
        if(categoryId==1){
            Random rand=new Random();
            int searchId=rand.nextInt(10)+2;
            httpClient=new HttpClient.Builder()
                    .setSize(needNum)
                    .setCategories(searchId)
                    .build();
        }
        else{
            httpClient=new HttpClient.Builder()
                    .setSize(needNum)
                    .setCategories(categoryId)
                    .build();
        }
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
                    if(categoryId==0&&!allNewsHash.contains(news.getNewsID())){
                        clearContent(news);
                        clearImage(news);
                        allNewsList.get(categoryId).addFirst(news);
                        allNewsHash.add(news.getNewsID());
                    }
                    else if(categoryId==1&&!recNewsHash.contains(news.getNewsID())){
                        clearContent(news);
                        clearImage(news);
                        allNewsList.get(categoryId).addFirst(news);
                        recNewsHash.add(news.getNewsID());
                    }
                    else if(categoryId>1&&!tagNewsHash.contains(news.getNewsID())){
                        clearContent(news);
                        clearImage(news);
                        allNewsList.get(categoryId).addFirst(news);
                        tagNewsHash.add(news.getNewsID());
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
        HttpClient httpClient;
        /*推荐页单独处理*/
        if(categoryId==1){
            Random rand=new Random();
            int searchId=rand.nextInt(10)+2;
            httpClient=new HttpClient.Builder()
                    .setSize(needNum)
                    .setCategories(searchId)
                    .setPage(pageList.get(categoryId))
                    .build();
        }
        else{
            httpClient=new HttpClient.Builder()
                    .setSize(needNum)
                    .setCategories(categoryId)
                    .setPage(pageList.get(categoryId))
                    .build();
        }
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
                    if(categoryId==0&&!allNewsHash.contains(news.getNewsID())){
                        clearContent(news);
                        clearImage(news);
                        allNewsList.get(categoryId).addLast(news);
                        allNewsHash.add(news.getNewsID());
                    }
                    else if(categoryId==1&&!recNewsHash.contains(news.getNewsID()))
                    {
                        clearContent(news);
                        clearImage(news);
                        allNewsList.get(categoryId).addLast(news);
                        recNewsHash.add(news.getNewsID());
                    }
                    else if(categoryId>1&&!tagNewsHash.contains(news.getNewsID())){
                        clearContent(news);
                        clearImage(news);
                        allNewsList.get(categoryId).addLast(news);
                        tagNewsHash.add(news.getNewsID());
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

    /*删除新闻请求*/
    public void sendNewsDeleteRequest(List<News> newsList,int position){
        newsList.remove(position);
        //如果先点进去新闻缓存了，
        //再退出来删除新闻，可能会有点问题
        //因为没从存储中把新闻删了
    }

    /*清除缓存请求*/
    public boolean sendDeleteCacheRequest(){
        for(LinkedList<News> newsList:allNewsList){
            newsList.clear();
        }
        allNewsHash.clear();
        tagNewsHash.clear();
        recNewsHash.clear();
        File path=mContext.getDir("news",Context.MODE_PRIVATE);
        return deleteAll(path);
    }

    /*保存收藏请求*/
    public boolean sendFavorSaveRequest(News news){
        if(favorHash.contains(news.getNewsID()))
            return false;
        File path=mContext.getDir("favor",Context.MODE_PRIVATE);
        File file=new File(path,news.getNewsID());
        Gson gson=new Gson();
        String jsonData=gson.toJson(news);
        fileSave(file,jsonData);
        favorList.addFirst(news);
        favorHash.add(news.getNewsID());
        return true;
    }

    /*删除收藏请求*/
    public boolean sendFavorDeleteRequest(News news){
        File path=mContext.getDir("favor",Context.MODE_PRIVATE);
        File file=new File(path,news.getNewsID());
        return favorList.remove(news) && favorHash.remove(news.getNewsID()) && file.delete();
    }

    /*加载收藏请求*/
    public LinkedList<News> sendFavorLoadRequest(){
        return favorList;
    }

    /*清除全部收藏请求*/
    public boolean sendFavorAllDeleteRequest(){
        favorList.clear();
        favorHash.clear();
        File path=mContext.getDir("favor",Context.MODE_PRIVATE);
        return deleteAll(path);
    }

    /*加载分类列表设置*/
    public List<TagManager.Tag> sendSettingLoadRequest(){
        File path=mContext.getDir("setting", Context.MODE_PRIVATE);
        File file=new File(path,"tag_setting.json");
        if(file.exists()){
            Gson gson=new Gson();
            String jsonData=fileLoad(file);
            TagManager.TagJson tagJson=gson.fromJson(jsonData,TagManager.TagJson.class);
            return tagJson.tagJsonList;
        }
        else{
            return TagManager.getDefaultTagList();
        }
    }

    /*保存分类列表设置*/
    public void sendSettingSaveRequest(TagManager.TagJson tagJson){
        File path=mContext.getDir("setting", Context.MODE_PRIVATE);
        File file=new File(path,"tag_setting.json");
        Gson gson=new Gson();
        String jsonData=gson.toJson(tagJson);
        settingSave(file,jsonData);
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

    /*设置保存*/
    private void settingSave(File file,String inputText){
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

    private boolean deleteAll(File path){
        if(path.exists()){
            File[] files=path.listFiles();
            for(File file:files){
                file.delete();
            }
            return path.delete();
        }
        else{
            return false;
        }
    }
}

