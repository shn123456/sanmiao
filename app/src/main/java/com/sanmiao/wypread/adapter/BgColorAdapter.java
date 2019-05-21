package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.BGColor;
import com.sanmiao.wypread.utils.SunStartBaseAdapter;
import com.sanmiao.wypread.utils.ViewHolder;

import java.util.HashMap;
import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/15 0015.
 * 类说明{}
 */

public class BgColorAdapter extends SunStartBaseAdapter {
    public BgColorAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_bgimage;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView imageView = holder.getImageView(R.id.menu_bgIv);

        BGColor bean =(BGColor) list.get(position);
        if(!bean.isCheck()){
            imageView.setImageResource(bean.getBg());
        }else{
            imageView.setImageResource(bean.getCheckBg());
        }

    }
}
