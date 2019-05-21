package com.sanmiao.wypread.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.adapter.VpFragmentAdapter;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.fragment.QuiteDetailsJianjieFragment;
import com.sanmiao.wypread.fragment.QuiteDetailsListFragment;
import com.sanmiao.wypread.utils.GlideUtil;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.UtilBox;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/4 0004.
 * 类说明{听书详情}
 */

public class QuiteDetailsActivity extends BaseActivity {
    @InjectView(R.id.quiteDetails_img)
    ImageView detailsImg;//图片
    @InjectView(R.id.quiteDetails_title)
    TextView detailsTitle;//标题
    @InjectView(R.id.quiteDetails_name)
    TextView detailsName;//主讲人
    @InjectView(R.id.quiteDetails_class)
    TextView detailsClass;//分类
    @InjectView(R.id.quiteDetails_readNum)
    TextView detailsReadNum;//收听量
    @InjectView(R.id.quiteDetails_downNum)
    TextView detailsDownNum;//下载量
    @InjectView(R.id.quiteDetails_jianjie)
    TextView detailsJianjie;//顶部简介
    @InjectView(R.id.quiteDetails_list)
    TextView detailsList;//顶部列表
    @InjectView(R.id.quiteDetails_VP)
    ViewPager detailsVP;
    @InjectView(R.id.quiteDetails_listen)
    TextView detailsListen;//收听
    @InjectView(R.id.quiteDetails_jianjieView)
    View detailsJianjieView;//简介底部线
    @InjectView(R.id.quiteDetails_listView)
    View detailsListView;//列表底部线
    @InjectView(R.id.quiteDetails_collection)
    CheckBox detailsCollection;//收藏
    QuiteDetailsJianjieFragment fragment1;//简介布局
    QuiteDetailsListFragment fragment2;//列表
    List<Fragment> fragmentList = new ArrayList<>();
    String quiteId= "";
    String listionNum = "0";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        detailsCollection.setPadding(20,10,10,10);
        int i= SharedPreferenceUtil.getIntData("bgcolor");
        if(i==1){
            detailsCollection.setButtonDrawable(R.drawable.collection1);
        }else if(i==2){
            detailsCollection.setButtonDrawable(R.drawable.collection2);
        }else if(i==3){
            detailsCollection.setButtonDrawable(R.drawable.collection3);
        }else if(i==4){
            detailsCollection.setButtonDrawable(R.drawable.collection4);
        }else if(i==5){
            detailsCollection.setButtonDrawable(R.drawable.collection5);
        }else{
            detailsCollection.setButtonDrawable(R.drawable.collection);
        }

        //广播
        mBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("setNum");
        registerReceiver(mBroadcastReceiver, intentFilter);


        checkList();
        initData();
        initView();
        detailsVP.setCurrentItem(1);
    }

    private void initView() {
        fragment1 = new QuiteDetailsJianjieFragment();
        fragment2 = new QuiteDetailsListFragment();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        detailsVP.setOffscreenPageLimit(2);
        detailsVP.setAdapter(new VpFragmentAdapter(getSupportFragmentManager(), fragmentList));
        detailsVP.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if(position == 0)
                    checkJianjie();
                else
                    checkList();
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initData() {
        UtilBox.showDialog(this,"加载数据,请稍候");
        HashMap<String,String> map = new HashMap<>();
        map.put("userID",SharedPreferenceUtil.getStringData("userId"));
        map.put("bookID",getIntent().getStringExtra("quiteID"));
        OkHttpUtils.post().url(MyUrl.quiteDetails).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                UtilBox.dismissDialog();
                Toast.makeText(QuiteDetailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean = new Gson().fromJson(response,RootBean.class);
                if(bean.getResultCode() == 0){
                    if(!TextUtils.isEmpty(bean.getData().getBookDetails().getImgUrl()))
                        GlideUtil.ShowImage(QuiteDetailsActivity.this, MyUrl.imgUrl+bean.getData().getBookDetails().getImgUrl(), detailsImg);
                    else
                        detailsImg.setImageResource(R.mipmap.icon_zhanweitu2);

                    listionNum = bean.getData().getBookDetails().getLookCount();
                    detailsTitle.setText(bean.getData().getBookDetails().getName());
                    detailsClass.setText("栏目:" + bean.getData().getBookDetails().getClassifyTitle());
                    detailsName.setText("主讲人:" + bean.getData().getBookDetails().getWriter());
                    detailsReadNum.setText("收听量:" + bean.getData().getBookDetails().getLookCount());
                    detailsDownNum.setText("下载量:" + bean.getData().getBookDetails().getDownCount());
                    if("1".equals( bean.getData().getBookDetails().getCollect())){
                        detailsCollection.setTextColor(getResources().getColor(R.color.basecolor));
                        detailsCollection.setSelected(true);
                        detailsCollection.setText("已收藏");
                    } else{
                        detailsCollection.setTextColor(getResources().getColor(R.color.black));
                        detailsCollection.setSelected(false);
                        detailsCollection.setText("收藏");
                    }

                    String str ="101"+bean.getData().getBookDetails().getBookID();
                    bookId = Integer.valueOf(str);
                    fragment1.initView(bean.getData().getBookDetails().getIntro());
                    fragment2.initData(response);

                    quiteId = bean.getData().getBookDetails().getBookID();
                }
            }
        });
    }
    int bookId = 0;
    int index = 0;
    @OnClick({R.id.quiteDetails_listen, R.id.quiteDetails_collection, R.id.quiteDetails_jianjie, R.id.quiteDetails_list})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.quiteDetails_listen:
                detailsVP.setCurrentItem(1);
                checkList();
                if(index == 0){
                    CommonUtils   dbUtils = new CommonUtils(this);
                    Wypread wypread = new Wypread();
                    wypread = dbUtils.listOneWypread(bookId);
                    if(wypread == null){
                        fragment2.playMusic(0,0);
                    }else{
                        String p = wypread.getQuitePosition();
                        String s = wypread.getQuiteChapter();
                        int position= Integer.valueOf(p);
                        int progresstion = Integer.valueOf(s);

                        fragment2.playMusic(position,progresstion);
                    }
                    initReadBookNum();
                    index =1;
                }else if(index == 1){
                    fragment2.pauseMusic();
                    index = 2;
                }else if(index == 2){
                    fragment2.pMusic();
                    index = 1;
                }
                break;
            case R.id.quiteDetails_collection:
                if(!detailsCollection.isSelected()){
                    collection("0");
                }else{
                    collection("1");
                }
                break;
            case R.id.quiteDetails_jianjie:
                detailsVP.setCurrentItem(0);
                checkJianjie();
                break;
            case R.id.quiteDetails_list:
                detailsVP.setCurrentItem(1);
                checkList();
                break;
        }
    }

    /**收藏 and  取消收藏*/
    private void collection(final String type){
        UtilBox.showDialog(this,"收藏中,请稍候");
        HashMap<String,String> map = new HashMap<>();
        map.put("userID",SharedPreferenceUtil.getStringData("userId"));
        map.put("bookID",getIntent().getStringExtra("quiteID"));
        map.put("type",type);
        OkHttpUtils.post().url(MyUrl.collection).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                UtilBox.dismissDialog();
                Toast.makeText(QuiteDetailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean = new Gson().fromJson(response,RootBean.class);
                if(bean.getResultCode() == 0){
                    if("0".equals(type)){
                        detailsCollection.setText("已收藏");
                        detailsCollection.setSelected(true);
                        detailsCollection.setTextColor(getResources().getColor(R.color.basecolor));
                    }else{
                        detailsCollection.setText("收藏");
                        detailsCollection.setSelected(false);
                        detailsCollection.setTextColor(getResources().getColor(R.color.black));
                    }
                }
            }
        });
    }

    //选择简介
    private void checkJianjie(){
        detailsJianjie.setTextColor(getResources().getColor(R.color.basecolor));
        detailsList.setTextColor(getResources().getColor(R.color.black));
        detailsJianjieView.setVisibility(View.VISIBLE);
        detailsListView.setVisibility(View.GONE);
    }
    //选择列表
    private void checkList(){
        detailsJianjie.setTextColor(getResources().getColor(R.color.black));
        detailsList.setTextColor(getResources().getColor(R.color.basecolor));
        detailsJianjieView.setVisibility(View.GONE);
        detailsListView.setVisibility(View.VISIBLE);
    }
    /**刷新统计量阅读量*/
    public void refresh(){
        HashMap<String,String> map = new HashMap<>();
        map.put("userID",SharedPreferenceUtil.getStringData("userId"));
        map.put("bookID",getIntent().getStringExtra("quiteID"));
        OkHttpUtils.post().url(MyUrl.quiteDetails).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                UtilBox.dismissDialog();
                Toast.makeText(QuiteDetailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean = new Gson().fromJson(response,RootBean.class);
                if(bean.getResultCode() == 0){
                    listionNum = bean.getData().getBookDetails().getLookCount();
                    detailsReadNum.setText("收听量:" + bean.getData().getBookDetails().getLookCount());
                    detailsDownNum.setText("下载量:" + bean.getData().getBookDetails().getDownCount());
                }
            }
        });
    }

    /***统计阅读量*/
    private void initReadBookNum() {
        HashMap<String,String> map = new HashMap<>();
        map.put("bookId",quiteId);
        map.put("userId",SharedPreferenceUtil.getStringData("userId"));
        map.put("StatisticsType","1");
        OkHttpUtils.post().url(MyUrl.Statistics).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(QuiteDetailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response) {
                RootBean bean = new Gson().fromJson(response,RootBean.class);
                if(bean.getResultCode() == 0){
                    listionNum();
                }
            }
        });
    }
    //收听量加1
    public void listionNum(){
        try{
            int li = Integer.valueOf(listionNum);
            li = li +1;
            detailsReadNum.setText("收听量:" + li);
        }catch (Exception e){}

    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver;
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    }
    @Override
    public int setBaseView() {
        return R.layout.activity_quitedetails;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "听书详情";
    }

    @Override
    public boolean showMore() {
        return false;
    }
}
