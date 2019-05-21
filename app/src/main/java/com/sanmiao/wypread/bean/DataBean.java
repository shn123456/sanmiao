package com.sanmiao.wypread.bean;

import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/16 0016.
 * 类说明{}
 */
public class DataBean {
    UserInfoBean userInfo;
    List<CollectionBook> collect_book;
    List<CollectionBook> collect_quite;
    List<CollectionBook> collect_video;
    List<Books> books;
    List<SlideShow>  slideShow;
    List<CollectionBook> recommendBook;
    List<CollectionBook> recommendListen;
    List<CollectionBook> recommendVideo;
    List<Classifies> classifies;
    BookDetails bookDetails;
    boolean update;
    String url;
    String collect;
    String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getCollect() {
        return collect;
    }

    public void setCollect(String collect) {
        this.collect = collect;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BookDetails getBookDetails() {
        return bookDetails;
    }

    public void setBookDetails(BookDetails bookDetails) {
        this.bookDetails = bookDetails;
    }

    public List<Classifies> getClassifies() {
        return classifies;
    }

    public void setClassifies(List<Classifies> classifies) {
        this.classifies = classifies;
    }

    public List<CollectionBook> getRecommendBook() {
        return recommendBook;
    }

    public void setRecommendBook(List<CollectionBook> recommendBook) {
        this.recommendBook = recommendBook;
    }

    public List<CollectionBook> getRecommendListen() {
        return recommendListen;
    }

    public void setRecommendListen(List<CollectionBook> recommendListen) {
        this.recommendListen = recommendListen;
    }

    public List<CollectionBook> getRecommendVideo() {
        return recommendVideo;
    }

    public void setRecommendVideo(List<CollectionBook> recommendVideo) {
        this.recommendVideo = recommendVideo;
    }

    public List<SlideShow> getSlideShow() {
        return slideShow;
    }

    public void setSlideShow(List<SlideShow> slideShow) {
        this.slideShow = slideShow;
    }

    public List<Books> getBooks() {
        return books;
    }

    public void setBooks(List<Books> books) {
        this.books = books;
    }

    public List<CollectionBook> getCollect_video() {
        return collect_video;
    }

    public void setCollect_video(List<CollectionBook> collect_video) {
        this.collect_video = collect_video;
    }

    public List<CollectionBook> getCollect_quite() {
        return collect_quite;
    }

    public void setCollect_quite(List<CollectionBook> collect_quite) {
        this.collect_quite = collect_quite;
    }

    public List<CollectionBook> getCollect_book() {
        return collect_book;
    }

    public void setCollect_book(List<CollectionBook> collect_book) {
        this.collect_book = collect_book;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }
}
