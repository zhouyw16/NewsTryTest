package com.java.chtzyw.main;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.java.chtzyw.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsListFragment extends Fragment {

//    private static final String ARG_KEYWORD = "keyWord";
    private static final String ARG_CATEGORY = "mCategory";

    private int mCategory;
//    private String mKeyWord;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private NewsListAdapter mAdapter;


    public NewsListFragment() {}

    // TODO: Rename and change types and number of parameters
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
        mAdapter = new NewsListAdapter(getContext());

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

                // 开始刷新，设置当前为刷新状态
                //swipeRefreshLayout.setRefreshing(true);

                // 这里是主线程
                // 一些比较耗时的操作，比如联网获取数据，需要放到子线程去执行

                new Handler().postDelayed(() -> {
                        Toast.makeText(getActivity(), "刷新了一条数据", Toast.LENGTH_SHORT).show();
                        // 加载完数据设置为不刷新状态，将下拉进度收起来
                        swipeRefreshLayout.setRefreshing(false);
                }, 1200);
        });

        recyclerView = view.findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(divider);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return view;
    }

//    public void setKeyWord(String keyWord) {
//        mKeyWord = keyWord;
//        Bundle args = this.getArguments();
//        args.putString(ARG_KEYWORD, keyWord);
//        this.setArguments(args);
//    }

}
