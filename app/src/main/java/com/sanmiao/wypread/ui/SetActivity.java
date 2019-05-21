package com.sanmiao.wypread.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.UpDataAPK;
import com.sanmiao.wypread.utils.UtilBox;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/3 0003.
 * 类说明{设置}
 */

public class SetActivity extends BaseActivity {
    @InjectView(R.id.set_skin)
    TextView setSkin;//换肤
    @InjectView(R.id.set_pass)
    TextView setPass;//修改密码
    @InjectView(R.id.set_xieyi)
    TextView setXieyi;//协议
    @InjectView(R.id.set_about)
    TextView setAbout;//关于我们
    @InjectView(R.id.set_updata)
    TextView setUpdata;//版本更新
    @InjectView(R.id.set_no)
    TextView setNo;//退出登录

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        //根据主体颜色动态更改shape文件颜色
        GradientDrawable background = (GradientDrawable) setNo.getBackground();
        background.setColor(getResources().getColor(R.color.basecolor));
    }

    @OnClick({R.id.set_skin,R.id.set_pass,R.id.set_xieyi,R.id.set_about,R.id.set_updata,R.id.set_no})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.set_skin:
                startActivity(new Intent(this,ThemeActivity.class));
                break;
            case R.id.set_pass:
                startActivity(new Intent(this,ModifyPassActivity.class));
                break;
            case R.id.set_xieyi:
                startActivity(new Intent(this,XieYiActivity.class).putExtra("title","1"));
                break;
            case R.id.set_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
            case R.id.set_updata:
                checkVersion();
                break;
            case R.id.set_no:
                SharedPreferenceUtil.SaveData("userLogin",false);
                finish();
                Intent in = new Intent();
                in.setAction("loginClose");
                sendBroadcast(in);
                Intent intent=new Intent(SetActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    //检测新版本
    private void checkVersion(){
        UtilBox.showDialog(this,"正在检测,请稍候");
        HashMap<String,String> map = new HashMap<>();
        map.put("version",getVersion());
        OkHttpUtils.get().url(MyUrl.checkVersion).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                UtilBox.dismissDialog();
                Toast.makeText(SetActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean  = new Gson().fromJson(response,RootBean.class);
                if(!bean.getData().isUpdate()){
                    Toast.makeText(SetActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
                }else{
                    upapk = new UpDataAPK(SetActivity.this, MyUrl.imgUrl+bean.getData().getUrl(), SetActivity.this);
                    upapk.checkUpdataInfo();
                }
            }
        });


    }
    UpDataAPK upapk;
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }
    }


    @Override
    public int setBaseView() {
        return R.layout.activity_set;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "设置";
    }

    @Override
    public boolean showMore() {
        return false;
    }

}
