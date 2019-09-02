package com.java.chtzyw.main;

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
import com.java.chtzyw.data.Category;

import java.util.List;

public class NewsFragment extends Fragment {

    private TabLayout tabLayout;
    private List<Category> categories = Category.getDefaultCategoryList();
//    private String keyWord = "";
    private ViewPager viewPager;
    private MyPageAdapter pageAdapter;

    public NewsFragment() {}

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageAdapter = new MyPageAdapter(getChildFragmentManager(), categories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.news_page, container, false);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);

        for (int i = 0; i < categories.size(); i++)
            tabLayout.addTab(tabLayout.newTab());

        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

//    public void setKeyword(String keyWord) {
//        this.keyWord = keyWord;
//        pageAdapter.notifyDataSetChanged();
//    }

    private class MyPageAdapter extends FragmentStatePagerAdapter {
        private List<Category> categories;

        MyPageAdapter(FragmentManager fm, List<Category> list) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            categories = list;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return categories.get(position).title;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Category cat = categories.get(position);
            return NewsListFragment.newInstance(cat.idx, cat.title);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            NewsListFragment f = (NewsListFragment) super.instantiateItem(container, position);
//            f.setKeyWord(keyWord);
            return f;
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Nullable
        @Override
        public Parcelable saveState() {
            return null;
        }
    }

}
