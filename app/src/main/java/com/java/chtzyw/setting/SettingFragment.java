package com.java.chtzyw.setting;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.java.chtzyw.R;
import com.java.chtzyw.data.ImageOption;
import com.java.chtzyw.data.NewsHandler;
import com.java.chtzyw.data.TagManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

// 设置页的fragment
public class SettingFragment extends Fragment {
    private MyAdapter myAdapter;     // recyclerview的适配器

    public SettingFragment() {}
    public static SettingFragment newInstance() { return new SettingFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myAdapter = new MyAdapter(); // 初始化适配器
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {           //退出前
        TagManager.getI().save();       //保存分类设置
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        CardView clearCache = view.findViewById(R.id.clear_cache_button);
        RxView.clicks(clearCache).throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe((dummy) -> {
                    Toast.makeText(getContext(), "已清空本地缓存", Toast.LENGTH_SHORT).show();
                    NewsHandler.getHandler().sendDeleteCacheRequest();
                });

        CardView noImage = view.findViewById(R.id.no_image_button);
        TextView noImageText = view.findViewById(R.id.no_image_button_text);
        if (ImageOption.noImage) {
            Toast.makeText(getContext(), "设置为无图模式", Toast.LENGTH_SHORT).show();
            noImage.setCardBackgroundColor(getContext().getColor(R.color.colorTagSelectedBg));
            noImageText.setTextColor(getContext().getColor(R.color.colorTagSelectedText));
        }
        else {
            Toast.makeText(getContext(), "取消无图模式", Toast.LENGTH_SHORT).show();
            noImage.setCardBackgroundColor(getContext().getColor(R.color.colorTagBg));
            noImageText.setTextColor(getContext().getColor(R.color.colorTagText));
        }
        RxView.clicks(noImage).throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe((dummy) -> {
                    ImageOption.noImage = !ImageOption.noImage;
                    if (ImageOption.noImage) {
                        Toast.makeText(getContext(), "设置为无图模式", Toast.LENGTH_SHORT).show();
                        noImage.setCardBackgroundColor(getContext().getColor(R.color.colorTagSelectedBg));
                        noImageText.setTextColor(getContext().getColor(R.color.colorTagSelectedText));
                    }
                    else {
                        Toast.makeText(getContext(), "取消无图模式", Toast.LENGTH_SHORT).show();
                        noImage.setCardBackgroundColor(getContext().getColor(R.color.colorTagBg));
                        noImageText.setTextColor(getContext().getColor(R.color.colorTagText));
                    }
                });

        // 使用recyclerview管理网格布局
        RecyclerView recyclerView = view.findViewById(R.id.tag_setting_list);
        recyclerView.setAdapter(myAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    // 创建右上角的菜单
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    // 自定义的适配器
    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<TagManager.Tag> tagList;

        // 构造函数中初始化标签列表
        MyAdapter() { super(); tagList = TagManager.getI().getSettingTagList(); }

        @Override
        public int getItemCount() { return tagList.size(); }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CardView view =(CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tag_setting_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TagManager.Tag tag = tagList.get(position);
            MyViewHolder item = (MyViewHolder) holder;
            item.mText.setText(tag.title);
            // 设置不同状态的标签样式
            if (tag.isVisible()) {
                item.mView.setCardBackgroundColor(getContext().getColor(R.color.colorTagSelectedBg));
                item.mText.setTextColor(getContext().getColor(R.color.colorTagSelectedText));
            }
            else {
                item.mView.setCardBackgroundColor(getContext().getColor(R.color.colorTagBg));
                item.mText.setTextColor(getContext().getColor(R.color.colorTagText));
            }

        }

        // 辅助函数，根据元素位置返回标签，供viewHolder回调
        private TagManager.Tag getTag(int pos) { return tagList.get(pos); }

        class MyViewHolder extends RecyclerView.ViewHolder {
            CardView mView;
            TextView mText;

            MyViewHolder(CardView view) {
                super(view);
                mView = view;
                mText =view.findViewById(R.id.tag_setting_card);
                // 绑定标签点击事件的回调函数
                RxView.clicks(mView).throttleFirst(500, TimeUnit.MILLISECONDS)
                        .subscribe((dummy) -> {
                            TagManager.Tag tag = getTag(getLayoutPosition());
                            TagManager.getI().changeVisibility(tag.idx);
                            if (tag.isVisible()) {
                                mView.setCardBackgroundColor(getContext().getColor(R.color.colorTagSelectedBg));
                                mText.setTextColor(getContext().getColor(R.color.colorTagSelectedText));
                                Toast.makeText(getContext(), "已添加"+tag.title+"分类", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                mView.setCardBackgroundColor(getContext().getColor(R.color.colorTagBg));
                                mText.setTextColor(getContext().getColor(R.color.colorTagText));
                                Toast.makeText(getContext(), "已删除"+tag.title+"分类", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}
