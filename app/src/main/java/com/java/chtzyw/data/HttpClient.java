package com.java.chtzyw.data;

import android.util.ArrayMap;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import okhttp3.OkHttpClient;

public class HttpClient {

    /*单例模式，仅允许通过builder创建*/
    private static HttpClient client;
    private HttpClient(){
        okHttpClient=new OkHttpClient.Builder().build();
    }
    private static HttpClient getClient(){
        if(client==null)
            client=new HttpClient();
        return client;
    }

    private static String newsUrl;
    private static OkHttpClient okHttpClient;
    public static String getNewsUrl(){
        return newsUrl;
    }
    public static OkHttpClient getOkHttpClient(){
        return okHttpClient;
    }

    public static final class Builder{
        private String baseUrl;
        private String size;
        private String startDate;
        private String endDate;
        private String words;
        private String categories;
        private String page;

        public Builder(){
            baseUrl="https://api2.newsminer.net/svc/news/queryNewsList";
            size="";
            startDate="";
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date=new Date(System.currentTimeMillis());
            endDate="endDate="+simpleDateFormat.format(date)+"&";
            words="";
            categories="";
            page="page=1";
        }
        public Builder setBaseUrl(String baseUrl){
            this.baseUrl=baseUrl;
            return this;
        }
        public Builder setSize(int size){
            this.size="size="+size+"&";
            return this;
        }
        public Builder setStartDate(String startDate){
            this.startDate="startDate="+startDate+"&";
            return this;
        }
        public Builder setEndDate(String endDate) {
            this.endDate = "endDate="+endDate+"&";
            return this;
        }
        public Builder setWords(String words) {
            this.words ="words="+words+"&";
            return this;
        }
        public Builder setCategories(int categories){
            if(categories>1)
                this.categories="categories="+Category.DEFAULT_CATEGORIES[categories]+"&";
            return this;
        }
        public Builder setPage(int page){
            this.page="page="+page;
            return this;
        }
        public HttpClient build(){
            newsUrl=baseUrl+"?"+size+startDate+endDate+words+categories+page;
            HttpClient client= HttpClient.getClient();
            return client;
        }
    }
}
