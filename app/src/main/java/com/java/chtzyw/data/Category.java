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

    public static int getCategoryId(String category){
        switch(category){
            case "推荐":
                return 1;
            case "科技":
                return 2;
            case "教育":
                return 3;
            case "军事":
                return 4;
            case "社会":
                return 5;
            case "文化":
                return 6;
            case "汽车":
                return 7;
            case "体育":
                return 8;
            case "财经":
                return 9;
            case "健康":
                return 10;
            case "娱乐":
                return 11;
            default:
                 return 0;
        }
    }
    public static List<Category> getDefaultCategoryList() {
        ArrayList<Category> list = new ArrayList<>();
        String[] cat = DEFAULT_CATEGORIES;
        for (int i = 0; i < cat.length; i++) {
            list.add(new Category(cat[i], i));
        }
        return list;
    }
}
