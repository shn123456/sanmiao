package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.BookBean;
import com.sanmiao.wypread.bean.Classifies;
import com.sanmiao.wypread.utils.GlideUtil;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SunStartBaseAdapter;
import com.sanmiao.wypread.utils.ViewHolder;

import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/2 0002.
 * 类说明{}
 */

public class QuiteClassAdapter extends SunStartBaseAdapter {
    public QuiteClassAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_readclass;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView img =holder.getImageView(R.id.class_img);
        TextView name =holder.getTextView(R.id.class_name);

        Classifies bean = (Classifies)list.get(position);

        if(!TextUtils.isEmpty(bean.getImgUrl()))
            GlideUtil.ShowImage(context, MyUrl.imgUrl+bean.getImgUrl(),img);
        else
            img.setImageResource(R.mipmap.icon_more);

        name.setText(bean.getName());

    }
}
