package com.java.chtzyw.data;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsHandler {
    private static NewsHandler handler;
    private NewsHandler(Context context){
        Log.d("MainActivity", "Construct");
        mContext=context;
        String[] files=mContext.fileList();
        fileList=new LinkedList<>();
        if(files.length==0)
            Log.d("MainActivity", "no File");
        else
            for(String file:files){
                fileList.addLast(file);
            }
//        fileList=new LinkedList<File>(Arrays.asList(files));
        newsList=new LinkedList<>();
    }
    public static NewsHandler getHandler(Context context){
        if(handler==null){
            handler=new NewsHandler(context);
        }
        return handler;
    }

    private final int localLoad=20;         //请求本地新闻数量
    private final int netLoad=20;           //请求网络新闻数量
    private LinkedList<News> newsList;
    private LinkedList<String> fileList;
    private LinkedList<News> favorList;
    private static Context mContext;

    public LinkedList<News> getNewsList(){
        return newsList;
    }

    /*注意综合栏调用完init后还需要调用sendType*/
    public LinkedList<News> initNewsList(){
        localNewsLoad(fileList.size());
        return newsList;
    }

    public void newsSave(News news){
        Gson gson=new Gson();
        String content=gson.toJson(news);
        String filename=news.getPublishTime().replaceAll("[-:\\s]","");
        filename+=news.getNewsID();
        fileSave(filename,content);
    }

    public LinkedList<News> sendLocalRequest(int typeId){
        localNewsLoad(localLoad);
        LinkedList<News> newsTypeList=typeNewsLoad(typeId);
        return newsList;
    }

    public LinkedList<News> sendTypeRequest(int typeId){
        return typeNewsLoad(typeId);
    }

    public void sendNetworkRequest(HttpClient httpClient,ResultListener resultListener){
        Request request=new Request.Builder().url(httpClient.getNewsUrl()).build();
        httpClient.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                resultListener.onFailure(-1);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonData=response.body().string();
                Gson gson=new Gson();
                NewsJson newsJson=gson.fromJson(jsonData,NewsJson.class);
                for(News news:newsJson.getData()){
                    clearContent(news);
                    clearImage(news);
                    newsList.addFirst(news);
                }
                resultListener.onSuccess(0);
            }
        });
    }

    public void sendFavorSave(News news){
        File path=mContext.getDir("favor",Context.MODE_PRIVATE);
        //可以换成收藏时间+新闻ID
        File file=new File(path,news.getNewsID());
        Gson gson=new Gson();
        String jsonData=gson.toJson(news);
        favorSave(file,jsonData);
    }

    public LinkedList<News> sendFavorLoad(){
        File path=mContext.getDir("favor",Context.MODE_PRIVATE);
        File[] files=path.listFiles();
        Gson gson=new Gson();
        if(files.length==0)
            return null;
        for(File file:files){
            String jsonData=favorLoad(file);
            News news=gson.fromJson(jsonData,News.class);
            favorList.addLast(news);
        }
        return favorList;
    }

    private void localNewsLoad(int loadNum){
        Gson gson=new Gson();
        for(int i=0;i<loadNum&&!fileList.isEmpty();i++){
            String file=fileList.removeFirst();
            News news=gson.fromJson(fileLoad(file),News.class);
            newsList.addLast(news);
        }
    }

    private LinkedList<News> typeNewsLoad(int typeId){
        String type=Category.DEFAULT_CATEGORIES[typeId];
        LinkedList<News> newsTypeList=new LinkedList<>();
        for(News news:newsList){
            if(typeId==0||news.getCategory().equals(type))
                newsTypeList.addLast(news);
        }
        return newsTypeList;
    }

    private void clearContent(News news){
        news.setContent(news.getContent().replaceAll("\\\n","\n"));
    }

    private void clearImage(News news){
        news.setImage(news.getImage().replaceAll("[\\[\\]\\s]",""));
    }

    private String fileLoad(String filename){
        FileInputStream in=null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        try{
            in=mContext.openFileInput(filename);
            reader=new BufferedReader(new InputStreamReader(in));
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

    private void fileSave(String filename,String inputText){
        File file=new File(mContext.getFilesDir(),filename);
        if(file.exists()){
            return;
        }

        FileOutputStream out=null;
        BufferedWriter writer=null;
        try {
            out=mContext.openFileOutput(filename,Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
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

    private String favorLoad(File file){
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

    private void favorSave(File file,String inputText){
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