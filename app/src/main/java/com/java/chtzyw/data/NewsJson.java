package com.java.chtzyw.data;

import java.util.List;

public class NewsJson {
    private String pageSize;
    private String total;
    private List<News> data;
    private String currentPage;
    public String getPageSize(){
        return pageSize;
    }
    public String getTotal(){
        return total;
    }
    public List<News> getData() {
        return data;
    }
    public String getCurrentPage() {
        return currentPage;
    }
    public void setPageSize(String pageSize){
        this.pageSize=pageSize;
    }
    public void setTotal(String total){
        this.total=total;
    }
    public void setData(List<News> data){
        this.data=data;
    }
    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }
}
