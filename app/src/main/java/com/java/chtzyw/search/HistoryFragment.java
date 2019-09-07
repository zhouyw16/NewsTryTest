package com.java.chtzyw.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.java.chtzyw.R;
import com.java.chtzyw.data.ImageOption;
import com.java.chtzyw.data.News;
import com.java.chtzyw.data.NewsHandler;
import com.java.chtzyw.data.TagManager;
import com.java.chtzyw.news.NewsDetailActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

// 历史记录的fragment
public class HistoryFragment extends Fragment {
    private MyAdapter mAdapter;     // recyclerview的适配器
    private OnSearchListener searchListener;

    public HistoryFragment() {}
    public static HistoryFragment newInstance() { return new HistoryFragment(); }

    public interface OnSearchListener{
        void onSearch(String query);
    }

    public void setOnSearchListen(OnSearchListener listener){
        this.searchListener=listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MyAdapter(); // 初始化适配器

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // 配置recyclerview的样式
        RecyclerView recyclerView = view.findViewById(R.id.history_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    // 自定义的适配器
    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<String> historyList;

        // 构造函数中初始化标签列表
        MyAdapter() {
            super();
            historyList = NewsHandler.getHandler().sendHistoryLoadRequest();
        }

        @Override
        public int getItemCount() { return historyList.size(); }

        // 删除一个元素
        public void removeItem(int position) {
            NewsHandler.getHandler().sendHistoryDeleteRequest(position);
            this.notifyItemRemoved(position);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {

            // 设置view的内容
            String record=historyList.get(pos);
            ((ItemViewHolder) holder).setRecord(record);
        }

        // 正常卡片的viewholder
        class ItemViewHolder extends RecyclerView.ViewHolder {
            View mView;
            TextView mRecord;
            ImageView mClose;

            ItemViewHolder(View view) {
                super(view);
                mView = view;
                mRecord=view.findViewById(R.id.history_record);
                mClose=view.findViewById(R.id.history_close);

                // 绑定点击事件，过滤频繁操作
                RxView.clicks(mRecord).throttleFirst(500, TimeUnit.MILLISECONDS)
                        .subscribe((dummy) -> {
                            searchListener.onSearch(mRecord.getText().toString());
                        });

                RxView.clicks(mClose).throttleFirst(500, TimeUnit.MILLISECONDS)
                        .subscribe((dummy) -> {
                            removeItem(getLayoutPosition());
                        });
            }

            void setRecord(String record) {
                mRecord.setText(record);
            }

            void setImage(String url) {
            }
        }
    }
}
