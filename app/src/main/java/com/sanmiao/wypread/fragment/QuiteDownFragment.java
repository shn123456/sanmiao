package com.sanmiao.wypread.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.adapter.QuiteDownAdapter;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.ui.QuiteDownFileActivity;
import com.sanmiao.wypread.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/6 0006.
 * 类说明{听书下载}
 */

public class QuiteDownFragment extends Fragment {
    View thisView;
    @InjectView(R.id.RV)
    RecyclerView RV;
    QuiteDownAdapter adapter;
    List<Wypread> list = new ArrayList<>();
    List<Wypread> list2 = new ArrayList<>();
    @InjectView(R.id.lv)
    LinearLayout lv;
    @InjectView(R.id.collection_refresh)
    TwinklingRefreshLayout collectionRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_collection, null);
        ButterKnife.inject(this, thisView);
        lv.setVisibility(View.GONE);
        collectionRefresh.setEnableRefresh(false);
        collectionRefresh.setEnableLoadmore(false);
        initView();
        initData();
        return thisView;
    }

    CommonUtils dbUtils;

    //设置数据
    private void initData() {
        dbUtils = new CommonUtils(getActivity());
        list = dbUtils.listAll();
        //区分听书 和读书
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.isEmpty(list.get(i).getBookPath()) && !TextUtils.isEmpty(list.get(i).getQuitePath()) && "0".equals(list.get(i).getIsDown())) {
                list2.add(list.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

    //设置布局
    private void initView() {
        adapter = new QuiteDownAdapter(getActivity(), list2, false);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        RV.setLayoutManager(manager);
        RV.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (list2.size() == 0 || list2 == null) {
                    return;
                }
                //判断是否为编辑状态
                if (!list2.get(0).isBianji()) {
                    startActivity(new Intent(getActivity(), QuiteDownFileActivity.class).putExtra("name", list2.get(i).getName()).putExtra("path", list2.get(i).getQuitePath()).putExtra("img", list2.get(i).getImgPath()));
                } else {
                    list2.get(i).setCheck(!list2.get(i).isCheck());
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    //开启编辑状态
    public void setCheckOn() {
        for (int i = 0; i < list2.size(); i++) {
            list2.get(i).setBianji(true);
        }
        adapter.notifyDataSetChanged();
    }

    //关闭编辑状态
    public void setCheckOff() {
        for (int i = 0; i < list2.size(); i++) {
            list2.get(i).setBianji(false);
        }
        adapter.notifyDataSetChanged();
    }

    //删除下载
    public void moveDown() {
        list3.clear();
        List<Wypread> l = new ArrayList<>();
        //查询所有数据遍历本书所包含章节全部删除
        l = dbUtils.listAll();
        for (int i = list2.size() - 1; i >= 0; i--) {
            if (list2.get(i).isCheck()) {
                //删除本地文件
                File file = new File(list2.get(i).getQuitePath());
                FileUtils fileUtils = new FileUtils();
                fileUtils.delete(file);
                //
                for (int j = 0; j < l.size(); j++) {
                    if (list2.get(i).getQuitePath().equals(l.get(j).getFromFile())) {
                        Wypread w = new Wypread();
                        w.setId(l.get(j).getId());
                        dbUtils.deleteWypread(w);
                    }
                }
                //删除数据库数据
                Wypread wypread = new Wypread();
                wypread.setId(list2.get(i).getId());
                dbUtils.deleteWypread(wypread);
                list2.remove(i);
                list3.add(wypread);
            }

        }

        if (list3.size() == 0)
            Toast.makeText(getActivity(), "请选择删除文件", Toast.LENGTH_SHORT).show();

        adapter.notifyDataSetChanged();
    }

    List<Wypread> list3 = new ArrayList<>();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}