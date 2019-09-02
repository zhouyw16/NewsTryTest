package com.java.chtzyw.data;

import android.util.ArrayMap;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient {
    public final static String BASE_URL="https://api2.newsminer.net/svc/news/queryNewsList";
    public static String newsUrl=BASE_URL;
    private static HttpClient client;
    private static OkHttpClient okHttpClient;
    private HttpClient(){
        okHttpClient=new OkHttpClient.Builder().build();
    }
    public static HttpClient getClient(){
        if(client==null)
            client=new HttpClient();
        return client;
    }
    public static OkHttpClient getOkHttpClient(){
        return okHttpClient;
    }
    public static String getNewsUrl(){
        return newsUrl;
    }

    public static final class Builder{
        private String baseUrl=BASE_URL;
        private String url;
        private Map<String,String> params=new ArrayMap<>();

        public Builder(){}
        public Builder setBaseUrl(String baseUrl){
            this.baseUrl=baseUrl;
            return this;
        }
        public Builder setUrl(String url){
            this.url=url;
            return this;
        }
        public Builder setParams(String key,String value){
            this.params.put(key,value);
            return this;
        }
        public HttpClient build(){
            newsUrl=getUrl();
            HttpClient client= HttpClient.getClient();
            return client;
        }
        private String getUrl(){
            String value="";
            if(!params.isEmpty()){
                for(Map.Entry<String,String> entry:params.entrySet()){
                    value+=value.equals("")?"?":"&";
                    value+=entry.getKey()+"="+entry.getValue();
                }
            }
            return baseUrl+value;
        }
    }
}
