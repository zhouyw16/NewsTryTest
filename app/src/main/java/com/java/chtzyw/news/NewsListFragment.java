package com.java.chtzyw.news;


import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Toast;

import com.java.chtzyw.R;

public class NewsListFragment extends Fragment {

    public  static final int GET_NEW = 1;  // 上拉刷新
    public  static final int GET_MORE = 2; // 下拉加载
    private static final String ARG_TAGID = "mTag";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private NewsListAdapter mAdapter;      // recyclerview的适配器
    private NewsListPresenter mPresenter;  // 新闻事务管理类
    private Context mContext;

    private boolean isGettingMore = false; // 用于适配上拉刷新的状态

    private boolean gestureUp = false;


    public NewsListFragment() {}

    public static NewsListFragment newInstance(int tagId) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAGID, tagId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int tagId = -1;
        if (getArguments() != null) {
            tagId = getArguments().getInt(ARG_TAGID);
        }

        mAdapter = new NewsListAdapter(getContext(), tagId);
        mPresenter = new NewsListPresenter(this, mAdapter, tagId);
        mAdapter.setOnItemClickListener((news)->mPresenter.openNewsDetail(getContext(), news));
        mContext = getContext();

        // 如果本地没有缓存，则初始先加载一批新闻
        if (mAdapter.getItemCount() == 0)
            mPresenter.firstGet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 配置swiperefreshlayout的样式
        View view = inflater.inflate(R.layout.news_list, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        // 下拉刷新监听器
        swipeRefreshLayout.setOnRefreshListener(() -> mPresenter.getLatestNews());

        // 配置recyclerview的样式
        recyclerView = view.findViewById(R.id.news_list_view);
        layoutManager = new ScrollSpeedLinearLayoutManger(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        // 上拉加载监听器
        recyclerView.addOnScrollListener(new OnLoadMoreListener());

        /* 监听recyclerview的上划手势 */
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            private int lastY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        recyclerView.performClick();
                        int y = (int) event.getY();
                        if ( lastY - y > ViewConfiguration.get(mContext).getScaledTouchSlop())
                            gestureUp = true;
                        else gestureUp = false;
                        break;
                }
                return false;
            }
        });

        return view;
    }

    // 加载新闻成功，更新ui
    void onSuccess(int newsNum, int mode) {
        String text = newsNum == 0 ? "当前已是最新新闻" : ("刷新了"+newsNum+"条新闻");
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        });

        if (newsNum != 0 && mode == GET_NEW)
            recyclerView.smoothScrollToPosition(0);
        else if (mode == GET_MORE)
            mAdapter.setFooterVisibility(false);
        swipeRefreshLayout.setRefreshing(false);
    }

    // 加载新闻失败，发出通知
    void onFailure(int mode) {
        System.out.println("onFailure: ");
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getContext(), "刷新失败", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            if (mode == GET_MORE) {
                new Handler().postDelayed(() -> {
                    mAdapter.setFooterVisibility(false);
                    isGettingMore = false;
                }, 100);
            }
        });
    }

    //  初始加载新闻失败
    void initFailure() {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
        });
    }

    // 上拉刷新的监听器，重写了recyclerview的两个滑动监听器
    private class OnLoadMoreListener extends RecyclerView.OnScrollListener {
        private int lastItem; // 当前最下面完整显示的卡片

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            // 如果滑动到最后一张卡片，并且没有在加载，则申请加载新闻
            if (!mPresenter.isLoading() && newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastItem == mAdapter.getItemCount() - 1 && gestureUp) {
                gestureUp = false;
                mAdapter.setFooterVisibility(true);
                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                mPresenter.getMoreNews();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
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
