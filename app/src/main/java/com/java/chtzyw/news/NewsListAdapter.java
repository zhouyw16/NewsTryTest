package com.java.chtzyw.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.java.chtzyw.R;
import com.java.chtzyw.data.ImageOption;
import com.java.chtzyw.data.News;
import com.java.chtzyw.data.NewsHandler;
import com.java.chtzyw.data.TagManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_CONTENT = 0; // 正常内容
    private final static int TYPE_FOOTER = 1;  // 下拉刷新

    private OnItemClickListener itemClickListener;
    private Context currContext;
    private List<News> newsList;
    private boolean showFooter;

    public NewsListAdapter(Context context, int tagId) {
        super();
        currContext = context;
        newsList = NewsHandler.getHandler().sendInitNewsList(tagId);
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    public News getNews(int pos) {
        return newsList.get(pos);
    }

    public void setFooterVisibility(boolean visible) {
        if (showFooter != visible) {
            showFooter = visible;
            if (visible) notifyItemInserted(newsList.size());
            else notifyItemRemoved(newsList.size());
        }
    }

    // 长按弹出的菜单
    public void showPopMenu(View view,final int pos) {
        PopupMenu popupMenu = new PopupMenu(currContext,view);
        popupMenu.getMenuInflater().inflate(R.menu.longclick_news, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener((item) -> {
                if (item.getItemId() == R.id.remove_news_menu_item) {
                    removeItem(pos);
                    Toast.makeText(currContext, "将减少此类新闻推荐", Toast.LENGTH_SHORT).show();
                    TagManager.getI().dislike(getNews(pos).getCategory());
                }
                else {
                    Toast.makeText(currContext, "已收藏本条新闻", Toast.LENGTH_SHORT).show();
                    News news = getNews(pos);
                    TagManager.getI().favour(news.getCategory());
                    // 记录收藏新闻
                    NewsHandler.getHandler().sendFavorSaveRequest(getNews(pos));
                }
                return true;
        });
        popupMenu.show();
    }

    // 删除一个元素
    public void removeItem(int position) {
        NewsHandler.getHandler().sendNewsDeleteRequest(newsList, position);
        this.notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position==newsList.size() && showFooter) {
            return TYPE_FOOTER;
        }
        return TYPE_CONTENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CONTENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.news_item, parent, false);
            return new ItemViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.news_item_footer, parent, false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        if (getItemViewType(pos) == TYPE_FOOTER) return;
        // 设置view的内容
        News news =  newsList.get(pos);
        ((ItemViewHolder) holder).setNews(news);

    }

    @Override
    public int getItemCount() {
        return newsList.size() + (showFooter ? 1 : 0);
    }

    // 设置卡片点击的回调函数
    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    // 卡片点击的回调接口
    public interface OnItemClickListener {
        void onItemClick(News news);
    }

    // 正常卡片的viewholder
    private class ItemViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mTitle, mAuthor, mDate;
        ImageView mImage;
        ItemViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = view.findViewById(R.id.text_title);
            mAuthor = view.findViewById(R.id.text_author);
            mDate = view.findViewById(R.id.text_date);
            mImage = view.findViewById(R.id.image_view);

            // 绑定点击事件，过滤频繁操作
            RxView.clicks(view).throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe((dummy) -> {
                        if (itemClickListener != null) {
                            News news= getNews(getLayoutPosition());
                            news.setHasRead(true);
                            mTitle.setTextColor(currContext.getColor(R.color.colorDetail));
                            itemClickListener.onItemClick(news);
                        }
                    });
            // 绑定长按事件
            RxView.longClicks(view).subscribe((dummy) -> showPopMenu(mView, this.getLayoutPosition()));
        }

        void setNews(News news) {
            mTitle.setText(news.getTitle());
            mAuthor.setText(news.getPublisher());
            mDate.setText(news.getPublishTime());
            setImage(news.getCover());
            int color = news.getHasRead() ? currContext.getColor(R.color.colorDetail)
                    : currContext.getColor(R.color.colorTitle);
            mTitle.setTextColor(color);
        }


        void setImage(String url) {
            if (url == null) {
                mImage.setVisibility(View.GONE);
            }
            else {
                mImage.setVisibility(View.VISIBLE);
                Glide.with(mView).load(url).apply(ImageOption.miniImgOption()).into(mImage);
            }
        }

    }

    // progressbar的viewholder
    private class FooterViewHolder extends RecyclerView.ViewHolder {
        FooterViewHolder(View view) { super(view); }
    }
}
