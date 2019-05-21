package com.sanmiao.wypread.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.adapter.MainBookAdapter;
import com.sanmiao.wypread.adapter.MainQuiteAdapter;
import com.sanmiao.wypread.adapter.MainVideoAdapter;
import com.sanmiao.wypread.bean.CollectionBook;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.bean.RootBeanEx;
import com.sanmiao.wypread.bean.SlideShow;
import com.sanmiao.wypread.ui.BookDtailsActivity;
import com.sanmiao.wypread.ui.LoginActivity;
import com.sanmiao.wypread.ui.MainActivity;
import com.sanmiao.wypread.ui.QRCodeActivity;
import com.sanmiao.wypread.ui.QuiteDetailsActivity;
import com.sanmiao.wypread.ui.VideoDetailsActivity;
import com.sanmiao.wypread.utils.BannerHolder;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.UtilBox;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/2 0002.
 * 类说明{首页}
 */

public class MainFragment extends Fragment {
    View thisView;
    @InjectView(R.id.mian_banner)
    ConvenientBanner mianBanner;//轮播
    @InjectView(R.id.main_book)
    RecyclerView mainBook;//图书
    @InjectView(R.id.main_quite)
    RecyclerView mainQuite;//听书
    @InjectView(R.id.main_video)
    RecyclerView mainVideo;//视频
    @InjectView(R.id.main_scan)
    ImageView mainScan;

    List<SlideShow> bannerList = new ArrayList<>();//轮播图集合

    List<CollectionBook> bookList = new ArrayList<>();
    List<CollectionBook> quiteList = new ArrayList<>();
    List<CollectionBook> videoList = new ArrayList<>();
    MainBookAdapter bookAdapter;
    MainQuiteAdapter quiteAdapter;
    MainVideoAdapter videoAdapter;
    @InjectView(R.id.main_RF)
    TwinklingRefreshLayout mainRF;

    private Handler handler= new Handler(Looper.getMainLooper());//初始化handler


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_main, null);
        ButterKnife.inject(this, thisView);
        initBanner();
        initData();
        initView();
        mianBanner.setFocusable(true);
        mianBanner.setFocusableInTouchMode(true);
        mianBanner.requestFocus();

        return thisView;
    }

    private void initData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("userID", SharedPreferenceUtil.getStringData("userId"))
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(MyUrl.home)
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
                    bookList.clear();
                    quiteList.clear();
                    videoList.clear();
                    bannerList.addAll(bean.getData().getSlideShow());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            initBanner();
                        }
                    });
                    bookList.addAll(bean.getData().getRecommendBook());
                    //quiteList.addAll(bean.getData().getRecommendListen());
                    //videoList.addAll(bean.getData().getRecommendVideo());
                    //quiteAdapter.notifyDataSetChanged();
                    //videoAdapter.notifyDataSetChanged();
                    if(mainRF!=null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                bookAdapter.notifyDataSetChanged();
                                mainRF.finishLoadmore();
                                mainRF.finishRefreshing();
                            }
                        });
                    }
                } else if (bean.getResultCode() == 2) {
                    SharedPreferenceUtil.SaveData("userLogin", false);
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                    Toast.makeText(getActivity(), "授权码已过期", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        OkHttpUtils.post().url(MyUrl.home).params(map).tag(getActivity()).build().execute(new StringCallback() {
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
//                    bookList.clear();
//                    quiteList.clear();
//                    videoList.clear();
//                    bannerList.addAll(bean.getData().getSlideShow());
//                    initBanner();
//                    bookList.addAll(bean.getData().getRecommendBook());
//                    quiteList.addAll(bean.getData().getRecommendListen());
//                    videoList.addAll(bean.getData().getRecommendVideo());
//                    bookAdapter.notifyDataSetChanged();
//                    quiteAdapter.notifyDataSetChanged();
//                    videoAdapter.notifyDataSetChanged();
//                    if(mainRF!=null){
//                        mainRF.finishLoadmore();
//                        mainRF.finishRefreshing();
//                    }
//                } else if (bean.getResultCode() == 2) {
//                    SharedPreferenceUtil.SaveData("userLogin", false);
//                    startActivity(new Intent(getActivity(), LoginActivity.class));
//                    getActivity().finish();
//                    Toast.makeText(getActivity(), "授权码已过期", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


    }

    //设置布局
    private void initView() {
        //设置加载效果
        SinaRefreshView sinaRefreshView = new SinaRefreshView(getActivity());
        mainRF.setHeaderView(sinaRefreshView);
        mainRF.setEnableLoadmore(false);//禁用上拉加载
        //下拉刷新
        mainRF.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                initData();
            }
        });

        //推荐图书
        bookAdapter = new MainBookAdapter(getActivity(), bookList, false);
        GridLayoutManager manager1 = new GridLayoutManager(getActivity(), 3);
        manager1.setOrientation(GridLayoutManager.VERTICAL);
        mainBook.setLayoutManager(manager1);
        mainBook.setAdapter(bookAdapter);
        bookAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getActivity(), BookDtailsActivity.class).putExtra("bookID", bookList.get(i).getBookID()));
            }
        });
        //推荐听书
        quiteAdapter = new MainQuiteAdapter(getActivity(), quiteList, false);
        GridLayoutManager manager2 = new GridLayoutManager(getActivity(), 3);
        manager1.setOrientation(GridLayoutManager.VERTICAL);
        mainQuite.setLayoutManager(manager2);
        mainQuite.setAdapter(quiteAdapter);
        quiteAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getActivity(), QuiteDetailsActivity.class).putExtra("quiteID", quiteList.get(i).getBookID()));
            }
        });

        //推荐视频
        videoAdapter = new MainVideoAdapter(getActivity(), videoList, false);
        GridLayoutManager manager3 = new GridLayoutManager(getActivity(), 3);
        manager1.setOrientation(GridLayoutManager.VERTICAL);
        mainVideo.setLayoutManager(manager3);
        mainVideo.setAdapter(videoAdapter);
        videoAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getActivity(), VideoDetailsActivity.class).putExtra("videoID", videoList.get(i).getBookID()));
            }
        });


    }

    //设置轮播图
    private void initBanner() {
        if (mianBanner == null) return;
        mianBanner.setPages(new CBViewHolderCreator() { //设置需要切换的View
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

    //二维码扫描
    @OnClick({R.id.main_scan})
    public void OnClick(View view) {
        new IntentIntegrator(getActivity())
                .setOrientationLocked(false)
                .setCaptureActivity(QRCodeActivity.class)
                .initiateScan(); // 初始化扫描
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
