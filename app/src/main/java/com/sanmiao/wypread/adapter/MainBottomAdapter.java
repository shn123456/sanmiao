package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.MainBottomBean;
import com.sanmiao.wypread.utils.SunStartBaseAdapter;
import com.sanmiao.wypread.utils.ViewHolder;

import java.util.List;


/**
 * 首页底部适配器
 */

public class MainBottomAdapter extends SunStartBaseAdapter {
    public MainBottomAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_main_bottom;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MainBottomBean bean = (MainBottomBean) list.get(position);
        TextView tv = holder.getTextView(R.id.main_bottom_tv);
        if(bean.isCheck()){
            holder.getImageView(R.id.main_bottom_img).setImageResource(bean.getCheckimgID());
        }else{
            holder.getImageView(R.id.main_bottom_img).setImageResource(bean.getImgID());
        }
        tv.setText(bean.getName());
    }
}
