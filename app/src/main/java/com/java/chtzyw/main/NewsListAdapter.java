package com.java.chtzyw.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.java.chtzyw.R;
import com.java.chtzyw.data.News;

import java.util.ArrayList;
import java.util.List;

class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnItemClickListener itemClickListener;
    private Context currContext;
    private List<News> newsList = new ArrayList<>();
    private int NUM = 20;

    public NewsListAdapter(Context context) {
        super();
        currContext = context;
        for (int i = 0; i < NUM; i++) {
            newsList.add(new News());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mView;
        TextView mTitle, mAuthor, mDate;
        int mCurrentPosition = -1;
        ImageView mImage;
        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = view.findViewById(R.id.text_title);
            mAuthor = view.findViewById(R.id.text_author);
            mDate = view.findViewById(R.id.text_date);
            mImage = view.findViewById(R.id.image_view);
            test_glide();
            view.setOnClickListener(this);
        }

        private void test_glide() {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(320, 180);
            Glide.with(mView).load(R.drawable.sample_pic).apply(options).into(mImage);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(view, this.getLayoutPosition());
            }
        }
    }
}
