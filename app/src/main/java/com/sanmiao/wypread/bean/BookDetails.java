package com.sanmiao.wypread.bean;

import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/17 0017.
 * 类说明{}
 */
public class BookDetails {
    String bookID;
    String imgUrl;
    String name;
    String writer;
    String ClassifyTitle;
    String lookCount;
    String downCount;
    String collect;
    String intro;
    String fileUrl;
    List<Likes> likes;
    List<SectionList> sectionList;
    String videoUrl;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<SectionList> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<SectionList> sectionList) {
        this.sectionList = sectionList;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getClassifyTitle() {
        return ClassifyTitle;
    }

    public void setClassifyTitle(String classifyTitle) {
        this.ClassifyTitle = classifyTitle;
    }

    public String getLookCount() {
        return lookCount;
    }

    public void setLookCount(String lookCount) {
        this.lookCount = lookCount;
    }

    public String getDownCount() {
        return downCount;
    }

    public void setDownCount(String downCount) {
        this.downCount = downCount;
    }

    public String getCollect() {
        return collect;
    }

    public void setCollect(String collect) {
        this.collect = collect;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public List<Likes> getLikes() {
        return likes;
    }

    public void setLikes(List<Likes> likes) {
        this.likes = likes;
    }
}
