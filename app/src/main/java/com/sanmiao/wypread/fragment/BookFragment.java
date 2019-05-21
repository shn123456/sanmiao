package com.sanmiao.wypread.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.adapter.ReadBookAdapter;
import com.sanmiao.wypread.adapter.ReadClassAdapter;
import com.sanmiao.wypread.bean.Classifies;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.bean.SlideShow;
import com.sanmiao.wypread.ui.BookDtailsActivity;
import com.sanmiao.wypread.ui.BookListActivity;
import com.sanmiao.wypread.ui.BookSearchActivity;
import com.sanmiao.wypread.ui.LoginActivity;
import com.sanmiao.wypread.ui.MoreClassActivtiy;
import com.sanmiao.wypread.ui.QuiteDetailsActivity;
import com.sanmiao.wypread.ui.VideoDetailsActivity;
import com.sanmiao.wypread.utils.BannerHolder;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/2 0002.
 * 类说明{阅读}
 */

public class BookFragment extends Fragment {
    View thisView;
    @InjectView(R.id.read_class)
    RecyclerView readClass;//分类
    @InjectView(R.id.read_book)
    RecyclerView readBook;//书籍列表
    @InjectView(R.id.read_search)
    EditText readSearch;//搜索
    List<SlideShow> bannerList = new ArrayList<>();//轮播图集合
    List<Classifies> classList = new ArrayList<>();//分类集合
    List<Classifies> bookList = new ArrayList<>();
    @InjectView(R.id.read_banner)
    ConvenientBanner readBanner;
    ReadClassAdapter classAdapter;
    ReadBookAdapter bookAdapter;
    @InjectView(R.id.bookRF)
    TwinklingRefreshLayout bookRF;
    private Handler handler= new Handler(Looper.getMainLooper());//初始化handler

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_read, null);
        ButterKnife.inject(this, thisView);
        readSearch.setHint("请输入图书名称、作者");
        initData();
        initView();
        return thisView;
    }

    //设置布局
    private void initView() {
        //设置加载效果
        SinaRefreshView sinaRefreshView = new SinaRefreshView(getActivity());
        bookRF.setHeaderView(sinaRefreshView);
        bookRF.setEnableLoadmore(false);//禁用上拉加载
        //下拉刷新
        bookRF.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                initData();
            }
        });

        //分类布局
        classAdapter = new ReadClassAdapter(getActivity(), classList, false);
        GridLayoutManager manager1 = new GridLayoutManager(getActivity(), 5);
        manager1.setOrientation(GridLayoutManager.VERTICAL);
        readClass.setLayoutManager(manager1);
        readClass.setAdapter(classAdapter);
        classAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == classList.size() - 1 && classList.size()>=9) {//如果是最后一项
                    startActivity(new Intent(getActivity(), MoreClassActivtiy.class).putExtra("type", "1"));
                } else
                    startActivity(new Intent(getActivity(), BookListActivity.class).putExtra("className", classList.get(i).getName()).putExtra("classID", classList.get(i).getClassifyID()));

            }
        });

        //图书布局
        bookAdapter = new ReadBookAdapter(getActivity(), bookList, false);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        readBook.setLayoutManager(manager);
        readBook.setAdapter(bookAdapter);

        //搜索监听
        readSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    // 隐藏软键盘
                    imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                    String str = readSearch.getText().toString();
                    if (TextUtils.isEmpty(str))
                        Toast.makeText(getActivity(), "请输入搜索内容", Toast.LENGTH_SHORT).show();
                    else
                        startActivity(new Intent(getActivity(), BookSearchActivity.class).putExtra("str", str));
                    return true;
                }
                return false;
            }
        });


    }

    //添加数据
    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
        map.put("type", "1");

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("userID", SharedPreferenceUtil.getStringData("userId"))
                .add("type", "1")
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(MyUrl.lookHome)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                String res = "";
                try {
                    res = response.body().string();
                }
                catch (Exception ex) {
                    return;
                }
                RootBean bean = new Gson().fromJson(res, RootBean.class);
                if (bean.getResultCode() == 0) {
                    bannerList.clear();
                    classList.clear();
                    bookList.clear();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            classAdapter.notifyDataSetChanged();
                            bookAdapter.notifyDataSetChanged();
                        }
                    });

                    bannerList.addAll(bean.getData().getSlideShow());
                    for (int i = 0; i < (bean.getData().getClassifies().size() < 9 ? bean.getData().getClassifies().size() : 9); i++) {
                        //分类数据
                        classList.add(bean.getData().getClassifies().get(i));
                    }
                    //列表数据
                    for (int i = 0; i < bean.getData().getClassifies().size(); i++) {
                        if(bean.getData().getClassifies().get(i).getItems().size() >0){
                            bookList.add(bean.getData().getClassifies().get(i));
                        }
                    }
                    if(bean.getData().getClassifies().size() >=9){
                        Classifies classifies = new Classifies();
                        classifies.setName("更多");
                        classList.add(classifies);
                    }

                    classAdapter.notifyDataSetChanged();
                    bookAdapter.notifyDataSetChanged();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            initBanner();
                        }
                    });

                    if(bookRF!=null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                bookRF.finishLoadmore();
                                bookRF.finishRefreshing();
                            }
                        });
                    }
                }
            }
        });

//        OkHttpUtils.post().url(MyUrl.lookHome).params(map).tag(getActivity()).build().execute(new StringCallback() {
//            @Override
//            public void onError(Request request, Exception e) {
//                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(String response) {
//                RootBean bean = new Gson().fromJson(response, RootBean.class);
//                if (bean.getResultCode() == 0) {
//                    bannerList.clear();
//                    classList.clear();
//                    bookList.clear();
//                    classAdapter.notifyDataSetChanged();
//                    bookAdapter.notifyDataSetChanged();
//
//                    bannerList.addAll(bean.getData().getSlideShow());
//                    for (int i = 0; i < (bean.getData().getClassifies().size() < 9 ? bean.getData().getClassifies().size() : 9); i++) {
//                        //分类数据
//                        classList.add(bean.getData().getClassifies().get(i));
//                    }
//                    //列表数据
//                    for (int i = 0; i < bean.getData().getClassifies().size(); i++) {
//                        if(bean.getData().getClassifies().get(i).getItems().size() >0){
//                            bookList.add(bean.getData().getClassifies().get(i));
//                        }
//                    }
//                    if(bean.getData().getClassifies().size() >=9){
//                        Classifies classifies = new Classifies();
//                        classifies.setName("更多");
//                        classList.add(classifies);
//                    }
//
//                    classAdapter.notifyDataSetChanged();
//                    bookAdapter.notifyDataSetChanged();
//                    initBanner();
//                    if(bookRF!=null){
//                        bookRF.finishLoadmore();
//                        bookRF.finishRefreshing();
//                    }
//                }
//            }
//        });
    }


    //设置轮播图
    private void initBanner() {
        readBanner.setPages(new CBViewHolderCreator() { //设置需要切换的View
            @Override
            public Object createHolder() {
                return new BannerHolder();
            }
        }, bannerList)
                .setPointViewVisible(true)    //设置指示器是否可见
                .setPageIndicator(new int[]{R.drawable.banner_white, R.drawable.banner_blue})   //设置指示器圆点
                .startTurning(3000)     //设置自动切换（同时设置了切换时间间隔）
                //.setManualPageable(false)  //设置手动影响（设置了该项无法手动切换）
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL) //设置指示器位置（左、中、右）
                .setOnItemClickListener(new OnItemClickListener() {//设置点击监听事件
                    @Override
                    public void onItemClick(int position) {
                        if ("1".equals(bannerList.get(position).getBookType())) {
                            startActivity(new Intent(getActivity(), BookDtailsActivity.class).putExtra("bookID", bannerList.get(position).getBookID()));
                        } else if ("2".equals(bannerList.get(position).getBookType())) {
                            //startActivity(new Intent(getActivity(), QuiteDetailsActivity.class).putExtra("quiteID", bannerList.get(position).getBookID()));
                        } else {
                            //startActivity(new Intent(getActivity(), VideoDetailsActivity.class).putExtra("videoID", bannerList.get(position).getBookID()));
                        }
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
