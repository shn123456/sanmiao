package com.sanmiao.wypread.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.adapter.VpFragmentAdapter;
import com.sanmiao.wypread.fragment.BookCollectionFragment;
import com.sanmiao.wypread.fragment.BookDownFragment;
import com.sanmiao.wypread.fragment.NoneFragment;
import com.sanmiao.wypread.fragment.QuiteCollectionFragment;
import com.sanmiao.wypread.fragment.QuiteDownFragment;
import com.sanmiao.wypread.fragment.VideoCollectionFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/6 0006.
 * 类说明{我的下载}
 */

public class MyDownActivity extends BaseActivity {
    @InjectView(R.id.down_book)
    TextView downBook;//图书
    @InjectView(R.id.down_bookView)
    View downBookView;//图书底部线
    @InjectView(R.id.down_quite)
    TextView downQuite;//听书
    @InjectView(R.id.down_quiteView)
    View downQuiteView;//听书底部线
    @InjectView(R.id.down_VP)
    ViewPager downVP;
    @InjectView(R.id.down_ok)
    TextView downOk;//删除

    int index=0;//选择页卡标识
    BookDownFragment fragment1;
    NoneFragment fragment2;
    List<Fragment> fragmentList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        setMoreText("编辑");
        checkBook();
        initView();
    }
    private void initView(){
        fragment1= new BookDownFragment();
        fragment2 = new NoneFragment();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        downVP.setOffscreenPageLimit(2);
        downVP.setAdapter(new VpFragmentAdapter(getSupportFragmentManager(), fragmentList));
        downVP.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if(position == 0)
                    checkBook();
                else if(position==1)
                    checkQuite();
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    @OnClick({R.id.down_book,R.id.down_quite,R.id.down_ok})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.down_book:
                checkBook();
                downVP.setCurrentItem(0);
                break;
            case R.id.down_quite:
                checkQuite();
                downVP.setCurrentItem(1);
                break;
            case R.id.down_ok:
                if(index==0)
                    fragment1.moveDown();
                else
                    //fragment2.moveDown();

                break;
        }
    }
    //编辑监听
    @Override
    public void moretextListener() {
        if("编辑".equals(getMoreText())){
            downOk.setVisibility(View.VISIBLE);
            setMoreText("完成");
            fragment1.setCheckOn();
            //fragment2.setCheckOn();
        }else{
            setMoreText("编辑");
            downOk.setVisibility(View.GONE);
            //fragment2.setCheckOff();
            fragment1.setCheckOff();
        }
    }

    //选择图书
    public void checkBook(){
        downBook.setTextColor(getResources().getColor(R.color.basecolor));
        downBookView.setVisibility(View.VISIBLE);
        downQuite.setTextColor(getResources().getColor(R.color.black));
        downQuiteView.setVisibility(View.GONE);
        index=0;
    }
    //选择听书
    public void checkQuite(){
        downBook.setTextColor(getResources().getColor(R.color.black));
        downBookView.setVisibility(View.GONE);
        downQuite.setTextColor(getResources().getColor(R.color.basecolor));
        downQuiteView.setVisibility(View.VISIBLE);
        index=1;
    }


    @Override
    public int setBaseView() {
        return R.layout.activity_mydown;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "我的下载";
    }

    @Override
    public boolean showMore() {
        return true;
    }
}
