package com.java.chtzyw.setting;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.java.chtzyw.R;
import com.java.chtzyw.data.TagManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SettingFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;

    public SettingFragment() {}

    public static SettingFragment newInstance() { return new SettingFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myAdapter = new MyAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        recyclerView = view.findViewById(R.id.tag_setting_list);
        recyclerView.setAdapter(myAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }


    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<TagManager.Tag> tagList;

        public MyAdapter() {
            super();
            tagList = TagManager.getI().getSettingTagList();
        }

        @Override
        public int getItemCount() {
            return tagList.size();
        }

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
            if (tag.isVisible()) {
                item.mView.setCardBackgroundColor(getContext().getColor(R.color.colorTagSelectedBg));
                item.mText.setTextColor(getContext().getColor(R.color.colorTagSelectedText));
            }
            else {
                item.mView.setCardBackgroundColor(getContext().getColor(R.color.colorTagBg));
                item.mText.setTextColor(getContext().getColor(R.color.colorTagText));
            }

        }

        private TagManager.Tag getTag(int pos) {
            return tagList.get(pos);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            CardView mView;
            TextView mText;
            MyViewHolder(CardView view) {
                super(view);
                mView = view;
                mText =view.findViewById(R.id.tag_setting_card);
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
