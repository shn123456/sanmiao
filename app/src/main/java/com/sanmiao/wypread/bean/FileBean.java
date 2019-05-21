package com.sanmiao.wypread.bean;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/20 0020.
 * 类说明{}
 */

public class FileBean extends CheckBean {
    String name;
    String path;
    String img;
    long id;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
