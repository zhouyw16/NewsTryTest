package com.java.chtzyw.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagManager {
    public static class Tag {
        public String title;
        public int idx;
        boolean visible = true;
        public Tag(String title, int idx) { this.title = title; this.idx = idx; }
        public boolean isVisible() {
            return visible;
        }
    }

    private static TagManager instance;

    private int[] tagWeight;
    private List<Tag> tagList;

    private TagManager() {
        tagWeight = new int[12];
        Arrays.fill(tagWeight, 0);
        tagList = getDefaultTagList();
    }

    public static TagManager getI() {
        if (instance == null)
            instance = new TagManager();
        return instance;
    }

    public List<Tag> getVisibleTagList() {
        ArrayList<Tag> list = new ArrayList<>();
        for (int i = 0; i < tagList.size(); i++) {
            if (tagList.get(i).visible)
                list.add(tagList.get(i));
        }
        return list;
    }

    public List<Tag> getSettingTagList() {
        ArrayList<Tag> list = new ArrayList<>();
        for (int i = 2; i < tagList.size(); i++) {
            list.add(tagList.get(i));
        }
        return list;
    }


    public void changeVisibility(int tagId) {
        tagList.get(tagId).visible = !tagList.get(tagId).visible;
    }

    public void favour(String tag) {
        tagWeight[getTagId(tag)] += 3;
    }

    public void dislike(String tag) {
        tagWeight[getTagId(tag)] -= 3;
    }

    public void looked(String tag) {
        tagWeight[getTagId(tag)] += 1;
    }

    public void load() {

    }

    public void save() {

    }

    final public static String[] DEFAULT_TAGLIST = {
            "综合", "推荐", "科技", "教育", "军事", "社会", "文化",
            "汽车", "体育", "财经", "健康", "娱乐" };

    public static int getTagId(String Tag){
        switch(Tag){
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

    public static List<Tag> getDefaultTagList() {
        ArrayList<Tag> list = new ArrayList<>();
        String[] cat = DEFAULT_TAGLIST;
        for (int i = 0; i < cat.length; i++) {
            list.add( new Tag(cat[i], i));
        }
        return list;
    }
}
