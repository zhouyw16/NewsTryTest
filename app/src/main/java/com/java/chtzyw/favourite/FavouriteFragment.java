package com.java.chtzyw.favourite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

import cn.jzvd.JzvdStd;

// 收藏页的fragment
public class FavouriteFragment extends Fragment {
    private MyAdapter mAdapter;     // recyclerview的适配器

    public FavouriteFragment() {}
    public static FavouriteFragment newInstance() { return new FavouriteFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MyAdapter(); // 初始化适配器
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        // 配置recyclerview的样式
        RecyclerView recyclerView = view.findViewById(R.id.favourite_list_view);
        LinearLayoutManager layoutManager = new ScrollSpeedLinearLayoutManger(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    // 创建右上角的菜单
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.favourite_toolbar,menu);
    }

    // 右上角菜单的响应事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            AlertDialog dialog=new AlertDialog.Builder(getContext())
                    .setMessage("是否确认删除全部收藏？")
                    .setIcon(R.drawable.ic_favorite)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            NewsHandler.getHandler().sendFavorAllDeleteRequest();
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(getContext(),"收藏已全部清除",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));

        }
        return super.onOptionsItemSelected(item);
    }

    // 自定义的适配器
    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<News> newsList;

        // 构造函数中初始化标签列表
        MyAdapter() {
            super();
            newsList = NewsHandler.getHandler().sendFavorLoadRequest();
        }

        @Override
        public int getItemCount() { return newsList.size(); }

        // 长按弹出的菜单
        void showPopMenu(View view,final int pos) {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.longclick_news, popupMenu.getMenu());

            // 复用一下长按弹出菜单的布局
            popupMenu.getMenu().findItem(R.id.favour_news_menu_item).setVisible(false);
            popupMenu.getMenu().findItem(R.id.remove_news_menu_item).setTitle("取消收藏");

            popupMenu.setOnMenuItemClickListener((item) -> {
                TagManager.getI().dislike(newsList.get(pos).getCategory());
                removeItem(pos);
                Toast.makeText(getContext(), "已取消收藏", Toast.LENGTH_SHORT).show();
                return true;
            });
            popupMenu.show();
        }

        // 删除一个元素
        public void removeItem(int position) {
            NewsHandler.getHandler().sendFavorDeleteRequest(newsList.get(position));
            this.notifyItemRemoved(position);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.news_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {

            // 设置view的内容
            News news =  newsList.get(pos);
            ((ItemViewHolder) holder).setNews(news);
        }

        // 正常卡片的viewholder
        class ItemViewHolder extends RecyclerView.ViewHolder {
            View mView;
            TextView mTitle, mAuthor, mDate;
            ImageView mImage;
            JzvdStd mVideo;
            ItemViewHolder(View view) {
                super(view);
                mView = view;
                mTitle = view.findViewById(R.id.text_title);
                mAuthor = view.findViewById(R.id.text_author);
                mDate = view.findViewById(R.id.text_date);
                mImage = view.findViewById(R.id.image_view);
                mVideo = view.findViewById(R.id.news_video);

                // 绑定点击事件，过滤频繁操作
                RxView.clicks(view).throttleFirst(500, TimeUnit.MILLISECONDS)
                        .subscribe((dummy) -> {
                            News news = newsList.get(getLayoutPosition());
                            Intent intent=new Intent(getContext(), NewsDetailActivity.class);
                            intent.putExtra("news_detail",news);
                            getContext().startActivity(intent);
                        });
                // 绑定长按事件
                RxView.longClicks(view).subscribe((dummy) -> showPopMenu(mView, getLayoutPosition()));
            }

            void setNews(News news) {
                mTitle.setText(news.getTitle());
                mAuthor.setText(news.getPublisher());
                mDate.setText(news.getPublishTime());
                if (ImageOption.noImage) {
                    mVideo.setVisibility(View.GONE);
                    mImage.setVisibility(View.GONE);
                }
                else if (news.getVideo().isEmpty()) {
                    mImage.setVisibility(View.VISIBLE);
                    mVideo.setVisibility(View.GONE);
                    setImage(news.getCover());
                }
                else {
                    mImage.setVisibility(View.GONE);
                    mVideo.setVisibility(View.VISIBLE);
                    mVideo.setUp(news.getVideo(), news.getTitle());
                }
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
                            return FavouriteFragment.ScrollSpeedLinearLayoutManger.this
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
