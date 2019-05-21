package com.sanmiao.wypread.ui;

import android.os.Bundle;

import com.sanmiao.wypread.R;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/25 0025.
 * 类说明{服务协议}
 */

public class XieYiActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String str = getIntent().getStringExtra("title");
        if("0".equals(str)){
            setTitle("注册协议");
        }else{
            setTitle("服务协议");
        }

    }

    @Override
    public int setBaseView() {
        return R.layout.activity_xieyi;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "服务协议";
    }

    @Override
    public boolean showMore() {
        return false;
    }
}
