package com.sanmiao.wypread.bean;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/17 0017.
 * 类说明{}
 */
public class Books {
    String bookID;
    String imgUrl;
    String name;
    String writer;
    String className;
    String ClassifyTitle;

    public String getClassifyTitle() {
        return ClassifyTitle;
    }

    public void setClassifyTitle(String classifyTitle) {
        this.ClassifyTitle = classifyTitle;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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
}
