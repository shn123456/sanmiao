package com.sanmiao.wypread.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.adapter.QuiteCollectionAdapter;
import com.sanmiao.wypread.bean.CollectionBook;
import com.sanmiao.wypread.bean.FileBean;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.ui.QuiteDetailsActivity;
import com.sanmiao.wypread.utils.FileUtils;
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

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/5 0005.
 * 类说明{听书收藏}
 */

public class QuiteCollectionFragment extends Fragment {
    View thisView;
    @InjectView(R.id.RV)
    RecyclerView RV;
    List<CollectionBook> list = new ArrayList<>();
    QuiteCollectionAdapter adapter;
    @InjectView(R.id.lv)
    LinearLayout lv;
    int index = 0;
    @InjectView(R.id.collection_refresh)
    TwinklingRefreshLayout collectionRefresh;
    private Player player; // 播放器
    int collectionSize = 0;

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
        adapter = new QuiteCollectionAdapter(getActivity(), list, false);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        RV.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.HORIZONTAL, 5, R.color.basecolor));
        RV.setLayoutManager(manager);
        RV.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (list.size() == 0 || list == null) {
                    return;
                }
                //判断是否编辑状态
                if (!list.get(0).isBianji()) {
                    if (i < index)
                        startActivity(new Intent(getActivity(), QuiteDetailsActivity.class).putExtra("quiteID", list.get(i).getBookID()));
                    else {
                        showDialog(list.get(i).getBookPath());
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

    SeekBar seekBar;

    //音频播放
    private void showDialog(final String path) {

        final Dialog dialog = new Dialog(getActivity(), R.style.MyDialogStyle2);
        View view = View.inflate(getActivity(), R.layout.dialog_music, null);
        seekBar = (SeekBar) view.findViewById(R.id.collection_seekBar);
        final ImageView start = (ImageView) view.findViewById(R.id.collection_start);
        ImageView stop = (ImageView) view.findViewById(R.id.collection_stop);
        start.setSelected(true);
        player = new Player(seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());

        new Thread(new Runnable() {
            @Override
            public void run() {
                player.playUrl(path);
            }
        }).start();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start.isSelected()) {
                    start.setSelected(false);
                    player.pause();
                } else {
                    start.setSelected(true);
                    player.play();
                }

            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.stop();
                dialog.dismiss();
            }
        });
        //dialog底部弹出
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                player.stop();
            }
        });
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        UtilBox.setMatchWith(dialog);
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
//            seekBar.setProgress(progress);
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

    private Handler handler = new UIHandler();
    private static final int PROCESSING = 1;
    private static final int FAILURE = -1;

    private final class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESSING: // 更新进度
                    seekBar.setProgress(msg.getData().getInt("size"));
                    float num = (float) seekBar.getProgress()
                            / (float) seekBar.getMax();
                    int result = (int) (num * 100); // 计算进度
                    if (seekBar.getProgress() == seekBar.getMax()) { // 下载完成
                        Toast.makeText(getActivity(), "缓冲完成", Toast.LENGTH_LONG).show();
                    }
                    break;
                case FAILURE: // 下载失败
                    Toast.makeText(getActivity(), "缓冲失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
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
                    for (int i = 0; i < bean.getData().getCollect_quite().size(); i++) {
                        String bookId = "101" + bean.getData().getCollect_quite().get(i).getBookID();
                        int bId = Integer.valueOf(bookId);
                        Wypread wypread = new Wypread();
                        CommonUtils commonUtils = new CommonUtils(getActivity());
                        wypread = commonUtils.listOneWypread((long) bId);

                        CollectionBook collectionBook = new CollectionBook();
                        collectionBook.setName(bean.getData().getCollect_quite().get(i).getName());
                        collectionBook.setImgUrl(bean.getData().getCollect_quite().get(i).getImgUrl());
                        collectionBook.setBookID(bean.getData().getCollect_quite().get(i).getBookID());
                        if (wypread != null) {
                            collectionBook.setSchedule(wypread.getQuiteJindu());
                        } else
                            collectionBook.setSchedule("0%");
                        list.add(collectionBook);
                    }
                    index = bean.getData().getCollect_quite().size();
                    collectionSize = bean.getData().getCollect_quite().size();
                }
                importBook();
                adapter.setSize(collectionSize);
                adapter.notifyDataSetChanged();
            }
        });
    }

    //导入本地mp3文件
    public void getMp3() {
        UtilBox.showDialog(getActivity(), "正在扫描,请稍候");
        FileUtils fileUtils = new FileUtils();
        List<FileBean> fileBeanList = new ArrayList<>();
        fileBeanList = fileUtils.getMP3();
        for (int i = 0; i < fileBeanList.size(); i++) {
            CollectionBook bean = new CollectionBook();
            bean.setBookName(fileBeanList.get(i).getName());
            bean.setBookPath(fileBeanList.get(i).getPath());
            list.add(bean);
        }
        adapter.notifyDataSetChanged();

        UtilBox.dismissDialog();
    }

    //插入数据库数据
    public void importBook() {
        CommonUtils commonUtils = new CommonUtils(getActivity());
        Wypread wypread = new Wypread();
        List<Wypread> w = new ArrayList<>();
        w = commonUtils.listAll();
        if (w != null) {
            for (int i = 0; i < w.size(); i++) {
                if ("quite".equals(w.get(i).getFromFile())) {
                    CollectionBook bean = new CollectionBook();
                    bean.setBookID(w.get(i).getImgPath() + "");
                    bean.setName(w.get(i).getName());
                    bean.setBookPath(w.get(i).getBookPath());
                    bean.setSchedule(w.get(i).getReadJindu());
                    list.add(bean);
                }
            }
        }
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
            Toast.makeText(getActivity(), "请选择收藏的听书", Toast.LENGTH_SHORT).show();
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
