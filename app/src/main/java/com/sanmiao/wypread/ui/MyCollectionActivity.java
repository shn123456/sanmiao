package com.sanmiao.wypread.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.adapter.VpFragmentAdapter;
import com.sanmiao.wypread.bean.FileBean;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.fragment.BookCollectionFragment;
import com.sanmiao.wypread.fragment.NoneFragment;
import com.sanmiao.wypread.fragment.QuiteCollectionFragment;
import com.sanmiao.wypread.fragment.VideoCollectionFragment;
import com.sanmiao.wypread.utils.FileUtils;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.UtilBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/5 0005.
 * 类说明{我的收藏}
 */

public class MyCollectionActivity extends BaseActivity {

    @InjectView(R.id.collection_book)
    TextView collectionBook;//图书
    @InjectView(R.id.collection_bookView)
    View collectionBookView;//图书底部线
    @InjectView(R.id.collection_quite)
    TextView collectionQuite;//听书
    @InjectView(R.id.collection_quiteView)
    View collectionQuiteView;//听书底部线
    @InjectView(R.id.collection_Video)
    TextView collectionVideo;//视频
    @InjectView(R.id.collection_VideoView)
    View collectionVideoView;//视频底部线
    @InjectView(R.id.collection_VP)
    ViewPager collectionVP;
    @InjectView(R.id.collection_ok)
    TextView collectionOk;//导入本地文件 or 取消收藏
    int importID = 0;
    BookCollectionFragment fragment1;
    NoneFragment fragment2;
    NoneFragment fragment3;
    List<Fragment> fragmentList=new ArrayList<>();
    int index=0;//页卡选择标识

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        setMoreText("编辑");
        checkBook();
        initView();
        importID=SharedPreferenceUtil.getIntData("importID");
    }
    @OnClick({R.id.collection_book,R.id.collection_quite,R.id.collection_Video,R.id.collection_ok})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.collection_book:
                checkBook();
                collectionVP.setCurrentItem(0);
                break;
            case R.id.collection_quite:
                checkQuite();
                collectionVP.setCurrentItem(1);
                break;
            case R.id.collection_Video:
                checkVideo();
                collectionVP.setCurrentItem(2);
                break;
            case R.id.collection_ok:
                if(collectionOk.getText().equals("取消收藏")){
                    if(index==0){
                        fragment1.moveCollction();
                    }else if(index==1){
                        //fragment2.moveCollction();
                    }else{
                        //fragment3.moveCollction();
                    }
                }else{
                    if(index==0){
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent,0);
                    }else if(index==1){
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent,1);
                    }else{
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent,2);
                    }
                }
                break;
        }
    }

    CommonUtils commonUtils;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        importID++;
        long id =System.currentTimeMillis();
        commonUtils= new CommonUtils(this);
        if (resultCode == Activity.RESULT_OK  && requestCode == 0) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            File file = new File(uri.getPath().toString());
            String[] url = uri.toString().split("\\.");
            if("TXT".equals(url[url.length-1].toUpperCase())  || "PDF".equals(url[url.length-1].toUpperCase())){
                //数据库插入文件
                Wypread wypread= new Wypread();
                wypread.setId((long)id);
                wypread.setBookPath(uri.getPath().toString());
                wypread.setName(file.getName());
                wypread.setReadJindu("0%");
                wypread.setFromFile("book");
                wypread.setImgPath((id)+"");
                commonUtils.insertWypread(wypread);
                fragment1.importBook();
            }
        }
//        if (resultCode == Activity.RESULT_OK  && requestCode == 1) {//是否选择，没选择就不会继续
//            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
//            String str= UtilBox.getRealFilePath(this,uri);
//            File file = new File(str);
//            String[] url = str.toString().split("\\.");
//            if("MP3".equals(url[url.length-1].toUpperCase())){
//                //数据库插入文件
//                Wypread wypread= new Wypread();
//                wypread.setId((long)id);
//                wypread.setBookPath(str);
//                wypread.setName(file.getName());
//                wypread.setReadJindu("0%");
//                wypread.setFromFile("quite");
//                wypread.setImgPath((id)+"");
//                commonUtils.insertWypread(wypread);
//                fragment2.importBook();
//                fragment2.refresh();
//            }
//        }
//        if (resultCode == Activity.RESULT_OK  && requestCode == 2) {//是否选择，没选择就不会继续
//            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
//            String str= UtilBox.getRealFilePath(this,uri);
//            File file = new File(str);
//            String[] url = str.toString().split("\\.");
//            if("MP4".equals(url[url.length-1].toUpperCase())  || "AVI".equals(url[url.length-1].toUpperCase())
//                    || "FLV".equals(url[url.length-1].toUpperCase())|| "RMVB".equals(url[url.length-1].toUpperCase())){
//                //数据库插入文件
//                Wypread wypread= new Wypread();
//                wypread.setId((long)id);
//                wypread.setBookPath(str);
//                wypread.setName(file.getName());
//                wypread.setReadJindu("0%");
//                wypread.setFromFile("video");
//                wypread.setImgPath((id)+"");
//                commonUtils.insertWypread(wypread);
//                fragment3.importBook();
//            }
//        }
    }

    /**设置布局*/
    private void initView(){
        fragment1= new BookCollectionFragment();
        fragment2 = new NoneFragment();
        fragment3 = new NoneFragment();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);
        collectionVP.setOffscreenPageLimit(3);
        collectionVP.setAdapter(new VpFragmentAdapter(getSupportFragmentManager(), fragmentList));
        collectionVP.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0)
                    checkBook();
                else if(position==1)
                    checkQuite();
                else
                    checkVideo();

            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    /**编辑监听*/
    @Override
    public void moretextListener() {
        //判断是否是编辑状态
        if ("编辑".equals(getMoreText())){
            fragment1.setCheckOn();
            //fragment2.setCheckOn();
            //fragment3.setCheckOn();
            collectionOk.setText("取消收藏");
            setMoreText("完成");
        }else{
            fragment1.setCheckOff();
            //fragment2.setCheckOff();
            //fragment3.setCheckOff();
            collectionOk.setText("导入本地文件");
            setMoreText("编辑");
        }

    }
    //选择图书页卡
    private void checkBook() {
        collectionBook.setTextColor(getResources().getColor(R.color.basecolor));
        collectionBookView.setVisibility(View.VISIBLE);
        collectionQuite.setTextColor(getResources().getColor(R.color.black));
        collectionQuiteView.setVisibility(View.GONE);
        collectionVideo.setTextColor(getResources().getColor(R.color.black));
        collectionVideoView.setVisibility(View.GONE);
        index=0;
    }
    //选择听书页卡
    private void checkQuite(){
        collectionBook.setTextColor(getResources().getColor(R.color.black));
        collectionBookView.setVisibility(View.GONE);
        collectionQuite.setTextColor(getResources().getColor(R.color.basecolor));
        collectionQuiteView.setVisibility(View.VISIBLE);
        collectionVideo.setTextColor(getResources().getColor(R.color.black));
        collectionVideoView.setVisibility(View.GONE);
        index=1;
    }
    //选择视频页卡
    private void checkVideo(){
        collectionBook.setTextColor(getResources().getColor(R.color.black));
        collectionBookView.setVisibility(View.GONE);
        collectionQuite.setTextColor(getResources().getColor(R.color.black));
        collectionQuiteView.setVisibility(View.GONE);
        collectionVideo.setTextColor(getResources().getColor(R.color.basecolor));
        collectionVideoView.setVisibility(View.VISIBLE);
        index=2;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        importID++;
        SharedPreferenceUtil.SaveData("importID",importID);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fragment1.refresh();
        //fragment2.refresh();
        //fragment3.refresh();
    }

    @Override
    public int setBaseView() {
        return R.layout.activity_mycollection;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "我的收藏";
    }

    @Override
    public boolean showMore() {
        return true;
    }
}
