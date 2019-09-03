package com.java.chtzyw.data;

import java.util.LinkedList;

public interface ResultListener {
    void onSuccess(LinkedList<News> newsList,int newsNum);
    void onFailure(int code);
}
