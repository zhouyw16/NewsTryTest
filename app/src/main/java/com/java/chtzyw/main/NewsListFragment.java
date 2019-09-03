package com.java.chtzyw.main;


import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.java.chtzyw.R;
import com.java.chtzyw.data.News;
import com.java.chtzyw.data.NewsHandler;
import com.java.chtzyw.data.ResultListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class NewsListFragment extends Fragment {
    private static final int NEWS_NUM = 15;

//    private static final String ARG_KEYWORD = "keyWord";
    private static final String ARG_CATEGORY = "mCategory";

    private int mCategory;
//    private String mKeyWord;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private NewsListAdapter mAdapter;


    public NewsListFragment() {}

    public static NewsListFragment newInstance(int idx, String keyWord) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_KEYWORD, keyWord);
        args.putInt(ARG_CATEGORY, idx);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mKeyWord = getArguments().getString(ARG_KEYWORD);
            mCategory = getArguments().getInt(ARG_CATEGORY);
        }
        mAdapter = new NewsListAdapter(getContext(), mCategory);
        mAdapter.setOnItemClickListener((View itemView, int position) -> {
            Toast.makeText(getActivity(), "假装打开了新闻页", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.news_list, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        
        swipeRefreshLayout.setOnRefreshListener(() -> {
                NewsHandler.getHandler().sendRefreshRequest(mCategory, NEWS_NUM,
                        new ResultListener() {
                            @Override
                            public void onSuccess(LinkedList<News> newsList, int newsNum) {
                                mAdapter.notifyItemRangeInserted(0, newsNum);
                                swipeRefreshLayout.setRefreshing(false);
                                recyclerView.smoothScrollToPosition(0);
                            }

                            @Override
                            public void onFailure(int code) {
                                Toast.makeText(getContext(), "refresh failed!", Toast.LENGTH_SHORT).show();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });

        });

        recyclerView = view.findViewById(R.id.recycler_view);

        layoutManager = new ScrollSpeedLinearLayoutManger(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(divider);
        recyclerView.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                new Handler().postDelayed(() -> {
                    Toast.makeText(getActivity(), "假装加载了一条数据", Toast.LENGTH_SHORT).show();
                    // 加载完数据设置为不刷新状态，将下拉进度收起来
                    swipeRefreshLayout.setRefreshing(false);
                    List<News> list = new ArrayList<>();
                    for (int i = 0; i < 15; i++) {
                        News news = new News();
                        news.setTitle("more news" + i);
                        list.add(news);
                    }
                    mAdapter.getMoreNews(list);
                }, 1200);
            }
        });
        return view;
    }

    // 下拉刷新的监听器
    public abstract class OnLoadMoreListener extends RecyclerView.OnScrollListener {
        private int countItem;
        private int lastItem;
        private boolean isScolled = false;//是否可以滑动
        private RecyclerView.LayoutManager layoutManager;

        /**
         * 加载回调方法
         * @param countItem 总数量
         * @param lastItem  最后显示的position
         */
        protected abstract void onLoading(int countItem, int lastItem);

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //拖拽或者惯性滑动时isScolled设置为true
            if (newState == SCROLL_STATE_DRAGGING || newState == SCROLL_STATE_SETTLING) {
                isScolled = true;
            } else { isScolled = false; }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            layoutManager = recyclerView.getLayoutManager();
            countItem = layoutManager.getItemCount();
            lastItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            if (isScolled && countItem != lastItem && lastItem == countItem - 1) {
                onLoading(countItem, lastItem);
            }
        }
    }


    // 设置自动滑动速度的类，重写了LinearLayoutManger中的几个方法
    private class ScrollSpeedLinearLayoutManger extends LinearLayoutManager {
        private float MILLISECONDS_PER_INCH = 0.03f;
        private Context context;

        ScrollSpeedLinearLayoutManger(Context context) {
            super(context);
            this.context = context;
            setSpeedSlow();
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView,RecyclerView.State state, int position) {
            LinearSmoothScroller linearSmoothScroller =
                    new LinearSmoothScroller(recyclerView.getContext()) {
                        @Override
                        public PointF computeScrollVectorForPosition(int targetPosition) {
                            return ScrollSpeedLinearLayoutManger.this
                                    .computeScrollVectorForPosition(targetPosition);
                        }

                        //This returns the milliseconds it takes to
                        //scroll one pixel.
                        @Override
                        protected float calculateSpeedPerPixel
                        (DisplayMetrics displayMetrics) {
                            return MILLISECONDS_PER_INCH / displayMetrics.density;
                            //返回滑动一个pixel需要多少毫秒
                        }

                        @Override
                        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                            return boxStart-viewStart;
                        }
                    };
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void setSpeedSlow() {
            //自己在这里用density去乘，希望不同分辨率设备上滑动速度相同
            //0.3f是自己估摸的一个值，可以根据不同需求自己修改
            MILLISECONDS_PER_INCH = context.getResources().getDisplayMetrics().density * 0.3f;
        }

        public void setSpeedFast() {
            MILLISECONDS_PER_INCH = context.getResources().getDisplayMetrics().density * 0.03f;
        }
    }

}
