package com.sanmiao.wypread.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

public class TimeCount extends CountDownTimer{
	private TextView mButton;
	
	public TimeCount(long millisInFuture, long countDownInterval,
					 TextView mButton) {
		super(millisInFuture, countDownInterval);
		this.mButton = mButton;
	}	
	@Override
	public void onTick(long millisUntilFinished) {
		// TODO Auto-generated method stub
		mButton.setClickable(false);
		mButton.setText("重新发送"+millisUntilFinished/1000+"（秒）");
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		mButton.setText("获取验证码");
		mButton.setClickable(true);
	}

}
