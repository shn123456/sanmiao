package com.sanmiao.wypread.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sanmiao.wypread.utils.FileUtils;

import java.io.File;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/31 0031.
 * 类说明{}
 */

public class MonitorSysReceiver extends BroadcastReceiver {
    FileUtils fileUtils ;
    @Override
    public void onReceive(Context context, Intent intent){
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
        }
        //接收卸载广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            fileUtils = new FileUtils();
            //卸载时删除本地文件
            File file = new File("/mnt/sdcard/wypread");
            if(file.exists()){
                fileUtils.delete(file);
            }
        }
    }
}
