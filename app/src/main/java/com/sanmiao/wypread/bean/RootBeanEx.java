package com.sanmiao.wypread.bean;

public class RootBeanEx {
    int code;
    String emsg;
    DataBeanEx Date;

    public int getcode() {
        return code;
    }

    public void setcode(int resultCode) {
        code = resultCode;
    }

    public String getemsg() {
        return emsg;
    }

    public void setemsg(String msg) {
        emsg = msg;
    }

    public DataBeanEx getDate() {
        return Date;
    }

    public void setDate(DataBeanEx data) {
        Date = data;
    }
}
