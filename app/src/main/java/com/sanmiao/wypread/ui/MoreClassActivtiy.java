package com.sanmiao.wypread.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.adapter.MoreClassAdapter;
import com.sanmiao.wypread.bean.Classifies;
import com.sanmiao.wypread.bean.LoginBean;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.bean.RootBeanEx;
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
 * 类说明{更多分类}
 */

public class MoreClassActivtiy extends BaseActivity {
    @InjectView(R.id.moreclass)
    RecyclerView moreclass;

    List<Classifies> list = new ArrayList<>();
    MoreClassAdapter adapter;
    @InjectView(R.id.refresh)
    TwinklingRefreshLayout refresh;
    @InjectView(R.id.class_lv)
    LinearLayout classLv;
    private Handler handler= new Handler(Looper.getMainLooper());//初始化handler

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        classLv.setVisibility(View.GONE);
        initData();
        initView();
    }

    //设置布局
    private void initView() {
        refresh.setEnableLoadmore(false);
        refresh.setEnableRefresh(false);

        final String type = getIntent().getStringExtra("type");

        adapter = new MoreClassAdapter(this, list, false);
        GridLayoutManager manager1 = new GridLayoutManager(this, 5);
        manager1.setOrientation(GridLayoutManager.VERTICAL);
        moreclass.setLayoutManager(manager1);
        moreclass.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if ("1".equals(type))
                    startActivity(new Intent(MoreClassActivtiy.this, BookListActivity.class).putExtra("classID", list.get(i).getClassifyID()).putExtra("className", list.get(i).getName()));
                else if ("2".equals(type))
                    startActivity(new Intent(MoreClassActivtiy.this, QuiteListActivity.class).putExtra("classID", list.get(i).getClassifyID()).putExtra("className", list.get(i).getName()));
                else if ("3".equals(type))
                    startActivity(new Intent(MoreClassActivtiy.this, VideoListActivity.class).putExtra("classID", list.get(i).getClassifyID()).putExtra("className", list.get(i).getName()));
            }
        });
    }

    //添加数据
    private void initData() {
//        Map<String, String> map = new HashMap<>();
//        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
//        map.put("type", getIntent().getStringExtra("type"));

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("userID", SharedPreferenceUtil.getStringData("userId"))
                .add("type", getIntent().getStringExtra("type"))
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
                        Toast.makeText(MoreClassActivtiy.this, "网络连接失败", Toast.LENGTH_SHORT).show();
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
                final RootBean bean = new Gson().fromJson(res, RootBean.class);
                if (bean.getResultCode() == 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            list.addAll(bean.getData().getClassifies());
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

//        OkHttpUtils.post().url(MyUrl.lookHome).params(map).tag(this).build().execute(new StringCallback() {
//            @Override
//            public void onError(Request request, Exception e) {
//                Toast.makeText(MoreClassActivtiy.this, "网络连接失败", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(String response) {
//                RootBean bean = new Gson().fromJson(response, RootBean.class);
//                if (bean.getResultCode() == 0) {
//                    list.addAll(bean.getData().getClassifies());
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        });
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
        return "更多分类";
    }

    @Override
    public boolean showMore() {
        return false;
    }

}
