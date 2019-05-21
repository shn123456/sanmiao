package com.sanmiao.wypread.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/3 0003.
 * 类说明{启动页}
 */

public class StartActivity extends BaseActivity {
    @InjectView(R.id.start)
    ImageView start;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        final boolean isLogin = SharedPreferenceUtil.getBooleanData("userLogin");

        int i= SharedPreferenceUtil.getIntData("bgcolor");
       if(i==1){
            start.setImageResource(R.mipmap.qidongye2);
        }else if(i==2){
            start.setImageResource(R.mipmap.qidongye3);
        }else if(i==3){
            start.setImageResource(R.mipmap.qidongye1);
        }else if(i==4){
            start.setImageResource(R.mipmap.qidongye5);
        }else if(i==5){
            start.setImageResource(R.mipmap.qidongye6);
        }else{
            start.setImageResource(R.mipmap.qidongye4);
        }
//        if(i==1){
//            start.setImageResource(R.mipmap.hc1);
//        }else if(i==2){
//            start.setImageResource(R.mipmap.hc2);
//        }else if(i==3){
//            start.setImageResource(R.mipmap.hc3);
//        }else if(i==4){
//            start.setImageResource(R.mipmap.hc4);
//        }else if(i==5){
//            start.setImageResource(R.mipmap.hc5);
//        }else{
//            start.setImageResource(R.mipmap.hc4);
//        }
        Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               if (isLogin){
                   startActivity(new Intent(StartActivity.this,MainActivity.class));
               }else{
                   startActivity(new Intent(StartActivity.this,LoginActivity.class));
               }
                finish();
            }
        },1500);
    }

    @Override
    public int setBaseView() {
        return R.layout.activity_start;
    }

    @Override
    public boolean showTitle() {
        return false;
    }

    @Override
    public String setTitleText() {
        return null;
    }

    @Override
    public boolean showMore() {
        return false;
    }


}
