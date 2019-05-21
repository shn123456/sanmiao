package com.sanmiao.wypread.ui;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.adapter.BookDetailsListAdapter;
import com.sanmiao.wypread.bean.Likes;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.bean.RootBeanEx;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.utils.GlideUtil;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.UtilBox;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/4 0004.
 * 类说明{图书详情}
 */

public class BookDtailsActivity extends BaseActivity {
    @InjectView(R.id.bookDetails_img)
    ImageView detailsImg;//书籍封面
    @InjectView(R.id.bookDetails_title)
    TextView detailsTitle;//标题
    @InjectView(R.id.bookDetails_name)
    TextView detailsName;//作者
    @InjectView(R.id.bookDetails_class)
    TextView detailsClass;//分类
    @InjectView(R.id.bookDetails_readNum)
    TextView detailsReadNum;//阅读数量
    @InjectView(R.id.bookDetails_downNum)
    TextView detailsDownNum;//下载数量
    @InjectView(R.id.bookDetails_read)
    TextView detailsRead;//阅读
    @InjectView(R.id.bookDetails_collation)
    CheckBox detailsCollation;//收藏
    @InjectView(R.id.bookDetails_allDown)
    TextView detailsAllDown;//全本下载
    @InjectView(R.id.bookDetails_jianjie)
    TextView detailsJianjie;//简介
    @InjectView(R.id.bookDetails_allJianjie)
    TextView detailsAllJianjie;//查看更多详情
    @InjectView(R.id.bookDetails_RV)
    RecyclerView detailsRV;//猜你喜欢列表
    @InjectView(R.id.bookDetails_jianjie2)
    TextView detailsJianjie2;//全部简介
    @InjectView(R.id.down_seek)
    ProgressBar downSeek;//下载进度
    @InjectView(R.id.down_start)
    ImageView downStart;//开始下载--暂停下载
    @InjectView(R.id.down_error)
    ImageView downError;//取消下载
    @InjectView(R.id.down_View)
    LinearLayout downView;//下载布局
    @InjectView(R.id.down_jindu)
    TextView downJindu;
    boolean isDown = false;//判断是否下载完成
    CommonUtils dbUtils;//数据库工具
    List<Likes> list = new ArrayList<>();
    BookDetailsListAdapter adapter;
    String filePath = "";
    String name = "";
    String bookId = "";
    String imgPath = "";
    String write = "";
    String classfly = "";
    int bookIdInt = 0;
    @InjectView(R.id.bookDeatils_lv)
    LinearLayout bookDeatilsLv;
    int index = 0;

    private Handler handler1= new Handler(Looper.getMainLooper());//初始化handler

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        detailsCollation.setPadding(20, 10, 10, 10);
        int i = SharedPreferenceUtil.getIntData("bgcolor");
        if (i == 1) {
            detailsCollation.setButtonDrawable(R.drawable.collection1);
        } else if (i == 2) {
            detailsCollation.setButtonDrawable(R.drawable.collection2);
        } else if (i == 3) {
            detailsCollation.setButtonDrawable(R.drawable.collection3);
        } else if (i == 4) {
            detailsCollation.setButtonDrawable(R.drawable.collection4);
        } else if (i == 5) {
            detailsCollation.setButtonDrawable(R.drawable.collection5);
        } else {
            detailsCollation.setButtonDrawable(R.drawable.collection);
        }

        initView();
        initData();
    }

    private void initData() {
        UtilBox.showDialog(this, "加载数据,请稍候");
        HashMap<String, String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
        map.put("bookID", getIntent().getStringExtra("bookID"));

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("userID", SharedPreferenceUtil.getStringData("userId"))
                .add("bookID", getIntent().getStringExtra("bookID"))
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(MyUrl.bookDetails)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        UtilBox.dismissDialog();
                        Toast.makeText(BookDtailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
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
                list.clear();
                UtilBox.dismissDialog();
                final RootBean bean = new Gson().fromJson(res, RootBean.class);
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bean.getResultCode() == 0) {
                            if (!TextUtils.isEmpty(bean.getData().getBookDetails().getImgUrl()))
                                GlideUtil.ShowImage(BookDtailsActivity.this, MyUrl.imgUrl + bean.getData().getBookDetails().getImgUrl(), detailsImg);
                            else
                                detailsImg.setImageResource(R.mipmap.icon_zhanweitu2);

                            detailsTitle.setText(bean.getData().getBookDetails().getName());
                            detailsName.setText("作者:  " + bean.getData().getBookDetails().getWriter());
                            detailsClass.setText("分类:  " + bean.getData().getBookDetails().getClassifyTitle());
                            detailsReadNum.setText("阅读量:  " + bean.getData().getBookDetails().getLookCount());
                            detailsDownNum.setText("下载量: " + bean.getData().getBookDetails().getDownCount());

                            if (TextUtils.isEmpty(bean.getData().getBookDetails().getIntro())) {
                                detailsJianjie.setText("暂无简介内容");
                                detailsJianjie2.setText("暂无简介内容");
                            } else {
                                detailsJianjie.setText(bean.getData().getBookDetails().getIntro());
                                detailsJianjie2.setText(bean.getData().getBookDetails().getIntro());
                            }


                            if ("1".equals(bean.getData().getBookDetails().getCollect())) {
                                detailsCollation.setTextColor(getResources().getColor(R.color.basecolor));
                                detailsCollation.setSelected(true);
                                detailsCollation.setText("已收藏");
                            } else {
                                detailsCollation.setText("收藏");
                                detailsCollation.setTextColor(getResources().getColor(R.color.black));
                                detailsCollation.setSelected(false);
                            }

                            if(bean.getData().getBookDetails().getLikes().size() ==0){
                                bookDeatilsLv.setVisibility(View.GONE);
                            }else{
                                list.addAll(bean.getData().getBookDetails().getLikes());
                            }
                            adapter.notifyDataSetChanged();

                            filePath = bean.getData().getBookDetails().getFileUrl();
                            if (UtilBox.isPDF(filePath)) {
                                name = UtilBox.getName(bean.getData().getBookDetails().getFileUrl());
                            } else {
                                name = UtilBox.getName(bean.getData().getBookDetails().getFileUrl());
                            }
                            bookId = bean.getData().getBookDetails().getBookID();
                            imgPath = bean.getData().getBookDetails().getImgUrl();
                            write = bean.getData().getBookDetails().getWriter();
                            classfly = bean.getData().getBookDetails().getClassifyTitle();
                            bookIdInt = Integer.valueOf(bookId);
                        }
                    }
                });

            }
        });

//        OkHttpUtils.post().url(MyUrl.bookDetails).params(map).tag(this).build().execute(new StringCallback() {
//            @Override
//            public void onError(Request request, Exception e) {
//                UtilBox.dismissDialog();
//                Toast.makeText(BookDtailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(String response) {
//                list.clear();
//                UtilBox.dismissDialog();
//                RootBean bean = new Gson().fromJson(response, RootBean.class);
//                if (bean.getResultCode() == 0) {
//                    if (!TextUtils.isEmpty(bean.getData().getBookDetails().getImgUrl()))
//                        GlideUtil.ShowImage(BookDtailsActivity.this, MyUrl.imgUrl + bean.getData().getBookDetails().getImgUrl(), detailsImg);
//                    else
//                        detailsImg.setImageResource(R.mipmap.icon_zhanweitu2);
//
//                    detailsTitle.setText(bean.getData().getBookDetails().getName());
//                    detailsName.setText("作者:  " + bean.getData().getBookDetails().getWriter());
//                    detailsClass.setText("分类:  " + bean.getData().getBookDetails().getClassifyTitle());
//                    detailsReadNum.setText("阅读量:  " + bean.getData().getBookDetails().getLookCount());
//                    detailsDownNum.setText("下载量: " + bean.getData().getBookDetails().getDownCount());
//
//                    if (TextUtils.isEmpty(bean.getData().getBookDetails().getIntro())) {
//                        detailsJianjie.setText("暂无简介内容");
//                        detailsJianjie2.setText("暂无简介内容");
//                    } else {
//                        detailsJianjie.setText(bean.getData().getBookDetails().getIntro());
//                        detailsJianjie2.setText(bean.getData().getBookDetails().getIntro());
//                    }
//
//
//                    if ("1".equals(bean.getData().getBookDetails().getCollect())) {
//                        detailsCollation.setTextColor(getResources().getColor(R.color.basecolor));
//                        detailsCollation.setSelected(true);
//                        detailsCollation.setText("已收藏");
//                    } else {
//                        detailsCollation.setText("收藏");
//                        detailsCollation.setTextColor(getResources().getColor(R.color.black));
//                        detailsCollation.setSelected(false);
//                    }
//
//                    if(bean.getData().getBookDetails().getLikes().size() ==0){
//                        bookDeatilsLv.setVisibility(View.GONE);
//                    }else{
//                        list.addAll(bean.getData().getBookDetails().getLikes());
//                    }
//                    adapter.notifyDataSetChanged();
//
//                    filePath = bean.getData().getBookDetails().getFileUrl();
//                    if (UtilBox.isPDF(filePath)) {
//                        name = UtilBox.getName(bean.getData().getBookDetails().getFileUrl());
//                    } else {
//                        name = UtilBox.getName(bean.getData().getBookDetails().getFileUrl());
//                    }
//                    bookId = bean.getData().getBookDetails().getBookID();
//                    imgPath = bean.getData().getBookDetails().getImgUrl();
//                    write = bean.getData().getBookDetails().getWriter();
//                    classfly = bean.getData().getBookDetails().getClassifyTitle();
//                    bookIdInt = Integer.valueOf(bookId);
//                }
//            }
//        });
    }

    //设置布局
    private void initView() {
        adapter = new BookDetailsListAdapter(this, list, false);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        detailsRV.setLayoutManager(manager);
        detailsRV.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(BookDtailsActivity.this, BookDtailsActivity.class).putExtra("bookID", list.get(i).getBookID()));
            }
        });

    }

    @OnClick({R.id.bookDetails_read, R.id.bookDetails_collation, R.id.bookDetails_allDown, R.id.bookDetails_allJianjie, R.id.down_start, R.id.down_error})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.bookDetails_read:
                UtilBox.showDialog(this, "加载中,请稍候");
                dbUtils = new CommonUtils(this);
                Wypread wypread = new Wypread();
                wypread = dbUtils.listOneWypread(bookIdInt);
                if (wypread == null) {
//                  判断文件进度 如果下载完成则删除重新下载 是否存在  如果存在则删除
                    File file = new File("/mnt/sdcard/wypread/" + name);
                    if (file.exists()) {
                        //文件存在并且isDown为1时 为下载文件
                        file.delete();
                    }
                    HttpUtils http = new HttpUtils();
                    handler = http.download(MyUrl.imgUrl + UtilBox.hanzi2utf2(filePath),
                            "/mnt/sdcard/wypread/" + name, true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                            false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                            new RequestCallBack<File>() {
                                @Override
                                public void onStart() {}
                                @Override
                                public void onLoading(long total, long current, boolean isUploading) {}
                                @Override
                                public void onSuccess(ResponseInfo<File> responseInfo) {
                                    Wypread  wypread1 =new Wypread();
                                    wypread1 =dbUtils.listOneWypread((long)bookIdInt);
                                    if(wypread1 == null){
                                        //插入数据库
                                        dbUtils = new CommonUtils(BookDtailsActivity.this);
                                        Wypread wypread = new Wypread();
                                        wypread.setName(name);
                                        if (UtilBox.isPDF(filePath)) {
                                            wypread.setBookPath("/mnt/sdcard/wypread/" + name );
                                        } else {
                                            wypread.setBookPath("/mnt/sdcard/wypread/" + name);
                                        }
                                        wypread.setId((long) bookIdInt);
                                        wypread.setReadJindu("0%");
                                        wypread.setImgPath(imgPath);
                                        wypread.setWriteName(write);
                                        wypread.setClassfiy(classfly);
                                        if(index == 0){
                                            wypread.setIsDown("0");
                                        }else{
                                            wypread.setIsDown("1");
                                        }
                                        dbUtils.insertWypread(wypread);
                                    }else{
                                        dbUtils = new CommonUtils(BookDtailsActivity.this);
                                        Wypread wypread = new Wypread();
                                        wypread.setName(name);
                                        if (UtilBox.isPDF(filePath)) {
                                            wypread.setBookPath("/mnt/sdcard/wypread/" + name);
                                        } else {
                                            wypread.setBookPath("/mnt/sdcard/wypread/" + name);
                                        }
                                        wypread.setId((long) bookIdInt);
                                        wypread.setImgPath(imgPath);
                                        wypread.setWriteName(write);
                                        wypread.setReadJindu("0%");
                                        wypread.setClassfiy(classfly);
                                        if(index == 0){
                                            wypread.setIsDown("0");
                                        }else{
                                            wypread.setIsDown("1");
                                        }
                                        dbUtils.updateWypread(wypread);
                                    }
                                    UtilBox.dismissDialog();

                                    if (UtilBox.isPDF(filePath)) {
                                        startActivity(new Intent(BookDtailsActivity.this, PDFActivity.class).putExtra("bookPath", "/mnt/sdcard/wypread/" + name).putExtra("bookName", name).putExtra("bookID", bookId)
                                                .putExtra("name", name).putExtra("imgPath", imgPath).putExtra("writer", write)
                                                .putExtra("classfiy", classfly).putExtra("filePath", "/mnt/sdcard/wypread/" + name).putExtra("isDown",index+""));
                                    } else {
                                        startActivity(new Intent(BookDtailsActivity.this, ReadBookActivity.class).putExtra("bookPath", "/mnt/sdcard/wypread/" + name).putExtra("bookName", name).putExtra("bookID", bookId)
                                                .putExtra("name", name).putExtra("imgPath", imgPath).putExtra("writer", write)
                                                .putExtra("classfiy", classfly).putExtra("isDown",index+""));
                                    }

                                }

                                @Override
                                public void onFailure(HttpException error, String msg) {
                                    UtilBox.dismissDialog();
                                    Log.e("阅读失败",msg);
                                    Toast.makeText(BookDtailsActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    UtilBox.dismissDialog();
                    if (UtilBox.isPDF(filePath)) {
                        startActivity(new Intent(BookDtailsActivity.this, PDFActivity.class).putExtra("bookPath", "/mnt/sdcard/wypread/" + name).putExtra("bookName", name).putExtra("bookID", bookId)
                                .putExtra("name", name).putExtra("imgPath", imgPath).putExtra("writer", write)
                                .putExtra("classfiy", classfly).putExtra("isDown", "1").putExtra("filePath", wypread.getBookPath()));
                    } else
                        startActivity(new Intent(this, ReadBookActivity.class).putExtra("bookPath", wypread.getBookPath()).putExtra("bookName", wypread.getName()).putExtra("bookName", name).putExtra("bookID", bookId)
                                .putExtra("name", wypread.getName()).putExtra("imgPath", wypread.getImgPath()).putExtra("writer", wypread.getWriteName())
                                .putExtra("classfiy", wypread.getClassfiy()).putExtra("isDown", "1"));
                }


                initReadBookNum();
                break;
            case R.id.bookDetails_collation:
                if (!detailsCollation.isSelected()) {
                    collection("0");
                } else {
                    collection("1");
                }
                break;
            case R.id.bookDetails_allDown:
                index = 1;
                downView.setVisibility(View.VISIBLE);

                isDown = true;
                downStart.setSelected(true);
//              判断文件进度 如果下载完成则删除重新下载 是否存在  如果存在则删除
                if (isDown) {
                    File file = new File("/mnt/sdcard/wypread/" + name);
                    if (file.exists()) {
                        file.delete();
                    }
                    //判断数据库是否存在此条数据  存在则删除
                    dbUtils = new CommonUtils(BookDtailsActivity.this);
                    Wypread w = new Wypread();
                    w = dbUtils.listOneWypread((long) bookIdInt);
                    if (w != null) {
                        Wypread w1 = new Wypread();
                        w1.setId((long) bookIdInt);
                        dbUtils.deleteWypread(w1);
                    }
                }
                downLoad();
                break;
            case R.id.bookDetails_allJianjie:
                if (detailsAllJianjie.getText().equals("点击收起")) {
                    detailsJianjie.setVisibility(View.VISIBLE);
                    detailsJianjie2.setVisibility(View.GONE);
                    detailsAllJianjie.setText("点击查看更多详情");
                    detailsAllJianjie.setSelected(false);
                } else {
                    detailsAllJianjie.setSelected(true);
                    detailsJianjie.setVisibility(View.GONE);
                    detailsJianjie2.setVisibility(View.VISIBLE);
                    detailsAllJianjie.setText("点击收起");
                }
                break;
            case R.id.down_start:
                if (downStart.isSelected()) {
                    downStart.setSelected(false);
                    handler.cancel();
                } else {
                    downLoad();
                    downStart.setSelected(true);
                }
                break;
            case R.id.down_error:
                downStart.setSelected(false);
                if (handler != null)
                    handler.cancel();

                downSeek.setProgress(0);
                downJindu.setText("0%");
                //判断文件是否存在  如果存在则删除
                File file = new File("/mnt/sdcard/wypread/" + name);
                if (file.exists()) {
                    file.delete();
                }
                if (!file.exists()) {
                    Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
                    dbUtils = new CommonUtils(BookDtailsActivity.this);
                    Wypread wypread1 = new Wypread();
                    wypread1.setId((long) bookIdInt);
                    dbUtils.deleteWypread(wypread1);
                }
                isDown = false;
                downView.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 文件下载
     */
    private void downLoad() {
        HttpUtils http = new HttpUtils();
        handler = http.download(MyUrl.imgUrl + UtilBox.hanzi2utf2(filePath),
                "/mnt/sdcard/wypread/" + name,
                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {
                    @Override
                    public void onStart() {
                        Toast.makeText(BookDtailsActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        downSeek.setMax(100);
                        long i = current * 100 / total;
                        downSeek.setProgress((int) i);
                        downJindu.setText((int) i + "%");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        //插入数据库
                        dbUtils = new CommonUtils(BookDtailsActivity.this);
                        Wypread wypread = new Wypread();
                        wypread=dbUtils.listOneWypread((long)bookIdInt);
                        if(wypread == null){

                            Wypread wypread1 = new Wypread();
                            wypread1.setName(name);
                            if (UtilBox.isPDF(filePath)) {
                                wypread1.setBookPath("/mnt/sdcard/wypread/" + name);
                            } else {
                                wypread1.setBookPath("/mnt/sdcard/wypread/" + name);
                            }
                            wypread1.setId((long) bookIdInt);
                            wypread1.setImgPath(imgPath);
                            wypread1.setWriteName(write);
                            wypread1.setClassfiy(classfly);
//                            wypread1.setReadJindu("0%");
                            if(index == 0){
                                wypread1.setIsDown("0");
                            }else{
                                wypread1.setIsDown("1");
                            }
                            dbUtils.insertWypread(wypread1);
                        }else{
                            wypread.setName(name);
                            if (UtilBox.isPDF(filePath)) {
                                wypread.setBookPath("/mnt/sdcard/wypread/" + name);
                            } else {
                                wypread.setBookPath("/mnt/sdcard/wypread/" + name);
                            }
                            wypread.setId((long) bookIdInt);
                            wypread.setImgPath(imgPath);
                            wypread.setWriteName(write);
                            wypread.setClassfiy(classfly);
//                            wypread.setReadJindu("0%");
                            if(index == 0){
                                wypread.setIsDown("0");
                            }else{
                                wypread.setIsDown("1");
                            }
                            dbUtils.updateWypread(wypread);
                        }
                        Toast.makeText(BookDtailsActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                        downStart.setSelected(false);
                        downSeek.setProgress(0);
                        downJindu.setText("0%");
                        isDown = false;
                        downView.setVisibility(View.GONE);
                        initDownBookNum();
                    }
                    @Override
                    public void onFailure(HttpException error, String msg) {
                        downStart.setSelected(false);
                        Log.e("下载失败",msg);
                        Toast.makeText(BookDtailsActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /***
     * 统计下载量
     */
    private void initDownBookNum() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("bookId", bookId);
//        map.put("userId", SharedPreferenceUtil.getStringData("userId"));
//        map.put("StatisticsType", "2");

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("bookId", bookId)
                .add("userId", SharedPreferenceUtil.getStringData("userId"))
                .add("StatisticsType", "2")
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(MyUrl.bookDetails)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BookDtailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
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
                refreshNum();

            }
        });

//        OkHttpUtils.post().url(MyUrl.Statistics).params(map).tag(this).build().execute(new StringCallback() {
//            @Override
//            public void onError(Request request, Exception e) {
//                Toast.makeText(BookDtailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(String response) {
//                refreshNum();
//            }
//        });
    }

    /***
     * 统计阅读量
     */
    private void initReadBookNum() {
        HashMap<String, String> map = new HashMap<>();
        map.put("bookId", bookId);
        map.put("userId", SharedPreferenceUtil.getStringData("userId"));
        map.put("StatisticsType", "1");

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("bookId", bookId)
                .add("userId", SharedPreferenceUtil.getStringData("userId"))
                .add("StatisticsType", "1")
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(MyUrl.bookDetails)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BookDtailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
            }
        });

//        OkHttpUtils.post().url(MyUrl.Statistics).params(map).tag(this).build().execute(new StringCallback() {
//            @Override
//            public void onError(Request request, Exception e) {
//                Toast.makeText(BookDtailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(String response) {
//
//            }
//        });
    }
    /**刷新下载量*/
    private void refreshNum(){
//        HashMap<String, String> map = new HashMap<>();
//        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
//        map.put("bookID", getIntent().getStringExtra("bookID"));

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("userID", SharedPreferenceUtil.getStringData("userId"))
                .add("bookID", getIntent().getStringExtra("bookID"))
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(MyUrl.bookDetails)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BookDtailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
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
                final RootBean bean = new Gson().fromJson(res, RootBean.class);
                if (bean.getResultCode() == 0) {
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            detailsDownNum.setText("下载量: " + bean.getData().getBookDetails().getDownCount());
                        }
                    });

                }

            }
        });

//        OkHttpUtils.post().url(MyUrl.bookDetails).params(map).tag(this).build().execute(new StringCallback() {
//            @Override
//            public void onError(Request request, Exception e) {
//                UtilBox.dismissDialog();
//                Toast.makeText(BookDtailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(String response) {
//                RootBean bean = new Gson().fromJson(response, RootBean.class);
//                if (bean.getResultCode() == 0) {
//                    detailsDownNum.setText("下载量: " + bean.getData().getBookDetails().getDownCount());
//                }
//            }
//        });
    }



    long id;
    DownloadManager.Request request;
    DownloadManager downloadManager;
    HttpHandler handler;

    @Override
    public void backListener() {
        if (isDown == true)
            dialog1();
        else
            finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (isDown == true)
                dialog1();
            else
                finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void dialog1() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("退出将会取消下载,您确认退出吗?");
        builder.setTitle("退出");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 收藏 and  取消收藏
     */
    private void collection(final String type) {
        UtilBox.showDialog(this, "收藏中,请稍候");
//        HashMap<String, String> map = new HashMap<>();
//        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
//        map.put("bookID", bookId);
//        map.put("type", type);

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("userID", SharedPreferenceUtil.getStringData("userId"))
                .add("bookID", bookId)
                .add("type", type)
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(MyUrl.collection)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        UtilBox.dismissDialog();
                        Toast.makeText(BookDtailsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
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
                UtilBox.dismissDialog();
                final RootBean bean = new Gson().fromJson(res, RootBean.class);
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bean.getResultCode() == 0) {
                            if ("0".equals(type)) {
                                detailsCollation.setText("已收藏");
                                detailsCollation.setSelected(true);
                                detailsCollation.setTextColor(getResources().getColor(R.color.basecolor));
                            } else {
                                detailsCollation.setText("收藏");
                                detailsCollation.setSelected(false);
                                detailsCollation.setTextColor(getResources().getColor(R.color.black));
                            }
                        }
                    }
                });


            }
        });

//        OkHttpUtils.post().url(MyUrl.collection).params(map).tag(this).build().execute(new StringCallback() {
//            @Override
//            public void onError(Request request, Exception e) {
//                UtilBox.dismissDialog();
//                Toast.makeText(BookDtailsActivity.this, "网络连接失败" + e, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(String response) {
//                UtilBox.dismissDialog();
//                RootBean bean = new Gson().fromJson(response, RootBean.class);
//                if (bean.getResultCode() == 0) {
//                    if ("0".equals(type)) {
//                        detailsCollation.setText("已收藏");
//                        detailsCollation.setSelected(true);
//                        detailsCollation.setTextColor(getResources().getColor(R.color.basecolor));
//                    } else {
//                        detailsCollation.setText("收藏");
//                        detailsCollation.setSelected(false);
//                        detailsCollation.setTextColor(getResources().getColor(R.color.black));
//                    }
//                }
//            }
//        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    @Override
    public int setBaseView() {
        return R.layout.activity_bookdetails;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "图书详情";
    }

    @Override
    public boolean showMore() {
        return false;
    }
}
