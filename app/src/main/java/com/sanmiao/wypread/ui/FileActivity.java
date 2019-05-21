package com.sanmiao.wypread.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.DrmInitData;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.sanmiao.wypread.R;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/31 0031.
 * 类说明{导入文件}
 */

public class FileActivity extends BaseActivity {
    @InjectView(R.id.file_Rv)
    RecyclerView fileRv;
    @InjectView(R.id.file_import)
    TextView fileImport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        initData();
    }
    //添加数据
    private void initData() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
        /* intent.setType(“image/*”);
        intent.setType(“audio/*”); //选择音频
        intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
        intent.setType(“video/*;image/*”);//同时选择视频和图片*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String[] url = uri.toString().split("\\.");
            if("TXT".equals(url[url.length-1].toUpperCase())  || "PDF".equals(url[url.length-1].toUpperCase())){
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
            }

//            String[] proj = {MediaStore.Images.Media.DATA};
//            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
//            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            actualimagecursor.moveToFirst();
//            String img_path = actualimagecursor.getString(actual_image_column_index);
//            File file = new File(img_path);
//            Toast.makeText(FileActivity.this, file.toString(), Toast.LENGTH_SHORT).show();
        }
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
        return "导入文件";
    }

    @Override
    public boolean showMore() {
        return false;
    }


}
