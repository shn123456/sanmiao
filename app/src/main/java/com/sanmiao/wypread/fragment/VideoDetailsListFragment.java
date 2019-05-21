package com.sanmiao.wypread.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.adapter.QuiteDetailsListAdapter;
import com.sanmiao.wypread.adapter.VideoDetailsListAdapter;
import com.sanmiao.wypread.bean.BookBean;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.bean.SectionList;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.ui.BookDtailsActivity;
import com.sanmiao.wypread.ui.VideoDetailsActivity;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.RecycleViewDivider;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.UtilBox;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/5 0005.
 * 类说明{视频详情列表}
 */

public class VideoDetailsListFragment extends Fragment {
    View thisView;
    @InjectView(R.id.list_RV)
    RecyclerView listRV;//列表
    @InjectView(R.id.list_bottom)
    LinearLayout listBottom;
    @InjectView(R.id.list_num)
    TextView listNum;
    VideoDetailsListAdapter adapter;
    List<SectionList> list = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_list, null);
        ButterKnife.inject(this, thisView);
        listBottom.setVisibility(View.GONE);
        initView();
        return thisView;
    }

    //设置布局
    private void initView() {
        //图书布局
        adapter = new VideoDetailsListAdapter(getActivity(), list, false);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listRV.addItemDecoration(new RecycleViewDivider(getActivity(),LinearLayoutManager.HORIZONTAL,1,R.color.textColor));
        listRV.setLayoutManager(manager);
        listRV.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int j = 0; j < list.size(); j++) {
                    list.get(j).setCheck(false);
                }
                list.get(i).setCheck(true);
                adapter.notifyDataSetChanged();
                VideoDetailsActivity activity =(VideoDetailsActivity) getActivity();
                activity.showVideo(true);
                activity.initReadBookNum();
                activity.setVideoUrl(MyUrl.imgUrl+ UtilBox.hanzi2utf(list.get(i).getFileUrl()),list.get(i).getName());

                jd = (int)((i+1)*100 / list.size());
            }
        });
    }
    int jd=0;
    String text = "";
    int bookId = 0;
    //添加数据
    public void initData(String s) {
        this.text = s;
        try{
            RootBean bean = new Gson().fromJson(s,RootBean.class);
            listNum.setText("共"+bean.getData().getBookDetails().getSectionList().size()+"章");
            for (int i = 0; i < bean.getData().getBookDetails().getSectionList().size(); i++) {
                SectionList sectionList= new SectionList();
                sectionList.setSize(bean.getData().getBookDetails().getSectionList().get(i).getSize());
                sectionList.setFileUrl(bean.getData().getBookDetails().getSectionList().get(i).getFileUrl());
                sectionList.setName(bean.getData().getBookDetails().getSectionList().get(i).getName());
                sectionList.setBookImg(bean.getData().getBookDetails().getVideoUrl());
                String id =bean.getData().getBookDetails().getBookID();
                bookId = Integer.valueOf(id);
                list.add(sectionList);
            }
            adapter.notifyDataSetChanged();
        }catch (Exception e){}
    }
    CommonUtils dbUtils;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
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
            wypread1.setQuiteJindu(jd+"%");
            dbUtils.insertWypread(wypread1);
        }else{
            //修改总数据
            Wypread wypread2 = new Wypread();
            int pos=Integer.valueOf(bookId);
            //根据ID来修改
            wypread2.setId((long)pos);
            //需要重新设置参数,只设置一位其它值变为null
            wypread2.setQuiteJindu(jd+"%");
            dbUtils.updateWypread(wypread2);
        }
    }
}
