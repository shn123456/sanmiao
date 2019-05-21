package com.sanmiao.wypread.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.client.android.Intents;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.adapter.MainBottomAdapter;
import com.sanmiao.wypread.bean.BookDownLoadBean;
import com.sanmiao.wypread.bean.LoginBean;
import com.sanmiao.wypread.bean.MainBottomBean;
import com.sanmiao.wypread.bean.NoSuchBookBean;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.bean.RootBeanEx;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.fragment.BookFragment;
import com.sanmiao.wypread.fragment.MainFragment;
import com.sanmiao.wypread.fragment.MineFragment;
import com.sanmiao.wypread.fragment.NoneFragment;
import com.sanmiao.wypread.fragment.QuiteFragment;
import com.sanmiao.wypread.fragment.VideoFragment;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.main_layout)
    FrameLayout mainLayout;
    @InjectView(R.id.main_bottom)
    RecyclerView mainBottom;
    @InjectView(R.id.activity_main)
    LinearLayout activityMain;

    MainFragment fragment1;
    BookFragment fragment2;
    NoneFragment fragment3;
    NoneFragment fragment4;
    MineFragment fragment5;
    FragmentManager fragmentManager;
    private List<Fragment> viewList=new ArrayList<>();
    private List<MainBottomBean> bottomList =new ArrayList<>();
    private MainBottomAdapter adapter;
    int index=0;
    private Handler handler= new Handler(Looper.getMainLooper());//初始化handler

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initView();
        initBottom();
        testCall();
        showBack(false);

        //广播
        mBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("loginClose");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }
    //底部
    private void initBottom() {
        MainBottomBean bean1=new MainBottomBean(R.mipmap.tab_home,R.mipmap.tab_home_pre,"首页");
        bean1.setCheck(true);
        MainBottomBean bean2=new MainBottomBean(R.mipmap.tab_read,R.mipmap.tab_read_pre,"阅读");
        MainBottomBean bean3=new MainBottomBean(R.mipmap.tab_voicebook,R.mipmap.tab_voicebook_pre,"听书");
        MainBottomBean bean4=new MainBottomBean(R.mipmap.tab_video,R.mipmap.tab_video_pre,"视频");
        MainBottomBean bean5=new MainBottomBean(R.mipmap.tab_mine,R.mipmap.tab_mine_pre,"我的");
        bottomList.add(bean1);
        bottomList.add(bean2);
        bottomList.add(bean3);
        bottomList.add(bean4);
        bottomList.add(bean5);
        adapter= new MainBottomAdapter(this,bottomList,false);
        GridLayoutManager manager= new GridLayoutManager(this,5);
        mainBottom.setLayoutManager(manager);
        mainBottom.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction ft=fragmentManager.beginTransaction();//获取管理器
                if(index != position){
                    for(int i =0 ; i<bottomList.size();i++ ){
                        bottomList.get(i).setCheck(false);
                        ft.hide(viewList.get(i));
                    }
                }
                bottomList.get(position).setCheck(true);
                if (viewList.get(position).isAdded()) {//如果碎片加入
                    if(position==2){
                        ft.show(viewList.get(position)).commit();//显示
                    }else
                        ft.show(viewList.get(position)).commit();//显示
                } else {
                    ft.add(R.id.main_layout, viewList.get(position)).show(viewList.get(position)).commit();//加入碎片并显示
                }
                adapter.notifyDataSetChanged();
                index=position;
            }
        });
    }


    //布局
    private void initView() {
        fragment1= new MainFragment();
        fragment2= new BookFragment();
        fragment3= new NoneFragment();
        fragment4= new NoneFragment();
        fragment5= new MineFragment();
        viewList.add(fragment1);
        viewList.add(fragment2);
        viewList.add(fragment3);
        viewList.add(fragment4);
        viewList.add(fragment5);
        fragmentManager=getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.main_layout,viewList.get(0)).show(viewList.get(0)).commit();
        ft.add(R.id.main_layout,viewList.get(1)).hide(viewList.get(1));
        ft.add(R.id.main_layout,viewList.get(2)).hide(viewList.get(2));
        ft.add(R.id.main_layout,viewList.get(3)).hide(viewList.get(3));
        ft.add(R.id.main_layout,viewList.get(4)).hide(viewList.get(4));
    }

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    public void testCall() {
        //添加权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    int  bookIdInt = 0;
    //返回扫描结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String type = "";
        String bookId = "";
        String bookName = "";
        String bookUrl = "";
        if(data!=null){
            final String str=data.getStringExtra(Intents.Scan.RESULT);
            try{
                String[] strings = str.split("&");
                bookUrl = strings[0];
                int endIndex = bookUrl.indexOf("/", 8);//截取主机地址  下载使用
                bookUrl = bookUrl.substring(0,endIndex);
                String[] body = strings[0].split("/");
                bookName = body[body.length-1];
                for (String string : strings) {
                    if (string.contains("bookId")) {
                        bookId = string.substring(7,string.length());
                    }
                    if (string.contains("type")) {
                        type = string.substring(5,string.length());
                    }
                }
//                final DownBean bean = new Gson().fromJson(str,DownBean.class);
                HashMap<String, String> map = new HashMap<>();
                map.put("userID", SharedPreferenceUtil.getStringData("userId"));
                map.put("bookID", bookId);
                final String finalBookName = bookName;
                final String finalType = type;
                final String finalBookId = bookId;
                final String finalType1 = type;
                final String finalBookId1 = bookId;
                final String finalBookUrl = bookUrl;
                final String url = finalBookUrl+MyUrl.DOWN_URL+"?"+"bookId=";
//                OkHttpUtils.get().url()


                OkHttpClient client = new OkHttpClient();
                FormBody formBody = new FormBody.Builder()
                        .add("userID", SharedPreferenceUtil.getStringData("userId"))
                        .add("bookID", bookId)
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(MyUrl.bookDetails)
                        .post(formBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                UtilBox.dismissDialog();
                                Toast.makeText(MainActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
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
                        RootBean bean = new Gson().fromJson(res, RootBean.class);
                        Log.e("awfdasfasd", "onResponse: "+response);
                        if (bean.getResultCode() == 1) {
                            NoSuchBookBean noSuchBookBean = new Gson().fromJson(res, NoSuchBookBean.class);
                            final String endUrl = noSuchBookBean.getData().getBookDetails().getUrl();//服务器返回的加密后的bookId  需要拼接到扫描二维码扫描出来的主机地址中
                            if(!finalType.equals("1")){
                                Toast.makeText(MainActivity.this, "无法下载此文件", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            try {
                                bookIdInt = Integer.valueOf(finalBookId);
                            }catch (Exception e){}

                            CommonUtils dbUtils = new CommonUtils(MainActivity.this);
                            Wypread wypread = new Wypread();
                            wypread=dbUtils.listOneWypread((long)bookIdInt);
                            if(wypread != null){
                                Toast.makeText(MainActivity.this, "此书已下载", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            OkHttpUtils.get().url(url+endUrl).tag(this).build().execute(new StringCallback() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response) {
                                    Log.e("safaafds", "onResponse: "+response);
                                    Log.e("safaafds", "onResponse: "+url+endUrl);
                                    final BookDownLoadBean bookDownLoadBean = new Gson().fromJson(response, BookDownLoadBean.class);
                                    dialog.setTitle("是否下载此图书?")
                                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    HttpUtils http = new HttpUtils();
                                                    HttpHandler handler = http.download(bookDownLoadBean.getData().getUrl(),
                                                            "/mnt/sdcard/wypread/" + bookDownLoadBean.getData().getBookname() + ".pdf",
                                                            true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                                                            false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                                                            new RequestCallBack<File>() {
                                                                @Override
                                                                public void onStart() {
                                                                    Toast.makeText(MainActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
                                                                }
                                                                @Override
                                                                public void onLoading(long total, long current, boolean isUploading) {}
                                                                @Override
                                                                public void onSuccess(ResponseInfo<File> responseInfo) {

                                                                    //插入数据库
                                                                    CommonUtils dbUtils = new CommonUtils(MainActivity.this);
                                                                    Wypread wypread = new Wypread();

                                                                    wypread=dbUtils.listOneWypread((long)bookIdInt);
                                                                    if(wypread == null) {
                                                                        Wypread wypread1 = new Wypread();

                                                                        if (UtilBox.isPDF(bookDownLoadBean.getData().getUrl())) {
                                                                            wypread1.setName(bookDownLoadBean.getData().getBookname()+ ".pdf");
                                                                            wypread1.setBookPath("/mnt/sdcard/wypread/" + bookDownLoadBean.getData().getBookname() + ".pdf");
                                                                        } else {
                                                                            wypread1.setName(bookDownLoadBean.getData().getBookname());
                                                                            wypread1.setBookPath("/mnt/sdcard/wypread/" + bookDownLoadBean.getData().getBookname());
                                                                        }
                                                                        wypread1.setId((long) bookIdInt);
                                                                        wypread1.setImgPath("/mnt/sdcard/wypread/" + bookDownLoadBean.getData().getBookname());
//                                                        wypread1.setWriteName(bean.getWriteName());
//                                                        wypread1.setClassfiy(bean.getClassName());
                                                                        wypread1.setIsDown("1");
                                                                        dbUtils.insertWypread(wypread1);

                                                                        Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                                @Override
                                                                public void onFailure(HttpException error, String msg) {
                                                                    Log.e("下载失败",msg);
                                                                    Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            })
                                            .create().show();
                                }
                            });

                        }else{
                            if("1".equals(finalType1)){
                                startActivity(new Intent(MainActivity.this,BookDtailsActivity.class).putExtra("bookID", finalBookId1));
                            }else if("2".equals(finalType1)){
                                startActivity(new Intent(MainActivity.this,QuiteDetailsActivity.class).putExtra("quiteID", finalBookId1));
                            }else if("3".equals(finalType1)){
                                startActivity(new Intent(MainActivity.this,VideoDetailsActivity.class).putExtra("videoID", finalBookId1));
                            }
                        }
                    }
                });


                //请求服务器判断是否存在  如存在跳转详情页面  不存在访问第三方服务器接口得到下载地址并下载图书
//                OkHttpUtils.post().url(MyUrl.bookDetails).params(map).tag(this).build().execute(new StringCallback() {
//                    @Override
//                    public void onError(Request request, Exception e) {
//                        UtilBox.dismissDialog();
//                        Toast.makeText(MainActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onResponse(final String response) {
//                        UtilBox.dismissDialog();
//                        RootBean bean = new Gson().fromJson(response, RootBean.class);
//                        Log.e("awfdasfasd", "onResponse: "+response);
//                        if (bean.getResultCode() == 1) {
//                            NoSuchBookBean noSuchBookBean = new Gson().fromJson(response, NoSuchBookBean.class);
//                            final String endUrl = noSuchBookBean.getData().getBookDetails().getUrl();//服务器返回的加密后的bookId  需要拼接到扫描二维码扫描出来的主机地址中
//                            if(!finalType.equals("1")){
//                                Toast.makeText(MainActivity.this, "无法下载此文件", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            try {
//                                bookIdInt = Integer.valueOf(finalBookId);
//                            }catch (Exception e){}
//
//                            CommonUtils dbUtils = new CommonUtils(MainActivity.this);
//                            Wypread wypread = new Wypread();
//                            wypread=dbUtils.listOneWypread((long)bookIdInt);
//                            if(wypread != null){
//                                Toast.makeText(MainActivity.this, "此书已下载", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
//                            OkHttpUtils.get().url(url+endUrl).tag(this).build().execute(new StringCallback() {
//                                @Override
//                                public void onError(Request request, Exception e) {
//                                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void onResponse(String response) {
//                                    Log.e("safaafds", "onResponse: "+response);
//                                    Log.e("safaafds", "onResponse: "+url+endUrl);
//                                    final BookDownLoadBean bookDownLoadBean = new Gson().fromJson(response, BookDownLoadBean.class);
//                                    dialog.setTitle("是否下载此图书?")
//                                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                    dialogInterface.dismiss();
//                                                    HttpUtils http = new HttpUtils();
//                                                    HttpHandler handler = http.download(bookDownLoadBean.getData().getUrl(),
//                                                            "/mnt/sdcard/wypread/" + bookDownLoadBean.getData().getBookname() + ".pdf",
//                                                            true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
//                                                            false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
//                                                            new RequestCallBack<File>() {
//                                                                @Override
//                                                                public void onStart() {
//                                                                    Toast.makeText(MainActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
//                                                                }
//                                                                @Override
//                                                                public void onLoading(long total, long current, boolean isUploading) {}
//                                                                @Override
//                                                                public void onSuccess(ResponseInfo<File> responseInfo) {
//
//                                                                    //插入数据库
//                                                                    CommonUtils dbUtils = new CommonUtils(MainActivity.this);
//                                                                    Wypread wypread = new Wypread();
//
//                                                                    wypread=dbUtils.listOneWypread((long)bookIdInt);
//                                                                    if(wypread == null) {
//                                                                        Wypread wypread1 = new Wypread();
//
//                                                                        if (UtilBox.isPDF(bookDownLoadBean.getData().getUrl())) {
//                                                                            wypread1.setName(bookDownLoadBean.getData().getBookname()+ ".pdf");
//                                                                            wypread1.setBookPath("/mnt/sdcard/wypread/" + bookDownLoadBean.getData().getBookname() + ".pdf");
//                                                                        } else {
//                                                                            wypread1.setName(bookDownLoadBean.getData().getBookname());
//                                                                            wypread1.setBookPath("/mnt/sdcard/wypread/" + bookDownLoadBean.getData().getBookname());
//                                                                        }
//                                                                        wypread1.setId((long) bookIdInt);
//                                                        wypread1.setImgPath("/mnt/sdcard/wypread/" + bookDownLoadBean.getData().getBookname());
////                                                        wypread1.setWriteName(bean.getWriteName());
////                                                        wypread1.setClassfiy(bean.getClassName());
//                                                                        wypread1.setIsDown("1");
//                                                                        dbUtils.insertWypread(wypread1);
//
//                                                                        Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
//                                                                    }
//                                                                }
//                                                                @Override
//                                                                public void onFailure(HttpException error, String msg) {
//                                                                    Log.e("下载失败",msg);
//                                                                    Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
//                                                                }
//                                                            });
//                                                }
//                                            })
//                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                    dialogInterface.dismiss();
//                                                }
//                                            })
//                                            .create().show();
//                                }
//                            });
//
//                        }else{
//                            if("1".equals(finalType1)){
//                                startActivity(new Intent(MainActivity.this,BookDtailsActivity.class).putExtra("bookID", finalBookId1));
//                            }else if("2".equals(finalType1)){
//                                startActivity(new Intent(MainActivity.this,QuiteDetailsActivity.class).putExtra("quiteID", finalBookId1));
//                            }else if("3".equals(finalType1)){
//                                startActivity(new Intent(MainActivity.this,VideoDetailsActivity.class).putExtra("videoID", finalBookId1));
//                            }
//                        }
//                    }
//                });
//                if(isDown){//下载
//
//                }else{
//                    Log.e("awfafad", "onActivityResult: ???");
//                    if("1".equals(type)){
//                        startActivity(new Intent(this,BookDtailsActivity.class).putExtra("bookID",bookId));
//                    }else if("2".equals(type)){
//                        startActivity(new Intent(this,QuiteDetailsActivity.class).putExtra("quiteID",bookId));
//                    }else if("3".equals(type)){
//                        startActivity(new Intent(this,VideoDetailsActivity.class).putExtra("videoID",bookId));
//                    }
//                }
            }catch (Exception e){
                Log.e("asfafaf", "onActivityResult: "+e.toString());
            }


          /*  String[] str =data.getStringExtra(Intents.Scan.RESULT).split("&");
            String bookID=str[0].substring(7,str[0].length());
            if(str[1].equals("type=1")){
                startActivity(new Intent(this,BookDtailsActivity.class).putExtra("bookID",bookID));
            }else if(str[1].equals("type=2")){
                startActivity(new Intent(this,QuiteDetailsActivity.class).putExtra("quiteID",bookID));
            }else if(str[1].equals("type=3")){
                startActivity(new Intent(this,VideoDetailsActivity.class).putExtra("videoID",bookID));
            }*/
        }
//            Toast.makeText(this, data.getStringExtra(Intents.Scan.RESULT), Toast.LENGTH_SHORT).show();
    }
    public String formatUrl(String url){
        if (url.contains(" ")){
            if(url.substring(url.length()-1)==" "){
                url= url.substring(0,url.length()-1);
            }else{
                url= url.replace(" ","%20");
            }
        }
        if (url.contains("\"")){
            url= url.replace("\"","%22");
        }
        if (url.contains("{")){
            url= url.replace("{","%7B");
        }
        if (url.contains("}")){
            url= url.replace("{","%7D");
        }
        return url;
    }

    @Override
    public int setBaseView() {
        return R.layout.activity_main;
    }

    @Override
    public boolean showTitle() {
        return false;
    }

    @Override
    public String setTitleText() {
       return null;
    }

    @Override
    public boolean showMore() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver;
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
}
