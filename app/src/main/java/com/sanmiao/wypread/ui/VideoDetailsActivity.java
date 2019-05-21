package com.sanmiao.wypread.ui;

import android.content.res.Resources;
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
import com.sanmiao.wypread.adapter.VpFragmentAdapter;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.fragment.VideoDetailsJianjieFragment;
import com.sanmiao.wypread.fragment.VideoDetailsListFragment;
import com.sanmiao.wypread.utils.GlideUtil;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.UtilBox;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

import static android.R.id.content;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/4 0004.
 * 类说明{视频详情}
 */

public class VideoDetailsActivity extends BaseActivity {
    @InjectView(R.id.videoDetails_video)
    JCVideoPlayer detailsVideo;//视频组件
    @InjectView(R.id.videoDetails_class)
    TextView detailsClass;//类别
    @InjectView(R.id.videoDetails_readNum)
    TextView detailsReadNum;//观看量
    @InjectView(R.id.videoDetails_jianjie)
    TextView detailsJianjie;//顶部简介
    @InjectView(R.id.videoDetails_jianjieView)
    View detailsJianjieView;//简介横线
    @InjectView(R.id.videoDetails_list)
    TextView detailsList;//列表
    @InjectView(R.id.videoDetails_listView)
    View detailsListView;//列表横线
    @InjectView(R.id.videoDetails_vp)
    ViewPager detailsVp;
    @InjectView(R.id.videoDetails_cover)
    ImageView detailsCover;//封面图
    @InjectView(R.id.videoDetails_collection)
    CheckBox detailsCollection;//收藏

    VideoDetailsJianjieFragment fragment1;
    VideoDetailsListFragment fragment2;
    List<Fragment> fragmentList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        detailsCollection.setPadding(20, 10, 10, 10);
        int i = SharedPreferenceUtil.getIntData("bgcolor");
        if (i == 1) {
            detailsCollection.setButtonDrawable(R.drawable.collection1);
        } else if (i == 2) {
            detailsCollection.setButtonDrawable(R.drawable.collection2);
        } else if (i == 3) {
            detailsCollection.setButtonDrawable(R.drawable.collection3);
        } else if (i == 4) {
            detailsCollection.setButtonDrawable(R.drawable.collection4);
        } else if (i == 5) {
            detailsCollection.setButtonDrawable(R.drawable.collection5);
        } else {
            detailsCollection.setButtonDrawable(R.drawable.collection);
        }
        showVideo(false);
        checkList();
        initView();
        detailsVp.setCurrentItem(1);
    }

    //设置布局
    private void initView() {
        Map<String, String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
        map.put("bookID", getIntent().getStringExtra("videoID"));
        OkHttpUtils.post().url(MyUrl.videoDetails).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(VideoDetailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                RootBean bean = new Gson().fromJson(response, RootBean.class);
                if (bean.getResultCode() == 0) {
                    if(!TextUtils.isEmpty(bean.getData().getBookDetails().getVideoUrl())){
                        GlideUtil.ShowImage(VideoDetailsActivity.this,MyUrl.imgUrl+bean.getData().getBookDetails().getVideoUrl(),detailsCover);
                    }else{
                        detailsCover.setImageResource(R.mipmap.icon_zhanweitu);
                    }
                    if("1".equals( bean.getData().getBookDetails().getCollect())){
                        detailsCollection.setTextColor(getResources().getColor(R.color.basecolor));
                        detailsCollection.setSelected(true);
                        detailsCollection.setText("已收藏");
                    } else{
                        detailsCollection.setText("收藏");
                        detailsCollection.setTextColor(getResources().getColor(R.color.black));
                        detailsCollection.setSelected(false);
                    }
                    detailsClass.setText("分类:  " + bean.getData().getBookDetails().getClassifyTitle());
                    detailsReadNum.setText("观看量:  " + bean.getData().getBookDetails().getLookCount());
                    fragment1.initView(bean.getData().getBookDetails().getIntro());
                    fragment2.initData(response);
                    s1=bean.getData().getBookDetails().getIntro();
                    s2=response;
                }
            }
        });
        fragment1 = new VideoDetailsJianjieFragment();
        fragment2 = new VideoDetailsListFragment();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);

        detailsVp.setOffscreenPageLimit(2);
        detailsVp.setAdapter(new VpFragmentAdapter(getSupportFragmentManager(), fragmentList));
        detailsVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    checkJianjie();
                else
                    checkList();
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    //设置视频地址
    public void setVideoUrl(String url, String name)  {
        detailsVideo.setUp(url, name);
        detailsCover.setVisibility(View.GONE);
    }
    String s1="";
    String s2="";


    @OnClick({R.id.videoDetails_collection, R.id.videoDetails_jianjie, R.id.videoDetails_list})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.videoDetails_collection:
                if (!detailsCollection.isSelected()) {
                    collection("0");
                } else {
                    collection("1");
                }
                break;
            case R.id.videoDetails_jianjie:
                detailsVp.setCurrentItem(0);
                checkJianjie();
                break;
            case R.id.videoDetails_list:
                detailsVp.setCurrentItem(1);
                checkList();
                break;
        }
    }

    /**封面与视频组件切换显示**/
    public void showVideo(boolean isShow){
        if(isShow){
            detailsVideo.setVisibility(View.VISIBLE);
            detailsCover.setVisibility(View.GONE);
        }else{
            detailsVideo.setVisibility(View.GONE);
            detailsCover.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 收藏 and  取消收藏
     */
    private void collection(final String type) {
        UtilBox.showDialog(this, "收藏中,请稍候");
        HashMap<String, String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
        map.put("bookID", getIntent().getStringExtra("videoID"));
        map.put("type", type);
        OkHttpUtils.post().url(MyUrl.collection).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                UtilBox.dismissDialog();
                Toast.makeText(VideoDetailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean = new Gson().fromJson(response, RootBean.class);
                if (bean.getResultCode() == 0) {
                    if ("0".equals(type)) {
                        detailsCollection.setText("已收藏");
                        detailsCollection.setSelected(true);
                        detailsCollection.setTextColor(getResources().getColor(R.color.basecolor));
                    } else {
                        detailsCollection.setSelected(false);
                        detailsCollection.setTextColor(getResources().getColor(R.color.black));
                        detailsCollection.setText("收藏");
                    }
                }
            }
        });
    }

    //选择简介
    private void checkJianjie() {
        detailsJianjie.setTextColor(getResources().getColor(R.color.basecolor));
        detailsList.setTextColor(getResources().getColor(R.color.black));
        detailsJianjieView.setVisibility(View.VISIBLE);
        detailsListView.setVisibility(View.GONE);
    }

    //选择列表
    private void checkList() {
        detailsJianjie.setTextColor(getResources().getColor(R.color.black));
        detailsList.setTextColor(getResources().getColor(R.color.basecolor));
        detailsJianjieView.setVisibility(View.GONE);
        detailsListView.setVisibility(View.VISIBLE);
    }
    /***
     * 统计观看量
     */
    public void initReadBookNum() {
        HashMap<String, String> map = new HashMap<>();
        map.put("bookId", getIntent().getStringExtra("videoID"));
        map.put("userId", SharedPreferenceUtil.getStringData("userId"));
        map.put("StatisticsType", "1");
        OkHttpUtils.post().url(MyUrl.Statistics).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(VideoDetailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                refreshNum();
            }
        });
    }

    /**刷新观看量*/
    public void refreshNum(){
        Map<String, String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
        map.put("bookID", getIntent().getStringExtra("videoID"));
        OkHttpUtils.post().url(MyUrl.videoDetails).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(VideoDetailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                RootBean bean = new Gson().fromJson(response, RootBean.class);
                if (bean.getResultCode() == 0) {
                    detailsReadNum.setText("观看量:  " + bean.getData().getBookDetails().getLookCount());
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            JCVideoPlayer.releaseAllVideos();
        } catch (Exception e) {
        }
    }

    @Override
    public int setBaseView() {
        return R.layout.activity_videodetails;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "视频详情";
    }

    @Override
    public boolean showMore() {
        return false;
    }
}
