package com.java.chtzyw.data;

public interface ResultListener {
    void onSuccess(int code);
    void onFailure(int code);
}
