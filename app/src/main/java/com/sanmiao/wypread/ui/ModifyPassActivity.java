package com.sanmiao.wypread.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.RootBean;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.TimeCount;
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
 * 类说明{修改密码}
 */

public class ModifyPassActivity extends BaseActivity {
    @InjectView(R.id.oldPass)
    EditText oldPass;
    @InjectView(R.id.modify_ok)
    TextView modifyOk;
    @InjectView(R.id.newPass)
    EditText newPass;
    @InjectView(R.id.newPass2)
    EditText newPass2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        GradientDrawable background = (GradientDrawable) modifyOk.getBackground();
        background.setColor(getResources().getColor(R.color.basecolor));
    }

    @OnClick({R.id.modify_ok})
    public void OnClick(View view){
        if(TextUtils.isEmpty(oldPass.getText().toString()) || oldPass.getText().length()<6)
            Toast.makeText(this, "请输入正确的原密码", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(newPass.getText().toString()) ||newPass.getText().length()<6)
            Toast.makeText(this, "请输入6~20位新密码", Toast.LENGTH_SHORT).show();
//        else if(!UtilBox.isLetterDigit(newPass2.getText().toString()) || !UtilBox.isLetterDigit(newPass.getText().toString()))
//            Toast.makeText(this, "请输入数字和字母组合的新密码", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(newPass2.getText().toString()))
            Toast.makeText(this, "请确认新密码", Toast.LENGTH_SHORT).show();
        else if (!newPass.getText().toString().equals(newPass2.getText().toString()))
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
        else
            setPass();
    }
    //修改密码
    private void setPass() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        UtilBox.showDialog(this,"修改中,请稍候");
        HashMap<String,String> map = new HashMap<>();
        map.put("userID", SharedPreferenceUtil.getStringData("userId"));
        map.put("oldPassWord",oldPass.getText().toString());
        map.put("newPassWord",newPass.getText().toString());
        OkHttpUtils.post().url(MyUrl.changePassword).params(map).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                UtilBox.dismissDialog();
                Toast.makeText(ModifyPassActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response) {
                UtilBox.dismissDialog();
                RootBean bean = new Gson().fromJson(response,RootBean.class);
                if(bean.getResultCode() == 0){
                    finish();
                }
                Toast.makeText(ModifyPassActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int setBaseView() {
        return R.layout.activity_modifypass;
    }

    @Override
    public boolean showTitle() {
        return true;
    }

    @Override
    public String setTitleText() {
        return "修改密码";
    }

    @Override
    public boolean showMore() {
        return false;
    }
}
