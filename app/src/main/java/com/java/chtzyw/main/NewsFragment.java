package com.java.chtzyw.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.java.chtzyw.R;

public class NewsFragment extends Fragment {

    private TabLayout mTabLayout;

    public NewsFragment() {
    }

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.news_page, container, false);
        mTabLayout = view.findViewById(R.id.tab_layout);
        for (int i = 0; i < 10; i++)
            mTabLayout.addTab(mTabLayout.newTab().setText("Item"+i));
        return view;
    }

}
