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
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.adapter.QuiteListAdapter;
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
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/3 0003.
 * 类说明{听书列表}
 */

public class QuiteListActivity extends BaseActivity {
    @InjectView(R.id.moreclass)
    RecyclerView moreclass;
    @InjectView(R.id.refresh)
    TwinklingRefreshLayout refresh;
    int page = 1;
    List<Books> list = new ArrayList<>();
    String className;
    QuiteListAdapter adapter;
    @InjectView(R.id.list_tv)
    TextView listTv;
    @InjectView(R.id.list_defout)
    TextView defout;
    @InjectView(R.id.list_click)
    TextView click;
    @InjectView(R.id.list_down)
    TextView down;
    @InjectView(R.id.list_bestNew)
    TextView bestNew;
    String sorttype = "3";//默认
    String order = "asc";//默认
    List<Integer> imgList = new ArrayList<>();//选择器图片集合
    @InjectView(R.id.list_clickImg)
    ImageView listClickImg;
    @InjectView(R.id.list_downImg)
    ImageView listDownImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        initImg();

        className = getIntent().getStringExtra("className");
        setTitle(className);

        setDefoult();
        defout.setTextColor(getResources().getColor(R.color.basecolor));
        initData(sorttype, order);
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

    //设置布局
    private void initView() {
        //设置加载效果
        SinaRefreshView sinaRefreshView = new SinaRefreshView(this);
        refresh.setHeaderView(sinaRefreshView);
        LoadingView loadingView = new LoadingView(this);
        refresh.setBottomView(loadingView);
        //下拉刷新  上拉加载
        refresh.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                page = 1;
                initData(sorttype, order);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                page++;
                initData(sorttype, order);
            }
        });
        adapter = new QuiteListAdapter(this, list, false);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        moreclass.setLayoutManager(manager);
        moreclass.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(QuiteListActivity.this, QuiteDetailsActivity.class).putExtra("quiteID", list.get(i).getBookID()));
            }
        });

    }

    //添加数据
    private void initData(String sorttype, String order) {
        UtilBox.showDialog(this, "加载数据,请稍候");
        Map<String, String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
        map.put("classifyID", getIntent().getStringExtra("classID"));
        map.put("pageIndex", page + "");
        map.put("pageSize", "9");
        map.put("sorttype", sorttype);//1点击量 2下载量 3 添加时间
        map.put("order", order);// asc 正序  desc倒序
        OkHttpUtils.post().url(MyUrl.classifyItem).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                UtilBox.dismissDialog();
                Toast.makeText(QuiteListActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                if (list.size() > 0) {
                    listTv.setVisibility(View.GONE);
                    moreclass.setVisibility(View.VISIBLE);
                } else {
                    listTv.setVisibility(View.VISIBLE);
                    moreclass.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean = new Gson().fromJson(response, RootBean.class);
                if (bean.getResultCode() == 0) {
                    if (page == 1) {
                        list.clear();
                    }
                    for (int i = 0; i < bean.getData().getBooks().size(); i++) {
                        Books books = new Books();
                        books.setBookID(bean.getData().getBooks().get(i).getBookID());
                        books.setName(bean.getData().getBooks().get(i).getName());
                        books.setClassName(className);
                        books.setImgUrl(bean.getData().getBooks().get(i).getImgUrl());
                        books.setWriter(bean.getData().getBooks().get(i).getWriter());
                        list.add(books);
                    }
                    adapter.notifyDataSetChanged();
                    if (list.size() > 0) {
                        listTv.setVisibility(View.GONE);
                        moreclass.setVisibility(View.VISIBLE);
                    } else {
                        listTv.setVisibility(View.VISIBLE);
                        moreclass.setVisibility(View.GONE);
                    }
                    if (refresh != null) {
                        refresh.finishLoadmore();
                        refresh.finishRefreshing();
                    }
                } else
                    Toast.makeText(QuiteListActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick({R.id.list_defout, R.id.list_down, R.id.list_click, R.id.list_bestNew})
    public void OnClick(View view) {
        list.clear();
        switch (view.getId()) {
            case R.id.list_defout:
                setDefoult();
                defout.setTextColor(getResources().getColor(R.color.basecolor));
                sorttype = "3";//默认
                order = "asc";//默认
                initData(sorttype, order);
                break;
            case R.id.list_down:
                defout.setTextColor(getResources().getColor(R.color.black));
                listClickImg.setImageResource(imgList.get(0));
                bestNew.setTextColor(getResources().getColor(R.color.black));

                if (checkDown == 0) {
                    sorttype = "2";//默认
                    order = "asc";//默认
                    initData(sorttype, order);
                    listDownImg.setImageResource(imgList.get(1));
                    checkDown = 1;
                } else {
                    sorttype = "2";//默认
                    order = "desc";//默认
                    initData(sorttype, order);
                    checkDown = 0;
                    listDownImg.setImageResource(imgList.get(2));
                }
                break;
            case R.id.list_click:
                defout.setTextColor(getResources().getColor(R.color.black));
                listDownImg.setImageResource(imgList.get(0));
                bestNew.setTextColor(getResources().getColor(R.color.black));

                if (checkClick == 0) {
                    sorttype = "1";//默认
                    order = "asc";//默认
                    initData(sorttype, order);
                    listClickImg.setImageResource(imgList.get(1));
                    checkClick = 1;
                } else {
                    sorttype = "1";//默认
                    order = "desc";//默认
                    initData(sorttype, order);
                    listClickImg.setImageResource(imgList.get(2));
                    checkClick = 0;
                }
                break;
            case R.id.list_bestNew:
                setDefoult();
                bestNew.setTextColor(getResources().getColor(R.color.basecolor));
                sorttype = "3";//默认
                order = "desc";//默认
                initData(sorttype, order);
                break;
        }
    }

    int checkClick = 0;
    int checkDown = 0;

    //全部设为默认
    private void setDefoult() {
        listClickImg.setImageResource(imgList.get(0));
        listDownImg.setImageResource(imgList.get(0));
        defout.setTextColor(getResources().getColor(R.color.black));
        bestNew.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public int setBaseView() {
        return R.layout.activity_moreclass;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {

        return className;
    }

    @Override
    public boolean showMore() {
        return false;
    }

}
