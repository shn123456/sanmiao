package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.bean.BookBean;
import com.sanmiao.wypread.bean.SectionList;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.ui.BookDtailsActivity;
import com.sanmiao.wypread.ui.QuiteDetailsActivity;
import com.sanmiao.wypread.utils.GlideUtil;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.SunStartBaseAdapter;
import com.sanmiao.wypread.utils.UtilBox;
import com.sanmiao.wypread.utils.ViewHolder;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/4 0004.
 * 类说明{}
 */

public class QuiteDetailsListAdapter extends SunStartBaseAdapter {
    String quiteId = "";
    public QuiteDetailsListAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_details;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ImageView img = holder.getImageView(R.id.details_img);
        ImageView down =holder.getImageView(R.id.details_down);
        TextView title =holder.getTextView(R.id.details_title);
        TextView size =holder.getTextView(R.id.details_size);

        final ProgressBar seek = holder.get(R.id.quiteDown_seek);
        final ImageView   start =holder.getImageView(R.id.quiteDown_start);
        final TextView   jindu =holder.getTextView(R.id.quiteDown_jindu);
        ImageView error = holder.getImageView(R.id.quiteDown_error);
        final LinearLayout lv=holder.get(R.id.quiteDown_View);

        int i= SharedPreferenceUtil.getIntData("bgcolor");
        if(i==1){
            down.setImageResource(R.mipmap.icon_download2_difen);
        }else if(i==2){
            down.setImageResource(R.mipmap.icon_download2_bohong);
        }else if(i==3){
            down.setImageResource(R.mipmap.icon_download2_lan);
        }else if(i==4){
            down.setImageResource(R.mipmap.icon_download2_caolv);
        }else if(i==5){
            down.setImageResource(R.mipmap.icon_download2_yanzhi);
        }else{
            down.setImageResource(R.mipmap.icon_download2);
        }

        final SectionList bean = (SectionList) list.get(position);

        if(!TextUtils.isEmpty(bean.getBookImg()))
            GlideUtil.ShowCircleImg(context,MyUrl.imgUrl+bean.getBookImg(),img);

        title.setText("名称  " +bean.getName());
        size.setText("大小: " +bean.getSize());
        if(bean.isCheck())
            title.setTextColor(context.getResources().getColor(R.color.basecolor));
        else
            title.setTextColor(context.getResources().getColor(R.color.black));

        String danzhangId= bean.getId();
        //单章ID
        final int id= Integer.valueOf(danzhangId);


        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lv.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
                start.setSelected(true);
//                    判断文件进度 如果下载完成则删除重新下载 是否存在  如果存在则删除
//                if(isDown){
//                    File file = new File("/mnt/sdcard/wypread/"+bean.getBookName()+"/"+bean.getName());
//                    if (file.exists()) {
//                        file.delete();
//                    }
//                }
                int bookId =Integer.valueOf(bean.getBookId());
                dbUtils= new CommonUtils(context);
                Wypread w= new Wypread();
                //查询数据库是否有这本听书文件了 有直接下载,没有插入在下载
                w=dbUtils.listOneWypread((long)bookId);
                if(w == null){
                    //插入整本书到数据
                    Wypread wypread= new Wypread();
                    wypread.setId((long)bookId);
                    wypread.setName(bean.getBookName());
                    wypread.setQuitePath("/mnt/sdcard/wypread/"+bean.getBookName());
                    wypread.setWriteName(bean.getWriteName());
                    wypread.setClassfiy(bean.getClassfly());
                    wypread.setImgPath(bean.getBookImg());
                    wypread.setIsDown("0");
                    dbUtils.insertWypread(wypread);
                }else{
                    //插入整本书到数据
                    Wypread wypread= new Wypread();
                    wypread.setId((long)bookId);
                    wypread.setName(bean.getBookName());
                    wypread.setQuitePath("/mnt/sdcard/wypread/"+bean.getBookName());
                    wypread.setWriteName(bean.getWriteName());
                    wypread.setClassfiy(bean.getClassfly());
                    wypread.setImgPath(bean.getBookImg());
                    wypread.setIsDown("0");
                    dbUtils.updateWypread(wypread);
                }

                HttpUtils http = new HttpUtils();
                String tmpstr= UtilBox.hanzi2utf(bean.getFileUrl());
                handler = http.download(MyUrl.imgUrl+tmpstr,
                        "/mnt/sdcard/wypread/"+bean.getBookName()+"/"+bean.getName(),
                        true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                        true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                        new RequestCallBack<File>() {
                            @Override
                            public void onStart() {
                                Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onLoading(long total, long current, boolean isUploading) {
                                seek.setMax(100);
                                long i =current*100 / total;
                                seek.setProgress((int)i);
                                jindu.setText((int)i+"%");
                                Log.e("loading", "total---" + total + "/////  current---" + current + "/////  isUploading---" + isUploading+ "i---"+i);
                            }
                            @Override
                            public void onSuccess(ResponseInfo<File> responseInfo) {
                                dbUtils = new CommonUtils(context);
                                Wypread w =new Wypread();
                                w=dbUtils.listOneWypread((long)id);
                                if(w == null){
                                    //插入数据库
                                    Wypread wypread = new Wypread();
                                    wypread.setId((long)id);
                                    wypread.setName(bean.getName());
                                    wypread.setQuitePath("/mnt/sdcard/wypread/" + bean.getBookName()+"/"+bean.getName());
                                    wypread.setFromFile("/mnt/sdcard/wypread/"+bean.getBookName());
                                    wypread.setImgPath(bean.getBookImg());
                                    wypread.setWriteName(bean.getWriteName());
                                    wypread.setClassfiy(bean.getClassfly());
                                    dbUtils.insertWypread(wypread);
                                }else{
                                    //插入数据库
                                    Wypread wypread = new Wypread();
                                    wypread.setId((long)id);
                                    wypread.setName(bean.getName());
                                    wypread.setQuitePath("/mnt/sdcard/wypread/" + bean.getBookName()+"/"+bean.getName());
                                    wypread.setFromFile("/mnt/sdcard/wypread/"+bean.getBookName());
                                    wypread.setImgPath(bean.getBookImg());
                                    wypread.setWriteName(bean.getWriteName());
                                    wypread.setClassfiy(bean.getClassfly());
                                    dbUtils.updateWypread(wypread);
                                }

                                Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
                                bean.setCheck(false);
                                start.setSelected(false);
                                seek.setProgress(0);
                                jindu.setText("0%");
                                isDown=true;
                                lv.setVisibility(View.GONE);
                                initDownBookNum();
                            }
                            @Override
                            public void onFailure(HttpException error, String msg) {
                                start.setSelected(false);
                                Log.e("downError",msg);
                                lv.setVisibility(View.GONE);
                                if(msg.equals("maybe the file has downloaded completely")){
                                    Toast.makeText(context, "已下载", Toast.LENGTH_SHORT).show();
                                    dbUtils = new CommonUtils(context);
                                    Wypread w =new Wypread();
                                    w=dbUtils.listOneWypread((long)id);
                                    if(w == null){
                                        //插入数据库
                                        Wypread wypread = new Wypread();
                                        wypread.setId((long)id);
                                        wypread.setName(bean.getName());
                                        wypread.setQuitePath("/mnt/sdcard/wypread/" + bean.getBookName()+"/"+bean.getName());
                                        wypread.setFromFile("/mnt/sdcard/wypread/"+bean.getBookName());
                                        wypread.setImgPath(bean.getBookImg());
                                        wypread.setWriteName(bean.getWriteName());
                                        wypread.setClassfiy(bean.getClassfly());
                                        dbUtils.insertWypread(wypread);
                                    }else{
                                        //插入数据库
                                        Wypread wypread = new Wypread();
                                        wypread.setId((long)id);
                                        wypread.setName(bean.getName());
                                        wypread.setQuitePath("/mnt/sdcard/wypread/" + bean.getBookName()+"/"+bean.getName());
                                        wypread.setFromFile("/mnt/sdcard/wypread/"+bean.getBookName());
                                        wypread.setImgPath(bean.getBookImg());
                                        wypread.setWriteName(bean.getWriteName());
                                        wypread.setClassfiy(bean.getClassfly());
                                        dbUtils.updateWypread(wypread);
                                    }
                                }else{
                                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });

        //开始下载
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start.setSelected(!start.isSelected());
                if (start.isSelected()) {
                    start.setSelected(true);
                    int bookId =Integer.valueOf(bean.getBookId());
                    HttpUtils http = new HttpUtils();
                    handler = http.download(MyUrl.imgUrl+UtilBox.hanzi2utf(bean.getFileUrl()),
                            "/mnt/sdcard/wypread/"+bean.getBookName()+"/"+bean.getName(),
                            true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                            true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                            new RequestCallBack<File>() {
                                @Override
                                public void onStart() {
                                    Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onLoading(long total, long current, boolean isUploading) {
                                    seek.setMax(100);
                                    long i =current*100 / total;
                                    seek.setProgress((int)i);
                                    jindu.setText((int)i+"%");
                                }
                                @Override
                                public void onSuccess(ResponseInfo<File> responseInfo) {
                                    Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
                                    bean.setCheck(false);
                                    start.setSelected(false);
                                    seek.setProgress(0);
                                    jindu.setText("0%");
                                    isDown=true;
                                    lv.setVisibility(View.GONE);
                                    initDownBookNum();
                                }
                                @Override
                                public void onFailure(HttpException error, String msg) {
                                    start.setSelected(false);
                                    Log.e("downError",msg);
                                    lv.setVisibility(View.GONE);
                                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    bean.setCheck(false);
                    start.setSelected(false);
                    handler.cancel();
                }
            }
        });

        //取消下载
        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start.setSelected(false);
                if(handler!=null)
                    handler.cancel();

                bean.setCheck(false);
                seek.setProgress(0);
                jindu.setText("0%");
                //判断文件是否存在  如果存在则删除
                File file = new File("/mnt/sdcard/wypread/"+bean.getBookName()+"/"+bean.getName());
                if (file.exists()) {
                    file.delete();
                }
                if(!file.exists()){
                    Toast.makeText(context, "已删除", Toast.LENGTH_SHORT).show();
                    lv.setVisibility(View.GONE);
                }
            }
        });

    }

    /***统计下载量*/
    private void initDownBookNum() {
        HashMap<String,String> map = new HashMap<>();
        map.put("bookId",quiteId);
        map.put("userId",SharedPreferenceUtil.getStringData("userId"));
        map.put("StatisticsType","2");
        OkHttpUtils.post().url(MyUrl.Statistics).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response) {
                Intent in= new Intent();
                in.setAction("setNum");
                context.sendBroadcast(in);
            }
        });
    }
    boolean isDown =false;//判断是否下载完成
    CommonUtils dbUtils;//数据库工具
    HttpHandler handler;

    public String getQuiteId() {
        return quiteId;
    }

    public void setQuiteId(String quiteId) {
        this.quiteId = quiteId;
    }
}
