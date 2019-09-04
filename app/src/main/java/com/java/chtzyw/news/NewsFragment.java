package com.java.chtzyw.news;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.chtzyw.R;
import com.java.chtzyw.data.TagManager;

import java.util.List;

public class NewsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyPageAdapter pageAdapter;

    public NewsFragment() {}

    public static NewsFragment newInstance() { return new NewsFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<TagManager.Tag> tagList = TagManager.getI().getVisibleTagList();
        for (TagManager.Tag tag : tagList) {
            System.out.println(tag.title+tag.idx+tag.isVisible());
        }
        pageAdapter = new MyPageAdapter(getChildFragmentManager(), tagList);
    }

    // 创建视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_page, container, false);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 设置缓存的页面数量，前后各3个
        viewPager.setOffscreenPageLimit(3);
        int num = pageAdapter.getCount();
        for (int i = 0; i < num; i++)
            tabLayout.addTab(tabLayout.newTab());

        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private class MyPageAdapter extends FragmentStatePagerAdapter {
        private List<TagManager.Tag> tagList;

        MyPageAdapter(FragmentManager fm, List<TagManager.Tag> list) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            tagList = list;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) { return tagList.get(position).title; }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            TagManager.Tag cat = tagList.get(position);
            return NewsListFragment.newInstance(cat.idx);
        }

        @Override
        public int getCount() { return tagList.size(); }

        @Override
        public int getItemPosition(@NonNull Object object) { return POSITION_NONE; }

        @Nullable
        @Override
        public Parcelable saveState() { return null; }
    }

}
