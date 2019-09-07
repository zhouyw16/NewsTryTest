package com.java.chtzyw;

import android.app.Application;
import android.content.Context;

import com.java.chtzyw.data.NewsHandler;
import com.java.chtzyw.data.TagManager;

public class MainApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        //获取Context
        super.onCreate();
        context = getApplicationContext();
        NewsHandler.getHandler();
        TagManager.getI();
    }

    //返回
    public static Context getContextObject() {
        return context;
    }


}
