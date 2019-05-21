package com.sanmiao.wypread.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.utils.AppManager;
import com.sanmiao.wypread.utils.GlideCircleTransform;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.StatusBarCompat;
import com.sanmiao.wypread.utils.UtilBox;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Locale;

public abstract class BaseActivity extends FragmentActivity {
    public static int ROUND_MORE = 0x00000001;
    public static int NORMAL_BACK = 0x00000002;
    public static int NORMAL_MORE = 0x00000003;
    public static int ROUND_BACK = 0x00000004;

    private ImageView back,more;
    private FrameLayout fatherView;
    private TextView titleText;
    private RelativeLayout title;
    private View dividerLine;
    private TextView moreText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_base);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//默认不弹出键盘
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        Log.d("BaseActivity", this.getClass().getName());
        //注入界面文件
        initView();
        setThemeColor();
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.basecolor));

    }
    /**设置主题颜色*/
    private void setThemeColor(){
        int i=SharedPreferenceUtil.getIntData("bgcolor");
        if(i==1){
            Configuration config2 = getBaseContext().getResources().getConfiguration();
            config2.locale = Locale.US;
            getBaseContext().getResources().updateConfiguration(config2
                    , getBaseContext().getResources().getDisplayMetrics());
        }else if(i==2){
            Configuration config3 = getBaseContext().getResources().getConfiguration();
            config3.locale = Locale.ENGLISH;
            getBaseContext().getResources().updateConfiguration(config3
                    , getBaseContext().getResources().getDisplayMetrics());
        }else if(i==3){
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.locale = Locale.CANADA;
            getBaseContext().getResources().updateConfiguration(config
                    , getBaseContext().getResources().getDisplayMetrics());
        }else if(i==4){
            Configuration config5 = getBaseContext().getResources().getConfiguration();
            config5.locale = Locale.JAPAN;
            getBaseContext().getResources().updateConfiguration(config5
                    , getBaseContext().getResources().getDisplayMetrics());
        }else if(i==5){
            Configuration config6 = getBaseContext().getResources().getConfiguration();
            config6.locale = Locale.KOREA;
            getBaseContext().getResources().updateConfiguration(config6
                    , getBaseContext().getResources().getDisplayMetrics());
        }else{
            Configuration config5 = getBaseContext().getResources().getConfiguration();
            config5.locale = Locale.getDefault();
            getBaseContext().getResources().updateConfiguration(config5
                    , getBaseContext().getResources().getDisplayMetrics());
        }
    }

    public void moreListener() {
        Toast.makeText(this, "更多", Toast.LENGTH_SHORT).show();
    }

    public void backListener() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        finish();
    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        finish();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilBox.dismissDialog();
        //可以取消同一个tag的
        OkHttpUtils.getInstance().cancelTag(this);//取消以Activity.this作为tag的请求
    }

    private void initView() {
        moreText = (TextView) findViewById(R.id.base_activity_moretext);
        back = (ImageView) findViewById(R.id.base_activity_back);
        dividerLine = findViewById(R.id.base_activity_divider_line);
        fatherView = (FrameLayout) findViewById(R.id.base_activity_father_view);
        more = (ImageView) findViewById(R.id.base_activity_more);
        title = (RelativeLayout) findViewById(R.id.base_activity_title);
        titleText = (TextView) findViewById(R.id.base_activity_title_text);
        fatherView.addView(View.inflate(this,setBaseView(),null));
        if(showTitle() == false){
            dividerLine.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        }else{
            dividerLine.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
        }
        if(showMore() == true){
            moreText.setVisibility(View.VISIBLE);
        }else{
            moreText.setVisibility(View.GONE);
        }

        titleText.setText(setTitleText());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backListener();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreListener();
            }
        });
        moreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moretextListener();
            }
        });
//        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.basecolor));
    }

    public void setTitle(String string){
        titleText.setText(string);
    }

    public void moretextListener() {
        Toast.makeText(this, "更多", Toast.LENGTH_SHORT).show();
    }

    public void showMoreTextVisibility(boolean b){
        if(b == true){
            moreText.setVisibility(View.VISIBLE);
        }else{
            moreText.setVisibility(View.GONE);
        }
        if(more.getVisibility() == View.GONE){
            moreText.setPadding(20,20,20,20);
        }
    }
    public void setMoreText(String text){
        moreText.setText(text);
    }
    public String getMoreText(){
        return moreText.getText().toString();
    }
    /**
     * 设置界面主体
     * @return
     */
    public abstract int setBaseView();

    /**
     * 设置标题是否显示
     * @return
     */
    public abstract boolean showTitle();

    /**
     * 设置标题文字
     * @return
     */
    public abstract String setTitleText();

    public void setTitletext1(String text){
        titleText.setText(text);
    }

    /**
     * 设置更多是否显示
     * @return
     */
    public abstract boolean showMore();

    /**设置更多图片显示*/
    public void showMore1(){
        more.setVisibility(View.VISIBLE);
    }

    /**
     * 显示更多类型圆形或者正常
     * @param type
     * @param Id 图片资源文件
     */
    public void setMore(int type,int Id){
        if(type == ROUND_MORE){
            Glide.with(this).load(Id).transform(new GlideCircleTransform(this)).into(more);
        }else{
            Glide.with(this).load(Id).into(more);
        }
    }

    /**
     * 设置返回类型圆形或者正常
     * @param type
     * @param Id 图片资源文件
     */
    public void setBack(int type,int Id){
        if(type == NORMAL_BACK){
            Glide.with(this).load(Id).transform(new GlideCircleTransform(this)).into(back);
        }else{
            Glide.with(this).load(Id).into(back);
        }
    }

    /**
     * 设置主体背景颜色
     * @param baseColor
     */
    public void setBaseBack(String baseColor){
        fatherView.setBackgroundColor(Color.parseColor(baseColor));
    }

    /**
     * 设置是否显示返回
     * @param showBack
     */
    public void showBack(boolean showBack){
        if(showBack == true)
            back.setVisibility(View.VISIBLE);
        else
            back.setVisibility(View.GONE);
    }

    /**
     * 利用16进制色值设置顶部标题背景颜色
     * @param titleColor
     */
    public void setTitleBack(String titleColor){
        title.setBackgroundColor(Color.parseColor(titleColor));
    }

    /**
     * 设置顶部标题背景图片
     * 会覆盖背景颜色
     * @param id
     */
    public void setTitleBackImg(int id){
        title.setBackgroundResource(id);
    }

    /**
     * 利用资源Id设置顶部标题背景颜色
     * @param id
     */
    public void setTitleBack(int id){
        title.setBackgroundColor(id);
    }

    /**
     * 界面跳转
     * @param cls
     */
    public void goToActivity(Class<?> cls){
        Intent intent = new Intent(this,cls);
        startActivity(intent);
    }

    /**
     * 界面跳转并kill掉本页
     * @param cls
     */
    public void goToActivityFinish(Class<?> cls){
        Intent intent = new Intent(this,cls);
        startActivity(intent);
        finish();
    }

    /**
     * 根据资源文件设置title文字颜色
     * @param id
     */
    public void setTitleTextColor(int id){
        titleText.setTextColor(getResources().getColor(id));
    }

    /**
     * 根据16进制色值设置title文字颜色
     * @param colorString
     */
    public void setTitleTextColor(String colorString){
        titleText.setTextColor(Color.parseColor(colorString));
    }

    /**
     * 设置layout前配置
     */
    private void doBeforeSetcontentView() {
        //设置昼夜主题
//        initTheme();
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(this);
        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 默认着色状态栏
        SetStatusBarColor();
    }
//    /**
//     * 设置主题
//     */
//    private void initTheme() {
//        ChangeModeController.setTheme(this, R.style.DayTheme, R.style.NightTheme);
//    }
    /**
     * 着色状态栏（4.4以上系统有效）
     */
    protected void SetStatusBarColor(){
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.basecolor));
    }
    /**
     * 着色状态栏（4.4以上系统有效）
     */
    protected void SetStatusBarColor(int color){
        StatusBarCompat.setStatusBarColor(this,color);
    }
    /**
     * 沉浸状态栏（4.4以上系统有效）
     */
    protected void SetTranslanteBar(){
        StatusBarCompat.translucentStatusBar(this);
    }

}
