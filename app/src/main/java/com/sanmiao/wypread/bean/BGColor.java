package com.sanmiao.wypread.bean;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/15 0015.
 * 类说明{}
 */

public class BGColor extends CheckBean {
    int Bg;
    int checkBg;
    int useBg;

    public int getUseBg() {
        return useBg;
    }

    public void setUseBg(int useBg) {
        this.useBg = useBg;
    }

    public BGColor(int bg, int checkBg, int useBg) {
        Bg = bg;
        this.checkBg = checkBg;
        this.useBg = useBg;
    }

    public int getBg() {
        return Bg;
    }

    public void setBg(int bg) {
        Bg = bg;
    }

    public int getCheckBg() {
        return checkBg;
    }

    public void setCheckBg(int checkBg) {
        this.checkBg = checkBg;
    }
}
