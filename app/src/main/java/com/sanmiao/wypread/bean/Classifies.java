package com.sanmiao.wypread.bean;

import com.iflytek.cloud.thirdparty.S;

import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/17 0017.
 * 类说明{}
 */
public class Classifies {
    String classifyID;
    String imgUrl;
    String name;
    List<CollectionBook> items;

    public String getClassifyID() {
        return classifyID;
    }

    public void setClassifyID(String classifyID) {
        this.classifyID = classifyID;
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

    public List<CollectionBook> getItems() {
        return items;
    }

    public void setItems(List<CollectionBook> items) {
        this.items = items;
    }
}
