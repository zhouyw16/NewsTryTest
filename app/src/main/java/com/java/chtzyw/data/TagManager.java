package com.java.chtzyw.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TagManager {
    public static class Tag {
        public String title;
        public int idx;
        public boolean visible = true;
        public int weight = 0;
        public Tag(String title, int idx) { this.title = title; this.idx = idx; }
        public Tag(String title, int idx, int weight) {this.title = title; this.idx = idx; this.weight = weight;}
        public boolean isVisible() {
            return visible;
        }
    }

    public class TagJson{
        public List<Tag> tagJsonList;
    }

    private static TagManager instance;
    private List<Tag> tagList;

    private TagManager() {
        load();
    }

    public static TagManager getI() {
        if (instance == null)
            instance = new TagManager();
        return instance;
    }

    public ArrayList<Integer> getRecommendTagList() {
        ArrayList<Tag> tmp = new ArrayList<>();
        for (Tag tag : tagList) {
            if (tag.visible && tag.idx > 1)
                tmp.add(new Tag(tag.title, tag.idx, tag.weight));
        }
        tmp.sort((t1, t2) -> t2.weight - t1.weight);
        ArrayList<Integer> res = new ArrayList<>();
        for (Tag tag : tmp)
            res.add(tag.idx);
        if (res.size() == 0) res.add(0);
        return res;
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
        tagList.get(getTagId(tag)).weight += 3;
    }

    public void dislike(String tag) {
        tagList.get(getTagId(tag)).weight -= 3;
    }

    public void looked(String tag) {
        tagList.get(getTagId(tag)).weight += 1;
    }

    public void load() {
        tagList=NewsHandler.getHandler().sendSettingLoadRequest();
    }

    public void save() {
        TagJson tagJson=new TagJson();
        tagJson.tagJsonList=tagList;
        NewsHandler.getHandler().sendSettingSaveRequest(tagJson);
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
