package com.sanmiao.wypread.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.sanmiao.wypread.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/13 0013.
 * 类说明{二维码扫描页面}
 */

public class QRCodeActivity extends BaseActivity {
    @InjectView(R.id.qrcode)
    DecoratedBarcodeView qrcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        //重要代码，初始化捕获
        captureManager = new CaptureManager(this,qrcode);
        captureManager.initializeFromIntent(getIntent(),savedInstanceState);
        captureManager.decode();

    }

    private CaptureManager captureManager;

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return qrcode.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public int setBaseView() {
        return R.layout.activity_qrcode;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "二维码扫描";
    }

    @Override
    public boolean showMore() {
        return false;
    }

}
