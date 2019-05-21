package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.BookBean;
import com.sanmiao.wypread.bean.SectionList;
import com.sanmiao.wypread.utils.GlideUtil;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SunStartBaseAdapter;
import com.sanmiao.wypread.utils.ViewHolder;

import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/5 0005.
 * 类说明{}
 */

public class VideoDetailsListAdapter extends SunStartBaseAdapter {
    public VideoDetailsListAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_details;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView img = holder.getImageView(R.id.details_img);
        ImageView down =holder.getImageView(R.id.details_down);
        down.setVisibility(View.GONE);
        TextView title =holder.getTextView(R.id.details_title);
        TextView size =holder.getTextView(R.id.details_size);
        LinearLayout lv=holder.get(R.id.quiteDown_View);
        lv.setVisibility(View.GONE);

        SectionList bean = (SectionList) list.get(position);
        if(!TextUtils.isEmpty(bean.getBookImg()))
            GlideUtil.ShowCircleImg(context, MyUrl.imgUrl+bean.getBookImg(),img);

        title.setText("名称  " +bean.getName());
        size.setText("大小: " +bean.getSize());
        if(bean.isCheck())
            title.setTextColor(context.getResources().getColor(R.color.basecolor));
        else
            title.setTextColor(context.getResources().getColor(R.color.black));


    }
}
