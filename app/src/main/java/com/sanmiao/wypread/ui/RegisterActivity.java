package com.sanmiao.wypread.ui;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.StringFormatUtil;
import com.sanmiao.wypread.utils.TimeCount;
import com.sanmiao.wypread.utils.UtilBox;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/2 0002.
 * 类说明{注册}
 */

public class RegisterActivity extends BaseActivity {
    @InjectView(R.id.register_tel)
    EditText registerTel;//手机号
    @InjectView(R.id.register_code)
    EditText registerCode;//验证码
    @InjectView(R.id.register_getcode)
    TextView registerGetcode;//获取验证码
    @InjectView(R.id.register_pass)
    EditText registerPass;//密码
    @InjectView(R.id.register_pass2)
    EditText registerPass2;//确认密码
    @InjectView(R.id.register_xieyi)
    TextView registerXieyi;//协议
    @InjectView(R.id.register_ok)
    TextView registerOk;//注册
    @InjectView(R.id.register_shouquan)
    EditText registerShouquan;//授权码
    @InjectView(R.id.register_check)
    CheckBox registerCheck;//是否阅读同意协议

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        showMore1();

//        String str = "已阅读并同意" + "<<中版行知用户注册协议>>";
//        StringFormatUtil util = new StringFormatUtil(this, str, "<<中版行知用户注册协议>>", R.color.basecolor);
//        util.fillColor();
//        registerXieyi.setText(util.getResult());

        registerCheck.setPadding(20,10,10,10);
        int i= SharedPreferenceUtil.getIntData("bgcolor");
        if(i==1){
            registerCheck.setButtonDrawable(R.drawable.check1);
        }else if(i==2){
            registerCheck.setButtonDrawable(R.drawable.check2);
        }else if(i==3){
            registerCheck.setButtonDrawable(R.drawable.check3);
        }else if(i==4){
            registerCheck.setButtonDrawable(R.drawable.check4);
        }else if(i==5){
            registerCheck.setButtonDrawable(R.drawable.check5);
        }else{
            registerCheck.setButtonDrawable(R.drawable.check);
        }

        GradientDrawable drawable=(GradientDrawable) registerOk.getBackground();
        drawable.setColor(getResources().getColor(R.color.basecolor));

    }

    @OnClick({R.id.register_getcode, R.id.register_ok,R.id.register_xieyi})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.register_xieyi:
                startActivity(new Intent(this,XieYiActivity.class).putExtra("title","0"));
                break;
            case R.id.register_getcode:
                if (TextUtils.isEmpty(registerTel.getText().toString()) == true) {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (UtilBox.isMobileNO(registerTel.getText().toString()) == false) {
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取验证码
                Getcode();
                break;
            case R.id.register_ok:
                if (TextUtils.isEmpty(registerTel.getText().toString()) == true) {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (UtilBox.isMobileNO(registerTel.getText().toString()) == false) {
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(registerCode.getText().toString()) == true) {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (registerCode.getText().toString().length() < 6) {
                    Toast.makeText(this, "请输入六位的验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(registerShouquan.getText().toString())){
                    Toast.makeText(this, "请输入授权码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(registerPass.getText().toString()) == true) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (registerPass.getText().length() < 6 || registerPass.getText().toString().length() > 20) {
                    Toast.makeText(this, "密码格式输入不正确,请输入6—20位密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(registerPass2.getText().toString()) == true) {
                    Toast.makeText(this, "请输入确认密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (registerPass2.getText().toString().length() < 6 || registerPass2.getText().toString().length() > 20) {
                    Toast.makeText(this, "确认密码格式输入不正确,请输入6—20位密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!registerPass2.getText().toString().equals(registerPass.getText().toString())) {
                    Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(registerCheck.isChecked() ==false){
                    Toast.makeText(this, "请勾选协议", Toast.LENGTH_SHORT).show();
                    return;
                }
                Registerok();
                break;
            default:
                break;
        }

    }
    /***注册*/
    private void Registerok() {
        UtilBox.showDialog(this,"正在注册,请稍候");
        HashMap<String,String> map = new HashMap<>();
        map.put("phone",registerTel.getText().toString());
        map.put("code",registerCode.getText().toString());
        map.put("authorizeCode",registerShouquan.getText().toString());
        map.put("passWord",registerPass.getText().toString());
        OkHttpUtils.post().params(map).url(MyUrl.register).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(RegisterActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                UtilBox.dismissDialog();
            }
            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean =new Gson().fromJson(response,RootBean.class);
                if(bean.getResultCode() == 0){
                    SharedPreferenceUtil.SaveData("userId",bean.getData().getUserInfo().getUserID());
                    SharedPreferenceUtil.SaveData("userName",bean.getData().getUserInfo().getUnitsName());
                    SharedPreferenceUtil.SaveData("userLogin",true);
                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(RegisterActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**获取验证码*/
    private void Getcode() {
        UtilBox.showDialog(this,"获取验证码,请稍候");
        HashMap<String,String> map = new HashMap<>();
        map.put("phone",registerTel.getText().toString());
        map.put("type","0");
        OkHttpUtils.post().url(MyUrl.getCode).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(RegisterActivity.this, "获取网络连接失败", Toast.LENGTH_SHORT).show();
                UtilBox.dismissDialog();
            }
            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean = new Gson().fromJson(response,RootBean.class);
                if(bean.getResultCode() == 0){
                    TimeCount time = new TimeCount(60000, 1000, registerGetcode);
                    time.start();
                }else
                    Toast.makeText(RegisterActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**扫描二维码*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            registerShouquan.setText(data.getStringExtra(Intents.Scan.RESULT));
        }
    }

    @Override
    public void moreListener() {
        new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(QRCodeActivity.class)
                .initiateScan();

    }

    @Override
    public int setBaseView() {
        return R.layout.activity_register;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "注册";
    }

    @Override
    public boolean showMore() {
        return false;
    }


}
