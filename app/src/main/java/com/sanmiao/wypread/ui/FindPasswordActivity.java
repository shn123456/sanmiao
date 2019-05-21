package com.sanmiao.wypread.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.utils.MyUrl;
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
 * Created by Administrator on 2017/3/10 0010.
 */

public class FindPasswordActivity extends BaseActivity {

    @InjectView(R.id.findpass_tel)
    EditText findpassTel;//手机号
    @InjectView(R.id.findpass_code)
    EditText findpassCode;//验证码
    @InjectView(R.id.findpass_getcode)
    TextView findpassGetcode;//获取验证码
    @InjectView(R.id.findpass_pass)
    EditText findpassPass;//密码
    @InjectView(R.id.findpass_pass2)
    EditText findpassPass2;//确认密码
    @InjectView(R.id.findpass_ok)
    TextView findpassOk;//提交
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        //弹出数字键盘
        findpassTel.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        findpassCode.setInputType(EditorInfo.TYPE_CLASS_PHONE);

    }

    @OnClick({R.id.findpass_getcode,R.id.findpass_ok})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.findpass_getcode:
                if (TextUtils.isEmpty(findpassTel.getText().toString()) == true) {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (UtilBox.isMobileNO(findpassTel.getText().toString()) == false) {
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取验证码
                Getcode();
                break;
            case R.id.findpass_ok:
                if (TextUtils.isEmpty(findpassTel.getText().toString()) == true) {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (UtilBox.isMobileNO(findpassTel.getText().toString()) == false) {
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(findpassCode.getText().toString()) == true) {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (findpassCode.getText().toString().length() <6) {
                    Toast.makeText(this, "请输入六位的验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(findpassPass.getText().toString()) == true) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (findpassPass.getText().length()<6||findpassPass.getText().toString().length()>20) {
                    Toast.makeText(this, "密码格式输入不正确,请输入6—20位密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(findpassPass2.getText().toString()) == true) {
                    Toast.makeText(this, "请输入确认密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (findpassPass2.getText().toString().length()<6||findpassPass2.getText().toString().length()>20) {
                    Toast.makeText(this, "确认密码格式输入不正确,请输入6—20位密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!findpassPass2.getText().toString().equals(findpassPass.getText().toString())) {
                    Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                findpassWord();
                break;
            default:
                break;
        }
    }
     //找回密码
    private void findpassWord() {
        UtilBox.showDialog(this,"修改密码,请稍候");
        HashMap<String,String > map = new HashMap<>();
        map.put("phone",findpassTel.getText().toString());
        map.put("code",findpassCode.getText().toString());
        map.put("passWord",findpassPass.getText().toString());
        OkHttpUtils.post().url(MyUrl.findPassWord).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                UtilBox.dismissDialog();
                Toast.makeText(FindPasswordActivity.this, "获取网络链接失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean = new Gson().fromJson(response,RootBean.class);
                Toast.makeText(FindPasswordActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                if(bean.getResultCode() == 0){
                    finish();
                }
            }
        });
    }

    //获取验证码
    private void Getcode() {
        UtilBox.showDialog(this,"获取验证码,请稍候");
        HashMap<String,String> map = new HashMap<>();
        map.put("phone",findpassTel.getText().toString());
        map.put("type","2");
        OkHttpUtils.post().url(MyUrl.getCode).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(FindPasswordActivity.this, "获取网络连接失败", Toast.LENGTH_SHORT).show();
                UtilBox.dismissDialog();
            }
            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean = new Gson().fromJson(response,RootBean.class);
                if(bean.getResultCode() == 0){
                    TimeCount time = new TimeCount(60000, 1000, findpassGetcode);
                    time.start();
                }else
                    Toast.makeText(FindPasswordActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int setBaseView() {
        return R.layout.activity_findpassword;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "忘记密码";
    }

    @Override
    public boolean showMore() {
        return false;
    }

}
