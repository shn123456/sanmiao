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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.adapter.BookHistoryAdapter;
import com.sanmiao.wypread.bean.CollectionBook;
import com.sanmiao.wypread.bean.LoginBean;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.bean.RootBeanEx;
import com.sanmiao.wypread.ui.BookDtailsActivity;
import com.sanmiao.wypread.ui.LoginActivity;
import com.sanmiao.wypread.ui.MainActivity;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/6 0006.
 * 类说明{图书历史}
 */

public class BookHistoryFragment extends Fragment {
    View thisView;
    @InjectView(R.id.RV)
    RecyclerView RV;
    BookHistoryAdapter adapter;
    List<CollectionBook> list = new ArrayList<>();
    @InjectView(R.id.lv)
    LinearLayout lv;
    @InjectView(R.id.collection_refresh)
    TwinklingRefreshLayout collectionRefresh;
    int page =1;
    private Handler handler= new Handler(Looper.getMainLooper());//初始化handler

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_collection, null);
        ButterKnife.inject(this, thisView);
        lv.setVisibility(View.GONE);
        initData();
        initView();
        return thisView;
    }

    //设置数据
    private void initData() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
//        map.put("type", "1");
//        map.put("pageIndex", page + "");
//        map.put("pageSize", "9");

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("userID", SharedPreferenceUtil.getStringData("userId"))
                .add("type", "1")
                .add("pageIndex", page + "")
                .add("pageSize", "9")
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(MyUrl.myCollection)
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
                    if(page == 1)
                        list.clear();

                    list.addAll(bean.getData().getCollect_book());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    if(collectionRefresh != null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                collectionRefresh.finishLoadmore();
                                collectionRefresh.finishRefreshing();
                            }
                        });
                    }
                }
            }
        });

//        OkHttpUtils.post().url(MyUrl.myCollection).params(map).tag(getActivity()).build().execute(new StringCallback() {
//            @Override
//            public void onError(Request request, Exception e) {
//                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(String response) {
//                RootBean bean = new Gson().fromJson(response, RootBean.class);
//                if (bean.getResultCode() == 0) {
//                    if(page == 1)
//                        list.clear();
//
//                    list.addAll(bean.getData().getCollect_book());
//                    adapter.notifyDataSetChanged();
//                    if(collectionRefresh != null){
//                        collectionRefresh.finishLoadmore();
//                        collectionRefresh.finishRefreshing();
//                    }
//                }
//            }
//        });
    }

    //设置布局
    private void initView() {
        //设置加载效果
        SinaRefreshView sinaRefreshView = new SinaRefreshView(getActivity());
        collectionRefresh.setHeaderView(sinaRefreshView);
        LoadingView loadingView = new LoadingView(getActivity());
        collectionRefresh.setBottomView(loadingView);
        //下拉刷新  上拉加载
        collectionRefresh.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                page = 1;
                initData();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                page++;
                initData();
            }
        });
        adapter = new BookHistoryAdapter(getActivity(), list, false);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        RV.setLayoutManager(manager);
        RV.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getActivity(), BookDtailsActivity.class).putExtra("bookID", list.get(i).getBookID()));
            }
        });

    }

    //清空记录
    public void moveHistory() {
        UtilBox.showDialog(getActivity(), "清除中,请稍候");
        HashMap<String, String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
        map.put("type", "1");
        map.put("pageIndex", page+ "");
        map.put("pageSize", "9");
        OkHttpUtils.get().url(MyUrl.removeHistory).params(map).tag(getActivity()).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                UtilBox.dismissDialog();
                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean = new Gson().fromJson(response, RootBean.class);
                if (bean.getResultCode() == 0) {
                    list.removeAll(list);
                    adapter.notifyDataSetChanged();
                }
                Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
