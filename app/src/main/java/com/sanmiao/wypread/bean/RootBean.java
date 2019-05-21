package com.sanmiao.wypread.bean;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/16 0016.
 * 类说明{}
 */

public class RootBean {
    int ResultCode;
    String Msg;
    String Total;
    DataBean Data;

    public int getResultCode() {
        return ResultCode;
    }

    public void setResultCode(int resultCode) {
        ResultCode = resultCode;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public DataBean getData() {
        return Data;
    }

    public void setData(DataBean data) {
        Data = data;
    }
}
