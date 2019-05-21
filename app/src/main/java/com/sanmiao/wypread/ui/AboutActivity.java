package com.sanmiao.wypread.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.utils.MyUrl;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/24 0024.
 * 类说明{关于我们}
 */

public class AboutActivity extends BaseActivity {


    @InjectView(R.id.about_tv)
    WebView aboutTv;
    @InjectView(R.id.web_bar)
    ProgressBar webBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        Map<String, String> map = new HashMap<>();
        map.put("", "");
        OkHttpUtils.post().tag(this).url(MyUrl.about).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(AboutActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                RootBean bean = new Gson().fromJson(response, RootBean.class);
                if (bean.getResultCode() == 0) {
                    aboutTv.loadUrl(MyUrl.imgUrl+bean.getData().getContent());
                }
            }
        });
        initView();
    }
    private void initView() {
        aboutTv.getSettings().setDefaultTextEncodingName("utf-8");//设置默认为Gbk
        //加载需要显示的网页
        aboutTv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//缓存
//         设置加载进来的页面自适应手机屏幕
        aboutTv.getSettings().setUseWideViewPort(true);
        aboutTv.getSettings().setLoadWithOverviewMode(true);
//      设置Web视图
        aboutTv.setWebViewClient(new HelloWebViewClient());
        webBar.setMax(100);
        jindu();
    }

    /**
     * 加载进度
     */
    private void jindu() {
        aboutTv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(aboutTv, newProgress);
                if (newProgress == 100) {
                    webBar.setVisibility(View.GONE);
                } else {
                    if (View.INVISIBLE == webBar.getVisibility()) {
                        webBar.setVisibility(View.VISIBLE);
                    }
                    webBar.setProgress(newProgress);
                }
            }
        });
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public int setBaseView() {
        return R.layout.activity_about;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "关于我们";
    }

    @Override
    public boolean showMore() {
        return false;
    }
}
