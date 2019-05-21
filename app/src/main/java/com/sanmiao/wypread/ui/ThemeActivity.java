package com.sanmiao.wypread.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.StatusBarCompat;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/6 0006.
 * 类说明{主题换肤}
 */

public class ThemeActivity extends BaseActivity {
    @InjectView(R.id.lan_bg)
    TextView lanBg;
    @InjectView(R.id.lan_text)
    TextView lanText;
    @InjectView(R.id.difen_bg)
    TextView difenBg;
    @InjectView(R.id.difen_text)
    TextView difenText;
    @InjectView(R.id.bohong_bg)
    TextView bohongBg;
    @InjectView(R.id.bohong_text)
    TextView bohongText;
    @InjectView(R.id.moren)
    TextView moren;
    @InjectView(R.id.moren_text)
    TextView morenText;
    @InjectView(R.id.caolv_bg)
    TextView caolvBg;
    @InjectView(R.id.caolv_text)
    TextView caolvText;
    @InjectView(R.id.yanzhi_bg)
    TextView yanzhiBg;
    @InjectView(R.id.yanzhi_text)
    TextView yanzhiText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        setThemeCheck();

    }
    //设置主题选中专状态
    private void setThemeCheck(){
        setDefult();
        int i=SharedPreferenceUtil.getIntData("bgcolor");
        if(i==1){
            difenBg.setSelected(true);
            difenText.setText("当前主题");
            difenText.setSelected(true);
            difenText.setTextColor(getResources().getColor(R.color.difen));
        }else if(i==2){
            bohongBg.setSelected(true);
            bohongText.setText("当前主题");
            bohongText.setSelected(true);
            bohongText.setTextColor(getResources().getColor(R.color.bohong));
        }else if(i==3){
            lanBg.setSelected(true);
            lanText.setText("当前主题");
            lanText.setSelected(true);
            lanText.setTextColor(getResources().getColor(R.color.lan));
        }else if(i==4){
            caolvBg.setSelected(true);
            caolvText.setText("当前主题");
            caolvText.setSelected(true);
            caolvText.setTextColor(getResources().getColor(R.color.caolv));
        }else if(i==5){
            yanzhiBg.setSelected(true);
            yanzhiText.setText("当前主题");
            yanzhiText.setSelected(true);
            yanzhiText.setTextColor(getResources().getColor(R.color.yanzhi));
        }else{
            moren.setSelected(true);
            morenText.setText("当前主题");
            morenText.setSelected(true);
            morenText.setTextColor(getResources().getColor(R.color.basecolor));
        }
    }



    @OnClick({R.id.lan_text,R.id.lan_bg,R.id.difen_bg,R.id.difen_text,R.id.bohong_bg,R.id.bohong_text,
            R.id.moren,R.id.moren_text,R.id.caolv_bg,R.id.caolv_text,R.id.yanzhi_bg,R.id.yanzhi_text})
    public void OnClick(View view){
        setDefult();
        switch (view.getId()){
            case R.id.moren:
            case R.id.moren_text:
                moren.setSelected(true);
                morenText.setText("当前主题");
                morenText.setSelected(true);
                morenText.setTextColor(getResources().getColor(R.color.basecolor));

                Configuration config4 = getBaseContext().getResources().getConfiguration();
                config4.locale = Locale.getDefault();
                getBaseContext().getResources().updateConfiguration(config4
                        , getBaseContext().getResources().getDisplayMetrics());
                SharedPreferenceUtil.SaveData("bgcolor",0);
                break;

            case R.id.difen_bg:
            case R.id.difen_text:
                difenBg.setSelected(true);
                difenText.setText("当前主题");
                difenText.setSelected(true);
                difenText.setTextColor(getResources().getColor(R.color.difen));
                Configuration config2 = getBaseContext().getResources().getConfiguration();
                config2.locale = Locale.US;
                getBaseContext().getResources().updateConfiguration(config2
                        , getBaseContext().getResources().getDisplayMetrics());
                SharedPreferenceUtil.SaveData("bgcolor",1);
                break;
            case R.id.bohong_bg:
            case R.id.bohong_text:
                bohongBg.setSelected(true);
                bohongText.setText("当前主题");
                bohongText.setSelected(true);
                bohongText.setTextColor(getResources().getColor(R.color.bohong));

                Configuration config3 = getBaseContext().getResources().getConfiguration();
                config3.locale = Locale.ENGLISH;
                getBaseContext().getResources().updateConfiguration(config3
                        , getBaseContext().getResources().getDisplayMetrics());
                SharedPreferenceUtil.SaveData("bgcolor",2);
                break;
            case R.id.lan_bg:
            case R.id.lan_text:
                lanBg.setSelected(true);
                lanText.setText("当前主题");
                lanText.setSelected(true);
                lanText.setTextColor(getResources().getColor(R.color.lan));
                Configuration config = getBaseContext().getResources().getConfiguration();
                config.locale = Locale.CANADA;
                getBaseContext().getResources().updateConfiguration(config
                        , getBaseContext().getResources().getDisplayMetrics());
                SharedPreferenceUtil.SaveData("bgcolor",3);
                break;
            case R.id.caolv_bg:
            case R.id.caolv_text:
                caolvBg.setSelected(true);
                caolvText.setText("当前主题");
                caolvText.setSelected(true);
                caolvText.setTextColor(getResources().getColor(R.color.caolv));

                Configuration config5 = getBaseContext().getResources().getConfiguration();
                config5.locale = Locale.JAPAN;
                getBaseContext().getResources().updateConfiguration(config5
                        , getBaseContext().getResources().getDisplayMetrics());
                SharedPreferenceUtil.SaveData("bgcolor",4);
                break;
            case R.id.yanzhi_bg:
            case R.id.yanzhi_text:
                yanzhiBg.setSelected(true);
                yanzhiText.setText("当前主题");
                yanzhiText.setSelected(true);
                yanzhiText.setTextColor(getResources().getColor(R.color.yanzhi));
                Configuration config6 = getBaseContext().getResources().getConfiguration();
                config6.locale = Locale.KOREA;
                getBaseContext().getResources().updateConfiguration(config6
                        , getBaseContext().getResources().getDisplayMetrics());
                SharedPreferenceUtil.SaveData("bgcolor",5);
                break;
        }
        Intent intent=new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//跳转此页销毁其他页面
        startActivity(intent);
    }

    //全部设为未选中
    private void setDefult(){
        lanText.setSelected(false);
        lanBg.setSelected(false);
        difenText.setSelected(false);
        difenBg.setSelected(false);
        bohongText.setSelected(false);
        bohongBg.setSelected(false);
        morenText.setSelected(false);
        moren.setSelected(false);
        caolvText.setSelected(false);
        caolvBg.setSelected(false);
        yanzhiText.setSelected(false);
        yanzhiBg.setSelected(false);
        lanText.setText("蓝");
        difenText.setText("砥粉");
        bohongText.setText("薄红");
        morenText.setText("默认");
        caolvText.setText("草绿");
        yanzhiText.setText("胭脂");
        lanText.setTextColor(getResources().getColor(R.color.black));
        difenText.setTextColor(getResources().getColor(R.color.black));
        bohongText.setTextColor(getResources().getColor(R.color.black));
        morenText.setTextColor(getResources().getColor(R.color.black));
        caolvText.setTextColor(getResources().getColor(R.color.black));
        yanzhiText.setTextColor(getResources().getColor(R.color.black));
    }



    @Override
    public int setBaseView() {
        return R.layout.activity_theme;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "主题换肤";
    }

    @Override
    public boolean showMore() {
        return false;
    }


}
