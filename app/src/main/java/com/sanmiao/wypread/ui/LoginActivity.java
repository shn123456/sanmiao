package com.sanmiao.wypread.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.DataBean;
import com.sanmiao.wypread.bean.DataBeanEx;
import com.sanmiao.wypread.bean.LoginBean;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.bean.RootBeanEx;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.UtilBox;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

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
 * 时间 2017/5/2 0002.
 * 类说明{登录}
 */

public class LoginActivity extends BaseActivity {
    @InjectView(R.id.login_tel)
    EditText loginTel;//手机号
    @InjectView(R.id.login_pass)
    EditText loginPass;//密码
    @InjectView(R.id.login_ip_port)
    EditText loginIpAndPort;//密码
    @InjectView(R.id.login_ok)
    TextView loginOk;//登录
    @InjectView(R.id.login_forgetPass)
    TextView loginForgetPass;//忘记密码
    private Handler handler= new Handler(Looper.getMainLooper());//初始化handler

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        setMoreText("");
        showBack(false);
    }

    @OnClick({R.id.login_ok,R.id.login_forgetPass})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.login_ok:
//                if(TextUtils.isEmpty(loginTel.getText().toString())  || !UtilBox.isMobileNO(loginTel.getText().toString()))
//                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
//                else if (TextUtils.isEmpty(loginPass.getText().toString()))
//                    Toast.makeText(this, "请输入正确的密码", Toast.LENGTH_SHORT).show();
//                else
                    login();

                break;
            case R.id.login_forgetPass:
                startActivity(new Intent(this,FindPasswordActivity.class));
                break;
        }
    }

    /**登录*/
    private void login(){
//        HashMap<String,String> map = new HashMap<>();
//        map.put("username",loginTel.getText().toString());
//        map.put("password",loginPass.getText().toString());
//        map.put("str",loginPass.getText().toString());
        OkHttpClient client = new OkHttpClient();
        String pattern = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\:([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])$";
        final String content = loginIpAndPort.getText().toString();
        boolean isMatch = Pattern.matches(pattern, content);
        if (!isMatch) {
            Toast.makeText(this, "IP端口格式不正确", Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferenceUtil.SaveData("IpAndPort", content);
        MyUrl.SetUrl(content);
        UtilBox.showDialog(this,"登录中,请稍候");
        FormBody body = new FormBody.Builder()
                .add("username",loginTel.getText().toString())
                .add("password",loginPass.getText().toString())
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(MyUrl.login)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
                UtilBox.dismissDialog();
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
                final LoginBean loginBean = new Gson().fromJson(res,LoginBean.class);
                if(loginBean.getcode() == 1){
                    final RootBeanEx bean = new Gson().fromJson(res,RootBeanEx.class);
                    SharedPreferenceUtil.SaveData("userId",bean.getDate().getusercode());
                    SharedPreferenceUtil.SaveData("userName",bean.getDate().getUserName());
                    SharedPreferenceUtil.SaveData("userLogin",true);
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, loginBean.getemsg(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

//        PostFormBuilder post = OkHttpUtils.getInstance().post()
////                .addHeader("Host", "192.168.1.162")
////                .addHeader("Content-Type", "application/x-www-form-urlencoded")
////                .addHeader("Content-Length", "length")
//                .url(MyUrl.login).params(map)
//                .tag(this);
//        post.build().execute(new StringCallback() {
//            @Override
//            public void onError(Request request, Exception e) {
//                Toast.makeText(LoginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
//                UtilBox.dismissDialog();
//            }
//            @Override
//            public void onResponse(String response) {
//                UtilBox.dismissDialog();
//                RootBean bean = new Gson().fromJson(response,RootBean.class);
//                if(bean.getResultCode() == 0){
//                    SharedPreferenceUtil.SaveData("userId",bean.getData().getUserInfo().getUserID());
//                    SharedPreferenceUtil.SaveData("userName",bean.getData().getUserInfo().getUnitsName());
//                    SharedPreferenceUtil.SaveData("userLogin",true);
//                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
//                    finish();
//                }else{
//                    Toast.makeText(LoginActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);}


    @Override
    public void moretextListener() {
        startActivity(new Intent(this,RegisterActivity.class));
    }

    @Override
    public int setBaseView() {
        return R.layout.activity_login;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "登录";
    }

    @Override
    public boolean showMore() {
        return true;
    }

}
