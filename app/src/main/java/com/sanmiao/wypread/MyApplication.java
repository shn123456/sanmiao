package com.sanmiao.wypread;

import android.app.Application;
import android.content.Intent;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.dao.DaoManager;
import com.sanmiao.wypread.service.MonitorSysReceiver;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/4 0004.
 * 类说明{}
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferenceUtil.init(this,"READ");
        DaoManager manager= new DaoManager();
        manager.initManager(getApplicationContext());
        String IPAndPort = SharedPreferenceUtil.getStringData("IpAndPort");
        MyUrl.SetUrl(IPAndPort);
        // 将“12345678”替换成您申请的APPID，申请地址：http://www.xfyun.cn
        // 请勿在“=”与appid之间添加任何空字符或者转义符
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID +"=59196507");

        Intent startIntent = new Intent(this, MonitorSysReceiver.class);
        startService(startIntent);
    }
}
