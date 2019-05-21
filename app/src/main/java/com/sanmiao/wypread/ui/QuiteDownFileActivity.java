package com.sanmiao.wypread.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.adapter.FileAdapter;
import com.sanmiao.wypread.bean.FileBean;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.fragment.QuiteDetailsListFragment;
import com.sanmiao.wypread.utils.FileUtils;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.Player;
import com.sanmiao.wypread.utils.RecycleViewDivider;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.UtilBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/20 0020.
 * 类说明{}
 */

public class QuiteDownFileActivity extends BaseActivity {
//    @InjectView(R.id.file_Rv)
//    RecyclerView fileRv;
//    @InjectView(R.id.import_file)
//    TextView importFile;
    FileUtils fileUtils;
//    List<FileBean> list = new ArrayList<>();
    FileAdapter fileAdapter;
    @InjectView(R.id.list_num)
    TextView listNum;
    @InjectView(R.id.list_RV)
    RecyclerView listRV;
    @InjectView(R.id.list_progressBar)
    SeekBar listProgressBar;
    @InjectView(R.id.list_nowTime)
    TextView listNowTime;
    @InjectView(R.id.list_allTime)
    TextView listAllTime;
    @InjectView(R.id.list_start)
    CheckBox listStart;
    @InjectView(R.id.list_last)
    ImageView listLast;
    @InjectView(R.id.list_next)
    ImageView listNext;
    @InjectView(R.id.list_bottom)
    LinearLayout listBottom;
    int index = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        listNum.setVisibility(View.GONE);
        setTitle(getIntent().getStringExtra("name"));
        int i= SharedPreferenceUtil.getIntData("bgcolor");
        if(i==1){
            listStart.setButtonDrawable(R.drawable.start_or_stop2);
            listLast.setImageResource(R.mipmap.icon_prev_difen);
            listNext.setImageResource(R.mipmap.icon_nextsong_difen);
        }else if(i==2){
            listStart.setButtonDrawable(R.drawable.start_or_stop3);
            listLast.setImageResource(R.mipmap.icon_prev_bohong);
            listNext.setImageResource(R.mipmap.icon_nextsong_bohong);
        }else if(i==3){
            listStart.setButtonDrawable(R.drawable.start_or_stop4);
            listLast.setImageResource(R.mipmap.icon_prev_lan);
            listNext.setImageResource(R.mipmap.icon_nextsong_lan);
        }else if(i==4){
            listStart.setButtonDrawable(R.drawable.start_or_stop5);
            listLast.setImageResource(R.mipmap.icon_prev_caolv);
            listNext.setImageResource(R.mipmap.icon_nextsong_caolv);
        }else if(i==5){
            listStart.setButtonDrawable(R.drawable.start_or_stop6);
            listLast.setImageResource(R.mipmap.icon_prev_yanzhi);
            listNext.setImageResource(R.mipmap.icon_nextsong_yanzhi);
        }else{
            listStart.setButtonDrawable(R.drawable.start_or_stop);
            listLast.setImageResource(R.mipmap.icon_prev);
            listNext.setImageResource(R.mipmap.icon_nextsong);
        }
        initView();
        initData();
        player = new Player(listProgressBar,listNowTime,listAllTime);
        listProgressBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
    }


    @OnClick({R.id.list_start, R.id.list_last, R.id.list_next})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.list_start:
                if(!listStart.isChecked()){
                    listStart.setChecked(false);
                    player.pause();
                }else{
                    listStart.setChecked(true);
                    player.play();
                }
                break;
            case R.id.list_last:
                if(index==0){
                    Toast.makeText(this, "已经是第一章了", Toast.LENGTH_SHORT).show();
                    return;
                }
                //选中位置刷新  上一曲
                index--;
                for (int  i =0; i<list.size() ; i ++){
                    list.get(i).setCheck(false);
                }
                list.get(index).setCheck(true);
                fileAdapter.notifyDataSetChanged();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        player.playUrl(list.get(index).getQuitePath());
                    }
                }).start();
                listStart.setChecked(true);
                break;
            case R.id.list_next:
                if(index==list.size()-1){
                    Toast.makeText(this, "已经是最后一章了", Toast.LENGTH_SHORT).show();
                    return;
                }
                //选中位置刷新  下一曲
                index++;
                for (int  i =0; i<list.size() ; i ++){
                    list.get(i).setCheck(false);
                }
                list.get(index).setCheck(true);
                fileAdapter.notifyDataSetChanged();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        player.playUrl(list.get(index).getQuitePath());
                    }
                }).start();
                listStart.setChecked(true);
                break;
        }
    }
    private Player player; // 播放器
    private Handler handler = new UIHandler();
    private static final int PROCESSING = 1;
    private static final int FAILURE = -1;
    private final class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESSING: // 更新进度
                    listProgressBar.setProgress(msg.getData().getInt("size"));
                    float num = (float) listProgressBar.getProgress()
                            / (float) listProgressBar.getMax();
                    int result = (int) (num * 100); // 计算进度
                    if (listProgressBar.getProgress() == listProgressBar.getMax()) { // 下载完成
                        Toast.makeText(QuiteDownFileActivity.this, "缓冲完成",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case FAILURE: // 下载失败
                    Toast.makeText(QuiteDownFileActivity.this, "缓冲失败",
                            Toast.LENGTH_LONG).show();
                    break;
                case 2 :
                    //单章进度保存入数据库
                    dbUtils = new CommonUtils(QuiteDownFileActivity.this);
                    Wypread wypread = new Wypread();
                    wypread = dbUtils.listOneWypread(list.get(index).getId());
                    if(wypread == null){
                        //插入数据库 听书总进度
                        dbUtils = new CommonUtils(QuiteDownFileActivity.this);
                        Wypread wypread1 = new Wypread();
                        wypread1.setId((long)list.get(index).getId());
                        wypread1.setQuitePosition(index+"");
                        wypread1.setQuiteChapter(progressSQL+"");
                        wypread1.setName(list.get(index).getName());
                        wypread1.setQuitePath(list.get(index).getQuitePath());
                        wypread1.setFromFile(getIntent().getStringExtra("path"));
                        wypread1.setImgPath(list.get(index).getImgPath());
                        dbUtils.insertWypread(wypread1);
                    }else{
                        //修改单章数据
                        Wypread wypread2 = new Wypread();
                        //根据ID来修改
                        wypread2.setId(list.get(index).getId());
                        //需要重新设置参数,只设置一位其它值变为null
                        wypread2.setQuiteChapter(progressSQL+"");
                        wypread2.setQuitePosition(index+"");
                        wypread2.setName(list.get(index).getName());
                        wypread2.setQuitePath(list.get(index).getQuitePath());
                        wypread2.setFromFile(getIntent().getStringExtra("path"));
                        wypread2.setImgPath(list.get(index).getImgPath());
                        dbUtils.updateWypread(wypread2);
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(2);
                        }
                    },5000);
                    break;
                case 0://自动播放下一曲
                    if(index==list.size()-1){
                        Toast.makeText(QuiteDownFileActivity.this, "已经是最后一章了", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    listStart.setChecked(true);
                    //选中位置刷新  下一曲
                    index++;
                    for (int  i =0; i<list.size() ; i ++){
                        list.get(i).setCheck(false);
                    }
                    list.get(index).setCheck(true);
                    fileAdapter.notifyDataSetChanged();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            player.playUrl(MyUrl.imgUrl+list.get(index).getQuitePath());
                        }
                    }).start();
                    listStart.setChecked(true);
                    break;
            }
        }
    }
    int  progressSQL =0;
    // 进度改变
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();

            progressSQL = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
            if((progressSQL+2500) >= player.mediaPlayer.getDuration() ||
                    player.mediaPlayer.isPlaying() == false ||
                    progress>98){
                listStart.setChecked(false);
                handler.sendEmptyMessage(0);
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            player.mediaPlayer.seekTo(progress);
        }
    }
    CommonUtils dbUtils;
    List<Wypread> wypreadList = new ArrayList<>();//数据库数据
    List<Wypread> list = new ArrayList<>();
    private void initData() {
        dbUtils = new CommonUtils(this);
        wypreadList = dbUtils.listAll();
        for (int i = 0; i < wypreadList.size(); i++) {
            if (getIntent().getStringExtra("path").equals(wypreadList.get(i).getFromFile())) {
                list.add(wypreadList.get(i));
            }
        }
        fileAdapter.notifyDataSetChanged();
    }

    private void initView() {
        fileAdapter = new FileAdapter(this, list, false);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listRV.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, 1, R.color.textColor));
        listRV.setLayoutManager(manager);
        listRV.setAdapter(fileAdapter);
        fileAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                for (int j = 0; j < list.size(); j++) {
                    list.get(j).setCheck(false);
                }
                list.get(i).setCheck(true);
                fileAdapter.notifyDataSetChanged();
                listStart.setChecked(true);
                listBottom.setVisibility(View.VISIBLE);
                CommonUtils   dbUtils = new CommonUtils(QuiteDownFileActivity.this);
                Wypread wypread = new Wypread();
                wypread = dbUtils.listOneWypread(list.get(i).getId());
                if(wypread == null || wypread.getQuiteChapter() == null){
                    playMusic(i,0);
                }else{
                    String s = wypread.getQuiteChapter();
                    int progresstion = Integer.valueOf(s);
                    playMusic(i,progresstion);
                }
                handler.sendEmptyMessage(2);
                index=i;

            }
        });

    }
    public void playMusic(final int position , final int progressPosition){
        for (int j = 0; j < list.size(); j++) {
            list.get(j).setCheck(false);
        }
        list.get(position).setCheck(true);
        fileAdapter.notifyDataSetChanged();

        new Thread(new Runnable() {
            @Override
            public void run() {
                player.playUrl(list.get(index).getQuitePath());
            }
        }).start();
        listStart.setChecked(true);
        listBottom.setVisibility(View.VISIBLE);
        index=position;
        new Handler().postDelayed(new Runnable(){
            public void run() {
                player.setPlayTime(progressPosition);
                player.onBufferingUpdate(player.mediaPlayer,progressPosition);
            }
        }, 500);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        if (player != null) {
            player.stop();
            player = null;
        }
    }

    @Override
    public int setBaseView() {
        return R.layout.fragment_list;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "音乐下载";
    }

    @Override
    public boolean showMore() {
        return false;
    }
}
