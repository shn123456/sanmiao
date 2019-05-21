package com.sanmiao.wypread.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.adapter.VpFragmentAdapter;
import com.sanmiao.wypread.fragment.BookCollectionFragment;
import com.sanmiao.wypread.fragment.BookHistoryFragment;
import com.sanmiao.wypread.fragment.NoneFragment;
import com.sanmiao.wypread.fragment.QuiteCollectionFragment;
import com.sanmiao.wypread.fragment.QuiteHistoryFragment;
import com.sanmiao.wypread.fragment.VideoCollectionFragment;
import com.sanmiao.wypread.fragment.VideoHistoryFragment;
import com.sanmiao.wypread.utils.UtilBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/6 0006.
 * 类说明{历史记录}
 */

public class MyHistoryActivity extends BaseActivity {

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
    TextView collectionOk;

    BookHistoryFragment fragment1;
    NoneFragment fragment2;
    NoneFragment fragment3;
    List<Fragment> fragmentList=new ArrayList<>();
    int index=0;//页卡选择标识
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        collectionOk.setVisibility(View.GONE);
        setMoreText("清除");
        checkBook();
        initView();
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
        }
    }
    /**设置布局*/
    private void initView(){
        fragment1= new BookHistoryFragment();
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
    /**清除监听*/
    @Override
    public void moretextListener() {
        final Dialog dialog = new Dialog(this, R.style.MyDialogStyle2);
        View view = View.inflate(this, R.layout.dialog_history, null);
        TextView no=(TextView) view.findViewById(R.id.hietory_no);
        TextView ok=(TextView) view.findViewById(R.id.hietory_ok);

        //点击事件
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index==0)
                    fragment1.moveHistory();
//                else if(index==1)
//                    fragment2.moveHistory();
//                else
//                    fragment3.moveHistory();

                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

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
    public int setBaseView() {
        return R.layout.activity_mycollection;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "历史记录";
    }

    @Override
    public boolean showMore() {
        return true;
    }
}
