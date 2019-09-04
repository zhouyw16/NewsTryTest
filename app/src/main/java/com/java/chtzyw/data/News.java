package com.java.chtzyw.data;

import java.io.Serializable;
import java.util.List;

public class News implements Serializable {
    private String image;
    private String publishTime;
    private List<Keywords> keywords;
    private String language;
    private String video;
    private String title;
    private List<When> when;
    private String content;
    private List<Persons> persons;
    private String newsID;
    private String crawlTime;
    private List<Organizations> organizations;
    private String publisher;
    private List<Locations> locations;
    private List<Where> where;
    private String category;
    private List<Who> who;
    private transient boolean hasRead=false;

    public String getImage() {
        return image;
    }
    public String getCover(){
        String[] images=image.split(",");
        if(images.length==0||images[0].isEmpty())
            return null;
        else
            return images[0];
    }
    public String[] getImages(){
        String[] images=image.split(",");
        if(images.length==0||images[0].isEmpty())
            return null;
        else
            return images;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getPublishTime() {
        return publishTime;
    }
    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
    public List<Keywords> getKeywords() {
        return keywords;
    }
    public void setKeywords(List<Keywords> keywords) {
        this.keywords = keywords;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getVideo() {
        return video;
    }
    public void setVideo(String video) {
        this.video = video;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public List<When> getWhen(){
        return when;
    }
    public void setWhen(List<When> when){
        this.when=when;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    public List<Persons> getPersons() {
        return persons;
    }
    public void setPersons(List<Persons> persons) {
        this.persons = persons;
    }
    public String getNewsID() {
        return newsID;
    }
    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }
    public String getCrawlTime() {
        return crawlTime;
    }
    public void setCrawlTime(String crawlTime) {
        this.crawlTime = crawlTime;
    }
    public List<Organizations> getOrganizations() {
        return organizations;
    }
    public void setOrganizations(List<Organizations> organizations) {
        this.organizations = organizations;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public List<Locations> getLocations() {
        return locations;
    }
    public void setLocations(List<Locations> locations) {
        this.locations = locations;
    }
    public List<Where> getWhere() {
        return where;
    }
    public void setWhere(List<Where> where) {
        this.where = where;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public List<Who> getWho() {
        return who;
    }
    public void setWho(List<Who> who) {
        this.who = who;
    }
    public boolean getHasRead(){
        return hasRead;
    }
    public void setHasRead(boolean hasRead){
        this.hasRead=hasRead;
    }
}

class Keywords implements Serializable{
    private String score;
    private String word;
}

class When implements Serializable{
    private String score;
    private String word;
    public String getScore(){
        return score;
    }
    public void setScore(String score){
        this.score=score;
    }
    public String getWord(){
        return word;
    }
    public void setWord(String word){
        this.word=word;
    }
}

class Persons implements Serializable{
    private String count;
    private String linkedURL;
    private String mention;
}

class Organizations implements Serializable{
    private String count;
    private String linkedURL;
    private String mention;
}

class Locations implements Serializable{
    private String lng;
    private String count;
    private String linkedURL;
    private String lat;
    private String mention;
}

class Where implements Serializable{
    private String score;
    private String word;
}

class Who implements Serializable{
    private String score;
    private String word;
}