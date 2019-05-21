package com.sanmiao.wypread.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.adapter.BookCollectionAdapter;
import com.sanmiao.wypread.bean.CollectionBook;
import com.sanmiao.wypread.bean.FileBean;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.ui.BookDtailsActivity;
import com.sanmiao.wypread.ui.PDFActivity;
import com.sanmiao.wypread.ui.ReadBookActivity;
import com.sanmiao.wypread.utils.FileUtils;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.RecycleViewDivider;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.UtilBox;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/5 0005.
 * 类说明{图书收藏}
 */

public class BookCollectionFragment extends Fragment {
    View thisView;
    @InjectView(R.id.RV)
    RecyclerView RV;
    List<CollectionBook> list = new ArrayList<>();
    BookCollectionAdapter adapter;
    @InjectView(R.id.lv)
    LinearLayout lv;
    int index = 0;
    int collectionSize = 0;
    @InjectView(R.id.collection_refresh)
    TwinklingRefreshLayout collectionRefresh;
    private Handler handler= new Handler(Looper.getMainLooper());//初始化handler

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
        adapter = new BookCollectionAdapter(getActivity(), list, false);
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
                    if (i < index) {
                        startActivity(new Intent(getActivity(), BookDtailsActivity.class).putExtra("bookID", list.get(i).getBookID()));
                    } else {
                        try {
                            String str[] = list.get(i).getBookPath().split("\\.");
                            if("PDF".equals(str[str.length - 1].toUpperCase())){
                                startActivity(new Intent(getActivity(), PDFActivity.class).putExtra("bookName", list.get(i).getName()).putExtra("filePath", list.get(i).getBookPath()).putExtra("bookID", list.get(i).getBookID()).putExtra("fromFile", "book"));
                            }else
                                startActivity(new Intent(getActivity(), ReadBookActivity.class).putExtra("bookName", list.get(i).getName()).putExtra("bookPath", list.get(i).getBookPath()).putExtra("bookID", list.get(i).getBookID()).putExtra("fromFile", "book"));
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }

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


    //设置数据
    private void initData() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
//        map.put("type", "0");
//        map.put("pageIndex", "1");
//        map.put("pageSize", "9");

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("userID", SharedPreferenceUtil.getStringData("userId"))
                .add("type", "0")
                .add("pageIndex", "1")
                .add("pageSize", "9")
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(MyUrl.myCollection)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                String res = "";
                try {
                    res = response.body().string();
                }
                catch (Exception ex) {
                    return;
                }
                RootBean bean = new Gson().fromJson(res, RootBean.class);
                if (bean.getResultCode() == 0) {
                    for (int i = 0; i < bean.getData().getCollect_book().size(); i++) {
                        String bookId = bean.getData().getCollect_book().get(i).getBookID();
                        int bId = Integer.valueOf(bookId);
                        Wypread wypread = new Wypread();
                        CommonUtils commonUtils = new CommonUtils(getActivity());
                        wypread = commonUtils.listOneWypread((long) bId);

                        CollectionBook collectionBook = new CollectionBook();
                        collectionBook.setName(bean.getData().getCollect_book().get(i).getName());
                        collectionBook.setImgUrl(bean.getData().getCollect_book().get(i).getImgUrl());
                        collectionBook.setBookID(bean.getData().getCollect_book().get(i).getBookID());
                        if (wypread != null) {
                            if (TextUtils.isEmpty(wypread.getReadJindu())) {
                                collectionBook.setSchedule("0%");
                            } else
                                collectionBook.setSchedule(wypread.getReadJindu());

                        } else
                            collectionBook.setSchedule("0%");
                        list.add(collectionBook);
                        collectionSize = bean.getData().getCollect_book().size();
                        index = bean.getData().getCollect_book().size();
                    }
                }
                importBook();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

//        OkHttpUtils.post().url(MyUrl.myCollection).params(map).tag(getActivity()).build().execute(new StringCallback() {
//            @Override
//            public void onError(Request request, Exception e) {
////                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(String response) {
//                RootBean bean = new Gson().fromJson(response, RootBean.class);
//                if (bean.getResultCode() == 0) {
//                    for (int i = 0; i < bean.getData().getCollect_book().size(); i++) {
//                        String bookId = bean.getData().getCollect_book().get(i).getBookID();
//                        int bId = Integer.valueOf(bookId);
//                        Wypread wypread = new Wypread();
//                        CommonUtils commonUtils = new CommonUtils(getActivity());
//                        wypread = commonUtils.listOneWypread((long) bId);
//
//                        CollectionBook collectionBook = new CollectionBook();
//                        collectionBook.setName(bean.getData().getCollect_book().get(i).getName());
//                        collectionBook.setImgUrl(bean.getData().getCollect_book().get(i).getImgUrl());
//                        collectionBook.setBookID(bean.getData().getCollect_book().get(i).getBookID());
//                        if (wypread != null) {
//                            if (TextUtils.isEmpty(wypread.getReadJindu())) {
//                                collectionBook.setSchedule("0%");
//                            } else
//                                collectionBook.setSchedule(wypread.getReadJindu());
//
//                        } else
//                            collectionBook.setSchedule("0%");
//                        list.add(collectionBook);
//                        collectionSize = bean.getData().getCollect_book().size();
//                        index = bean.getData().getCollect_book().size();
//                    }
//                }
//                importBook();
//                adapter.notifyDataSetChanged();
//            }
//        });
    }

    //插入数据库数据
    public void importBook() {
        CommonUtils commonUtils = new CommonUtils(getActivity());
        Wypread wypread = new Wypread();
        List<Wypread> w = new ArrayList<>();
        w = commonUtils.listAll();
        if (w != null) {
            for (int i = 0; i < w.size(); i++) {
                if ("book".equals(w.get(i).getFromFile())) {
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


    //导入本地TXT/PDF文件
    public void getTxt() {
        UtilBox.showDialog(getActivity(), "正在扫描,请稍候");
        FileUtils fileUtils = new FileUtils();
        List<FileBean> fileBeanList = new ArrayList<>();
        fileBeanList = fileUtils.getTxtPdf();
        for (int i = 0; i < fileBeanList.size(); i++) {
            CollectionBook bean = new CollectionBook();
            bean.setBookName(fileBeanList.get(i).getName());
            bean.setBookPath(fileBeanList.get(i).getPath());
            list.add(bean);
        }
        adapter.notifyDataSetChanged();

        UtilBox.dismissDialog();
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
        String ids = "";
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).isCheck()) {
                ids += list.get(i).getBookID() + ",";
            }
        }
        adapter.notifyDataSetChanged();
        if (TextUtils.isEmpty(ids)) {
            Toast.makeText(getActivity(), "请选择收藏的图书", Toast.LENGTH_SHORT).show();
            return;
        }

        ids = ids.substring(0, ids.length() - 1);
        HashMap<String, String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
        map.put("bookID", ids);
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
