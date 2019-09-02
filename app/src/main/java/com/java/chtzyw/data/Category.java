package com.java.chtzyw.data;

import java.util.ArrayList;
import java.util.List;

public class Category {
    public String title;
    public int idx;

    public Category(String title, int idx) {
        this.title = title; this.idx = idx;
    }

    final public static String[] DEFAULT_CATEGORIES = {
            "综合", "推荐", "科技", "教育", "军事", "社会", "文化",
            "汽车", "体育", "财经", "健康", "娱乐" };

    public static List<Category> getDefaultCategoryList() {
        ArrayList<Category> list = new ArrayList<>();
        String[] cat = DEFAULT_CATEGORIES;
        for (int i = 0; i < cat.length; i++) {
            list.add(new Category(cat[i], i));
        }
        return list;
    }
}
