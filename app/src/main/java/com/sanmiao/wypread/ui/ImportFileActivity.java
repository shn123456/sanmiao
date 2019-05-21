package com.sanmiao.wypread.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.sanmiao.wypread.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/17 0017.
 * 类说明{}
 */

public class ImportFileActivity extends BaseActivity {
    @InjectView(R.id.file_Rv)
    RecyclerView fileRv;
    @InjectView(R.id.file_import)
    TextView importFile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

    }



    @Override
    public int setBaseView() {
        return R.layout.activity_file;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "导入本地文件";
    }

    @Override
    public boolean showMore() {
        return false;
    }

}
