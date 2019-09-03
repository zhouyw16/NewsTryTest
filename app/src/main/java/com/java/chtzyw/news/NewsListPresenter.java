package com.java.chtzyw.news;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.java.chtzyw.data.News;
import com.java.chtzyw.data.NewsHandler;
import com.java.chtzyw.data.ResultListener;

import java.util.LinkedList;

public class NewsListPresenter {
    private final static int NEWS_NUM = 15;

    private int category;
    private boolean loading = false;

    private NewsListAdapter adapter;
    private NewsListFragment fragment;

    public NewsListPresenter(NewsListFragment fragment, NewsListAdapter adapter, int category) {
        this.adapter = adapter;
        this.category = category;
        this.fragment = fragment;
    }

    public boolean isLoading() {
        return loading;
    }

    // 初始化可能会用的回调接口
    public void firstGet() {
        loading = true;
        NewsHandler.getHandler().sendRefreshRequest(category, NEWS_NUM, new ResultListener() {
            @Override
            public void onSuccess(LinkedList<News> newsList, int newsNum) {
                loading = false;
                if (newsNum != 0) {
                    adapter.setNewsList(newsList);
                    adapter.notifyItemRangeInserted(0, newsNum);
                }
            }

            @Override
            public void onFailure(int code) {
                loading = false;
                fragment.initFailure();
            }
        });
    }

    // 加载更多新闻，设置回调函数
    public void getMoreNews() {
        loading = true;
        NewsHandler.getHandler().sendLoadRequest(category, NEWS_NUM, new ResultListener() {
            @Override
            public void onSuccess(LinkedList<News> newsList, int newsNum) {
                loading = false;
                if (newsNum != 0) {
                    adapter.setNewsList(newsList);
                    adapter.notifyItemRangeInserted(newsList.size()-newsNum, newsNum);
                }
                adapter.setFooterVisibility(false);
                fragment.onSuccess(newsNum, NewsListFragment.GET_MORE);
            }

            @Override
            public void onFailure(int code) {
                loading = false;
                adapter.setFooterVisibility(false);
                fragment.onFailure();
            }
        });
    }

    // 获取最新新闻，设置回调函数
    public void getLatestNews() {
        loading = true;
        NewsHandler.getHandler().sendRefreshRequest(category, NEWS_NUM, new ResultListener() {
            @Override
            public void onSuccess(LinkedList<News> newsList, int newsNum) {
                loading = false;
                if (newsNum != 0) {
                    adapter.setNewsList(newsList);
                    adapter.notifyItemRangeInserted(0, newsNum);
                }
                fragment.onSuccess(newsNum, NewsListFragment.GET_NEW);
            }

            @Override
            public void onFailure(int code) {
                loading = false;
                fragment.onFailure();
            }
        });

    }

    public void openNewsDetail(News news) {

    }
}
