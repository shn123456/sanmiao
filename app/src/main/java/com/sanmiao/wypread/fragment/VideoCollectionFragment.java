package com.sanmiao.wypread.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.adapter.VideoCollectionAdapter;
import com.sanmiao.wypread.bean.CollectionBook;
import com.sanmiao.wypread.bean.FileBean;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.ui.VideoDetailsActivity;
import com.sanmiao.wypread.utils.FileUtils;
import com.sanmiao.wypread.utils.MyUrl;
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
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/5 0005.
 * 类说明{视频收藏}
 */

public class VideoCollectionFragment extends Fragment {
    View thisView;
    @InjectView(R.id.RV)
    RecyclerView RV;
    List<CollectionBook> list = new ArrayList<>();
    VideoCollectionAdapter adapter;
    @InjectView(R.id.lv)
    LinearLayout lv;
    int index = 0;
    int collectionSize = 0;
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
        initData();
        initView();
        return thisView;
    }

    //设置布局
    private void initView() {
        adapter = new VideoCollectionAdapter(getActivity(), list, false);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        RV.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.HORIZONTAL, 5, R.color.basecolor));
        RV.setLayoutManager(manager);
        RV.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (list.size() == 0 || list == null) {
                    return;
                }
                //判断是否编辑状态
                if (!list.get(0).isBianji()) {
                    if (i < index)
                        startActivity(new Intent(getActivity(), VideoDetailsActivity.class).putExtra("videoID", list.get(i).getBookID()));
                    else {
                        showDialog(list.get(i).getBookPath(), list.get(i).getName());

                    }
                } else {
                    list.get(i).setCheck(!list.get(i).isCheck());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        adapter.setOnItemLongClcikListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                if (pos >= collectionSize) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("确认删除?").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //删除导入文件
                            String s = list.get(pos).getBookID();
                            long id = Long.valueOf(s);
                            CommonUtils commonUtils = new CommonUtils(getActivity());
                            Wypread wypread = new Wypread();
                            wypread.setId((long) id);
                            commonUtils.deleteWypread(wypread);
                            list.remove(pos);
                            adapter.notifyDataSetChanged();
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();

                }
                return true;
            }
        });
    }

    //视频播放
    private void showDialog(String path, String name) {
        final Dialog dialog = new Dialog(getActivity(), R.style.MyDialogStyle2);
        View view = View.inflate(getActivity(), R.layout.dialog_video, null);
        JCVideoPlayer jc = (JCVideoPlayer) view.findViewById(R.id.collection_videoView);
        //dialog底部弹出
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        //点击事件
        jc.setUp(path, name);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                try {
                    JCVideoPlayer.releaseAllVideos();
                } catch (Exception e) {
                }
            }
        });
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        UtilBox.setMatchWith(dialog);
    }

    //设置数据
    private void initData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
        map.put("type", "0");
        map.put("pageIndex", "1");
        map.put("pageSize", "9");
        OkHttpUtils.post().url(MyUrl.myCollection).params(map).tag(getActivity()).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
//                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                RootBean bean = new Gson().fromJson(response, RootBean.class);
                if (bean.getResultCode() == 0) {
                    index = bean.getData().getCollect_video().size();
                    for (int i = 0; i < bean.getData().getCollect_video().size(); i++) {
                        String bookId = bean.getData().getCollect_video().get(i).getBookID();
                        int bId = Integer.valueOf(bookId);
                        Wypread wypread = new Wypread();
                        CommonUtils commonUtils = new CommonUtils(getActivity());
                        wypread = commonUtils.listOneWypread((long) bId);

                        CollectionBook collectionBook = new CollectionBook();
                        collectionBook.setName(bean.getData().getCollect_video().get(i).getName());
                        collectionBook.setImgUrl(bean.getData().getCollect_video().get(i).getImgUrl());
                        collectionBook.setBookID(bean.getData().getCollect_video().get(i).getBookID());
                        if (wypread != null) {
                            collectionBook.setSchedule(wypread.getQuiteJindu());
                        } else
                            collectionBook.setSchedule("0%");
                        list.add(collectionBook);

                        collectionSize = bean.getData().getCollect_book().size();

                    }
                }
                importBook();
                adapter.setSize(collectionSize);
                adapter.notifyDataSetChanged();
            }
        });


    }

    //插入数据库数据
    public void importBook() {
        CommonUtils commonUtils = new CommonUtils(getActivity());
        Wypread wypread = new Wypread();
        List<Wypread> w = new ArrayList<>();
        w = commonUtils.listAll();
        if (w != null) {
            for (int i = 0; i < w.size(); i++) {
                if ("video".equals(w.get(i).getFromFile())) {
                    CollectionBook bean = new CollectionBook();
                    bean.setBookID(w.get(i).getImgPath() + "");
                    bean.setName(w.get(i).getName());
                    bean.setBookPath(w.get(i).getBookPath());
                    bean.setSchedule(w.get(i).getReadJindu());
                    list.add(bean);
                }
            }
        }
//        adapter.notifyDataSetChanged();

    }

    //打开编辑状态
    public void setCheckOn() {
        for (int i = 0; i < list.size(); i++) {
            if (i < collectionSize) {
                list.get(i).setBianji(true);
            } else {
                list.get(i).setBianji(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    //关闭编辑状态
    public void setCheckOff() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setBianji(false);
        }
        adapter.notifyDataSetChanged();
    }

    //取消收藏
    public void moveCollction() {
        String id = "";
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).isCheck()) {
//                list.remove(i);
                id += list.get(i).getBookID() + ",";
            }
            adapter.notifyDataSetChanged();
        }
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(getActivity(), "请选择收藏的视频", Toast.LENGTH_SHORT).show();
            return;
        }

        id = id.substring(0, id.length() - 1);
        HashMap<String, String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
        map.put("bookID", id);
        map.put("type", "1");
        OkHttpUtils.post().url(MyUrl.collection).params(map).tag(getActivity()).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                RootBean bean = new Gson().fromJson(response, RootBean.class);
                Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
                if (bean.getResultCode() == 0) {
                    for (int i = list.size() - 1; i >= 0; i--) {
                        if (list.get(i).isCheck()) {
                            list.remove(i);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    //导入本地TXT/PDF文件
    public void getVideo() {
        UtilBox.showDialog(getActivity(), "正在扫描,请稍候");
        FileUtils fileUtils = new FileUtils();
        List<FileBean> fileBeanList = new ArrayList<>();
        fileBeanList = fileUtils.getVideo();
        for (int i = 0; i < fileBeanList.size(); i++) {
            CollectionBook bean = new CollectionBook();
            bean.setBookName(fileBeanList.get(i).getName());
            bean.setBookPath(fileBeanList.get(i).getPath());
            list.add(bean);
        }
        adapter.notifyDataSetChanged();

        UtilBox.dismissDialog();
    }

    public void refresh() {
        list.clear();
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}
