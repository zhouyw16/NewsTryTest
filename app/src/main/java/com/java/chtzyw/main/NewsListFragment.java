package com.java.chtzyw.main;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.java.chtzyw.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_KEYWORD = "keyWord";
    private static final String ARG_CATEGORY = "mCategory";

    private int mCategory;
    private String mKeyWord;


    public NewsListFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static NewsListFragment newInstance(int idx, String keyWord) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_KEYWORD, keyWord);
        args.putString(ARG_CATEGORY, String.valueOf(idx));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mKeyWord = getArguments().getString(ARG_KEYWORD);
            mCategory = getArguments().getInt(ARG_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        TextView tv = view.findViewById(R.id.news_list_demo);
        tv.setText(mKeyWord+" demo!");
        return view;
    }

    public void setKeyWord(String keyWord) {
        mKeyWord = keyWord;
        Bundle args = this.getArguments();
        args.putString(ARG_KEYWORD, keyWord);
        this.setArguments(args);
    }

}
