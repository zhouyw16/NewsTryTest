package com.java.chtzyw.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jakewharton.rxbinding2.view.RxView;
import com.java.chtzyw.R;
import com.java.chtzyw.data.ImageOption;
import com.java.chtzyw.data.News;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    // 弹出长按删除的菜单
    public void showPopMenu(View view,final int pos){
        PopupMenu popupMenu = new PopupMenu(currContext,view);
        popupMenu.getMenuInflater().inflate(R.menu.longclick_news, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener((item) -> {
                removeItem(pos); return false;
        });
        popupMenu.setOnDismissListener((menu) -> {
                Toast.makeText(currContext, "关闭PopupMenu", Toast.LENGTH_SHORT).show();
        });
        popupMenu.show();
    }

    // 删除一个元素
    public void removeItem(int position) {
        newsList.remove(position);
        this.notifyItemRemoved(position);
    }

    public void appendNewsList(List<News> list) {
        int pos = newsList.size();
        newsList.addAll(list);
        this.notifyItemRangeChanged(pos, newsList.size());
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        News news =  newsList.get(pos);
        ItemViewHolder item = (ItemViewHolder) holder;
//        item.mTitle.setText(news.getTitle());
//        item.mAuthor.setText(news.getPublisher());
//        item.mDate.setText(news.getPublishTime());
//        item.setImage(news.getImage());

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;
        public TextView mTitle, mAuthor, mDate;
//        int mCurrentPosition = -1;
        ImageView mImage;
        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = view.findViewById(R.id.text_title);
            mAuthor = view.findViewById(R.id.text_author);
            mDate = view.findViewById(R.id.text_date);
            mImage = view.findViewById(R.id.image_view);
            test_glide();
            RxView.clicks(view).throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe((dummy) -> {
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(mView, this.getLayoutPosition());
                        }
                    });
            RxView.longClicks(view).subscribe((dummy) -> showPopMenu(mView, this.getLayoutPosition()));
        }

        public void setImage(String url) {
            if (url == null)
                mImage.setVisibility(View.GONE);
            else {
                mImage.setVisibility(View.VISIBLE);
                Glide.with(mView).load(url).apply(ImageOption.miniImgOption()).into(mImage);
            }
        }

        private void test_glide() {
            String url = "http://5b0988e595225.cdn.sohucs.com/images/20190830/4926e098335446058eb45e43194d8fc4.png";
            Glide.with(mView).load(url).apply(ImageOption.miniImgOption()).into(mImage);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(view, this.getLayoutPosition());
            }
        }
    }
}
