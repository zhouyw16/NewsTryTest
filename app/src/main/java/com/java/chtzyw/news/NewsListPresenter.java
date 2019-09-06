package com.java.chtzyw.news;

import android.content.Context;
import android.content.Intent;

import com.java.chtzyw.data.News;
import com.java.chtzyw.data.NewsHandler;
import com.java.chtzyw.data.ResultListener;
import com.java.chtzyw.data.TagManager;

import java.util.LinkedList;

class NewsListPresenter {
    private final static int NEWS_NUM = 15;

    private int tagId;
    private boolean loading = false;

    private NewsListAdapter adapter;
    private NewsListFragment fragment;

    NewsListPresenter(NewsListFragment fragment, NewsListAdapter adapter, int tagId) {
        this.adapter = adapter;
        this.tagId = tagId;
        this.fragment = fragment;
    }

    boolean isLoading() {
        return loading;
    }

    // 初始化可能会用的回调接口
    void firstGet() {
        loading = true;
        NewsHandler.getHandler().sendRefreshRequest(tagId, NEWS_NUM, new ResultListener() {
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
    void getMoreNews() {
        loading = true;
        NewsHandler.getHandler().sendLoadRequest(tagId, NEWS_NUM, new ResultListener() {
            @Override
            public void onSuccess(LinkedList<News> newsList, int newsNum) {
                loading = false;
                if (newsNum != 0) {
                    adapter.setNewsList(newsList);
                    adapter.notifyItemRangeInserted(newsList.size()-newsNum, newsNum);
                }
                fragment.onSuccess(newsNum, NewsListFragment.GET_MORE);
            }

            @Override
            public void onFailure(int code) {
                loading = false;
                fragment.onFailure(NewsListFragment.GET_MORE);
            }
        });
    }

    // 获取最新新闻，设置回调函数
    void getLatestNews() {
        loading = true;
        NewsHandler.getHandler().sendRefreshRequest(tagId, NEWS_NUM, new ResultListener() {
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
                fragment.onFailure(NewsListFragment.GET_NEW);
            }
        });

    }

    void openNewsDetail(Context context, News news) {
        // 增加当前点击分类的权重
        TagManager.getI().looked(news.getCategory());
        NewsHandler.getHandler().sendNewsSaveRequest(news);
        Intent intent=new Intent(context,NewsDetailActivity.class);
        intent.putExtra("news_detail",news);
        context.startActivity(intent);
    }
}
