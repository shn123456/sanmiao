package com.sanmiao.wypread.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanmiao.wypread.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/4 0004.
 * 类说明{听书简介}
 */

public class QuiteDetailsJianjieFragment extends Fragment {
    View thisView;
    @InjectView(R.id.jianjie_text)
    TextView jianjieText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_jianjie, null);
        ButterKnife.inject(this, thisView);
        return thisView;
    }

    public void initView(String text) {
        if(TextUtils.isEmpty(text)){
            jianjieText.setText("暂无简介内容");
        }else
            jianjieText.setText(text);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
