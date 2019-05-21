package com.sanmiao.wypread.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.adapter.QuiteDetailsListAdapter;
import com.sanmiao.wypread.bean.BookBean;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.bean.SectionList;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.ui.BookDtailsActivity;
import com.sanmiao.wypread.ui.QuiteDetailsActivity;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.Player;
import com.sanmiao.wypread.utils.RecycleViewDivider;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.UtilBox;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/4 0004.
 * 类说明{听书详情列表}
 */

public class QuiteDetailsListFragment extends Fragment {
    View thisView;
    @InjectView(R.id.list_progressBar)
    SeekBar listProgressBar;// 音乐进度
    @InjectView(R.id.list_nowTime)
    TextView listNowTime;//当前时间
    @InjectView(R.id.list_allTime)
    TextView listAllTime;//所有时间
    @InjectView(R.id.list_start)
    CheckBox listStart;//开始-暂停
    @InjectView(R.id.list_last)
    ImageView listLast;//下一曲
    @InjectView(R.id.list_next)
    ImageView listNext;//上一曲
    @InjectView(R.id.list_RV)
    RecyclerView listRV;
    @InjectView(R.id.list_num)
    TextView listNum;
    @InjectView(R.id.list_bottom)
    LinearLayout listBottom;
    QuiteDetailsListAdapter adapter;
    List<SectionList> list = new ArrayList<>();
    int  index=0;//当前音乐播放position
    String text="";
    String quiteId="";//整本id
    String id="";//单章ID
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        thisView = inflater.inflate(R.layout.fragment_list, null);
        ButterKnife.inject(this, thisView);
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

//        initData();
        initView();
        player = new Player(listProgressBar,listNowTime,listAllTime);
        listProgressBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());

        return thisView;
    }


    //设置布局
    private void initView() {
        adapter = new QuiteDetailsListAdapter(getActivity(), list, false);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listRV.addItemDecoration(new RecycleViewDivider(getActivity(),LinearLayoutManager.HORIZONTAL,1,R.color.textColor));
        listRV.setLayoutManager(manager);
        listRV.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                for (int j = 0; j < list.size(); j++) {
                    list.get(j).setCheck(false);
                }
                list.get(i).setCheck(true);
                adapter.notifyDataSetChanged();

                id = "101"+list.get(i).getId();
                int d =Integer.valueOf(id);
                CommonUtils   dbUtils = new CommonUtils(getActivity());
                Wypread wypread = new Wypread();
                wypread = dbUtils.listOneWypread(d);
                if(wypread == null){
                    playMusic(0,0);
                }else{
                    String p = wypread.getQuitePosition();
                    String s = wypread.getQuiteChapter();
                    int position= Integer.valueOf(p);
                    int progresstion = Integer.valueOf(s);
                    playMusic(position,progresstion);
                }
                listStart.setChecked(true);
                listBottom.setVisibility(View.VISIBLE);
                index=i;
                initReadBookNum();
                handler.sendEmptyMessage(2);
                isListen=true;
            }
        });
    }
    boolean isListen = false;

    //添加数据
    public void initData(String text) {
        this.text = text;
        RootBean bean = new Gson().fromJson(text,RootBean.class);
        listNum.setText("共"+ bean.getData().getBookDetails().getSectionList().size()+"章");
        for (int i = 0; i < bean.getData().getBookDetails().getSectionList().size(); i++) {
            SectionList sectionList= new SectionList();
            bookIdStr = "101"+bean.getData().getBookDetails().getBookID();
            bookId = Integer.valueOf(bookIdStr);
            sectionList.setId(bean.getData().getBookDetails().getSectionList().get(i).getId());
            sectionList.setBookId(bean.getData().getBookDetails().getBookID());
            sectionList.setWriteName(bean.getData().getBookDetails().getWriter());
            sectionList.setClassfly(bean.getData().getBookDetails().getClassifyTitle());
            sectionList.setBookImg(bean.getData().getBookDetails().getImgUrl());
            sectionList.setFileUrl(bean.getData().getBookDetails().getSectionList().get(i).getFileUrl());
            sectionList.setName(bean.getData().getBookDetails().getSectionList().get(i).getName());
            sectionList.setSize(bean.getData().getBookDetails().getSectionList().get(i).getSize());
            sectionList.setBookName(bean.getData().getBookDetails().getName());
            quiteId = bean.getData().getBookDetails().getBookID();
            id = bean.getData().getBookDetails().getSectionList().get(i).getId();
            adapter.setQuiteId(quiteId);
            list.add(sectionList);
        }
        adapter.notifyDataSetChanged();
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
                isListen=true;
                break;
            case R.id.list_last:
                if(index==0){
                    Toast.makeText(getActivity(), "已经是第一章了", Toast.LENGTH_SHORT).show();
                    return;
                }
                //选中位置刷新  上一曲
                index--;
                for (int  i =0; i<list.size() ; i ++){
                    list.get(i).setCheck(false);
                }
                list.get(index).setCheck(true);
                adapter.notifyDataSetChanged();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        player.playUrl(MyUrl.imgUrl+UtilBox.hanzi2utf(list.get(index).getFileUrl()));
                    }
                }).start();
                listStart.setChecked(true);
                break;
            case R.id.list_next:
                if(index==list.size()-1){
                    Toast.makeText(getActivity(), "已经是最后一章了", Toast.LENGTH_SHORT).show();
                    return;
                }
                //选中位置刷新  下一曲
                index++;
                for (int  i =0; i<list.size() ; i ++){
                    list.get(i).setCheck(false);
                }
                list.get(index).setCheck(true);
                adapter.notifyDataSetChanged();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        player.playUrl(MyUrl.imgUrl+UtilBox.hanzi2utf(list.get(index).getFileUrl()));
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
    int progressSQL = 0;//存入数据库的播放进度
    private final class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESSING: // 更新进度
                    listProgressBar.setProgress(msg.getData().getInt("size"));
                    float num = (float) listProgressBar.getProgress()
                            / (float) listProgressBar.getMax();
                    int result = (int) (num * 100); // 计算进度
                    if (listProgressBar.getProgress() == listProgressBar.getMax()) { // 下载完成
                        Toast.makeText(getActivity(), "缓冲完成", Toast.LENGTH_LONG).show();
                    }
                    break;
                case FAILURE: // 下载失败
                    Toast.makeText(getActivity(), "缓冲失败", Toast.LENGTH_LONG).show();
                    break;

                case 0://自动播放下一曲
                    if(index==list.size()-1){
                        Toast.makeText(getActivity(), "已经是最后一章了", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    listStart.setChecked(true);
                    //选中位置刷新  下一曲
                    index++;
                    for (int  i =0; i<list.size() ; i ++){
                        list.get(i).setCheck(false);
                    }
                    list.get(index).setCheck(true);
                    adapter.notifyDataSetChanged();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            player.playUrl(MyUrl.imgUrl+list.get(index).getFileUrl());
                        }
                    }).start();
                    listStart.setChecked(true);
                    break;
                case 2 :
                    //单章进度保存入数据库
                    id = "101"+list.get(index).getId();
                    int d = Integer.valueOf(id);
                    dbUtils = new CommonUtils(getActivity());
                    Wypread wypread = new Wypread();
                    wypread = dbUtils.listOneWypread(d);
                    if(wypread == null){
                        //插入数据库 听书总进度
                        dbUtils = new CommonUtils(getActivity());
                        Wypread wypread1 = new Wypread();
                        wypread1.setId((long)d);
                        wypread1.setQuitePosition(index+"");
                        wypread1.setQuiteChapter(progressSQL+"");
                        dbUtils.insertWypread(wypread1);
                    }else{
                        //修改单章数据
                        Wypread wypread2 = new Wypread();
                        int pos=Integer.valueOf(id);
                        //根据ID来修改
                        wypread2.setId((long)pos);
                        //需要重新设置参数,只设置一位其它值变为null
                        wypread2.setQuiteChapter(progressSQL+"");
                        wypread2.setQuitePosition(index+"");
                        dbUtils.updateWypread(wypread2);
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           handler.sendEmptyMessage(2);
                        }
                    },5000);
                    break;
            }
        }
    }
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

            if((progressSQL+2500) >= player.mediaPlayer.getDuration() &&
                    player.mediaPlayer.isPlaying() == false &&
                    progress>98){
                listStart.setChecked(false);
                handler.sendEmptyMessage(0);
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            player.mediaPlayer.seekTo(progress);
        }
    }

    public void playMusic(final int position , final int progressPosition){

        try {
            for (int j = 0; j < list.size(); j++) {
                list.get(j).setCheck(false);
            }
            list.get(position).setCheck(true);
            adapter.notifyDataSetChanged();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    player.playUrl(MyUrl.imgUrl + UtilBox.hanzi2utf(list.get(position).getFileUrl()));
                }
            }).start();
            listStart.setChecked(true);
            listBottom.setVisibility(View.VISIBLE);
            index = position;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    player.setPlayTime(progressPosition);
                    player.onBufferingUpdate(player.mediaPlayer, progressPosition);
                }
            }, 500);
        }catch (Exception e){}
    }

    /***统计阅读量*/
    private void initReadBookNum() {
        HashMap<String,String> map = new HashMap<>();
        map.put("bookId",quiteId);
        map.put("userId",SharedPreferenceUtil.getStringData("userId"));
        map.put("StatisticsType","1");
        OkHttpUtils.post().url(MyUrl.Statistics).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response) {
                RootBean bean = new Gson().fromJson(response,RootBean.class);
                if(bean.getResultCode() == 0){
                    QuiteDetailsActivity activity =(QuiteDetailsActivity)getActivity();
                    activity.refresh();
                }
            }
        });
    }


    public  void pauseMusic(){
        player.pause();
    }
    public  void pMusic(){
        player.play();
    }

    CommonUtils dbUtils;
    String bookIdStr = "";
    int bookId= 0;

    @Override
    public void onPause() {
        super.onPause();
        //听书进度%
        double i=list.size();
        double jindu = 0.0;
        if(isListen){
             jindu =  (index+1) /i *100  ;
        }else{
            jindu =  (index) /i *100  ;
        }

        int jin = (int) jindu;
        String jd = jin + "%";

        //进度保存入数据库
        dbUtils = new CommonUtils(getActivity());
        Wypread wypread = new Wypread();
        wypread = dbUtils.listOneWypread(bookId);
        if(wypread == null){
            RootBean bean = new Gson().fromJson(text,RootBean.class);
            //插入数据库 听书总进度
            dbUtils = new CommonUtils(getActivity());
            Wypread wypread1 = new Wypread();
            wypread1.setId((long)bookId);
            wypread1.setQuitePosition(index+"");
            wypread1.setQuiteChapter(progressSQL+"");
            wypread1.setQuiteJindu(jd);
            dbUtils.insertWypread(wypread1);
        }else{
            //修改总数据
            Wypread wypread2 = new Wypread();
            int pos=Integer.valueOf(bookId);
            //根据ID来修改
            wypread2.setId((long)pos);
            //需要重新设置参数,只设置一位其它值变为null
            wypread2.setQuiteChapter(progressSQL+"");
            wypread2.setQuitePosition(index+"");
            wypread2.setQuitePosition(index+"");
            wypread2.setQuiteJindu(jd);
            dbUtils.updateWypread(wypread2);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        if (player != null) {
            player.stop();
            player = null;
        }
    }

}
