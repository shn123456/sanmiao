package com.sanmiao.wypread.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.adapter.QuiteSearchAdapter;
import com.sanmiao.wypread.bean.Books;
import com.sanmiao.wypread.bean.RootBean;
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
 * 时间 2017/5/6 0006.
 * 类说明{听书搜索}
 */

public class QuiteSearchActivity extends BaseActivity {

    @InjectView(R.id.RV)
    RecyclerView RV;
    List<Books> list = new ArrayList<>();
    QuiteSearchAdapter adapter;
    @InjectView(R.id.TV)
    TextView TV;
    @InjectView(R.id.defout)
    TextView defout;
    @InjectView(R.id.click)
    TextView click;
    @InjectView(R.id.down)
    TextView down;
    @InjectView(R.id.bestNew)
    TextView bestNew;
    List<Integer> imgList = new ArrayList<>();//选择器图片集合
    @InjectView(R.id.clickImg)
    ImageView clickImg;
    @InjectView(R.id.downImg)
    ImageView downImg;
    @InjectView(R.id.resresh)
    TwinklingRefreshLayout resresh;
    int page =1;
    String sorttype="3";
    String order="asc";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        initImg();
        setDefoult();
        defout.setTextColor(getResources().getColor(R.color.basecolor));
        initData();
        initView();
    }

    //设置选择器图片集合
    private void initImg() {
        imgList.clear();
        int i = SharedPreferenceUtil.getIntData("bgcolor");
        imgList.add(R.mipmap.btn_hui);
        if (i == 1) {
            imgList.add(R.mipmap.btn_shang_difen);
            imgList.add(R.mipmap.btn_xia_difen);
        } else if (i == 2) {
            imgList.add(R.mipmap.btn_shang_bohong);
            imgList.add(R.mipmap.btn_xia_bohong);
        } else if (i == 3) {
            imgList.add(R.mipmap.btn_shang_lan);
            imgList.add(R.mipmap.btn_xia_lan);
        } else if (i == 4) {
            imgList.add(R.mipmap.btn_shang_caolv);
            imgList.add(R.mipmap.btn_xia_caolv);
        } else if (i == 5) {
            imgList.add(R.mipmap.btn_shang_yanzhi);
            imgList.add(R.mipmap.btn_xia_yanzhi);
        } else {
            imgList.add(R.mipmap.btn_shang_boqing);
            imgList.add(R.mipmap.btn_xia_boqing);
        }
    }

    //设置数据
    private void initData() {
        String str = getIntent().getStringExtra("str");
        UtilBox.showDialog(this, "正在搜索,请稍候");
        HashMap<String, String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
        map.put("keyword", str);
        map.put("type", "2");
        map.put("sorttype", sorttype);//1点击量 2下载量 3 添加时间
        map.put("order", order);// asc 正序  desc倒序
        map.put("pageIndex",page + "");
        map.put("pageSize","9");
        OkHttpUtils.post().url(MyUrl.search).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                UtilBox.dismissDialog();
                Toast.makeText(QuiteSearchActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                TV.setVisibility(View.GONE);
                RV.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean = new Gson().fromJson(response, RootBean.class);
                if (bean.getResultCode() == 0) {
                    if(page == 1)
                        list.clear();

                    list.addAll(bean.getData().getBooks());
                    adapter.notifyDataSetChanged();
                    if (resresh != null) {
                        resresh.finishLoadmore();
                    }
                    if (list.size() > 0) {
                        TV.setVisibility(View.GONE);
                        RV.setVisibility(View.VISIBLE);
                    } else {
                        TV.setVisibility(View.VISIBLE);
                        RV.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @OnClick({R.id.defout, R.id.down, R.id.click, R.id.bestNew})
    public void OnClick(View view) {
        list.clear();
        switch (view.getId()) {
            case R.id.defout:
                setDefoult();
                defout.setTextColor(getResources().getColor(R.color.basecolor));
                sorttype="3";
                order="asc";
                page=1;
                initData();
                break;
            case R.id.down:
                page=1;
                defout.setTextColor(getResources().getColor(R.color.black));
                clickImg.setImageResource(imgList.get(0));
                bestNew.setTextColor(getResources().getColor(R.color.black));

                if (checkDown == 0) {
                    sorttype="2";
                    order="asc";
                    initData();
                    downImg.setImageResource(imgList.get(1));
                    checkDown = 1;
                } else {
                    sorttype="2";
                    order="desc";
                    initData();
                    checkDown = 0;
                    downImg.setImageResource(imgList.get(2));
                }
                break;
            case R.id.click:
                page=1;
                defout.setTextColor(getResources().getColor(R.color.black));
                downImg.setImageResource(imgList.get(0));
                bestNew.setTextColor(getResources().getColor(R.color.black));

                if (checkClick == 0) {
                    sorttype="1";
                    order="asc";
                    initData();
                    clickImg.setImageResource(imgList.get(1));
                    checkClick = 1;
                } else {
                    sorttype="1";
                    order="desc";
                    initData();
                    clickImg.setImageResource(imgList.get(2));
                    checkClick = 0;
                }
                break;
            case R.id.bestNew:
                page=1;
                setDefoult();
                bestNew.setTextColor(getResources().getColor(R.color.basecolor));
                sorttype="3";
                order="desc";
                initData();
                break;
        }
    }

    int checkClick = 0;
    int checkDown = 0;

    //全部设为默认
    private void setDefoult() {
        clickImg.setImageResource(imgList.get(0));
        downImg.setImageResource(imgList.get(0));
        defout.setTextColor(getResources().getColor(R.color.black));
        bestNew.setTextColor(getResources().getColor(R.color.black));
    }


    //设置布局
    private void initView() {
        //设置加载效果
        resresh.setEnableRefresh(false);
        LoadingView loadingView = new LoadingView(this);
        resresh.setBottomView(loadingView);
        //上拉加载
        resresh.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                page++;
                initData();
            }
        });
        adapter = new QuiteSearchAdapter(this, list, false);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        RV.setLayoutManager(manager);
        RV.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(QuiteSearchActivity.this, QuiteDetailsActivity.class).putExtra("quiteID", list.get(i).getBookID()));
            }
        });
    }

    @Override
    public int setBaseView() {
        return R.layout.activity_search;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "搜索";
    }

    @Override
    public boolean showMore() {
        return false;
    }
}