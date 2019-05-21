package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.bean.FileBean;
import com.sanmiao.wypread.utils.GlideUtil;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SunStartBaseAdapter;
import com.sanmiao.wypread.utils.ViewHolder;

import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/20 0020.
 * 类说明{}
 */

public class FileAdapter extends SunStartBaseAdapter {
    public FileAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_file;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView img = holder.getImageView(R.id.file_img);
        TextView  title = holder.getTextView(R.id.file_title);
        TextView  size = holder.getTextView(R.id.file_size);

        Wypread bean =(Wypread)list.get(position);
        if(!TextUtils.isEmpty(bean.getImgPath()))
            GlideUtil.ShowCircleImg(context, MyUrl.imgUrl + bean.getImgPath(),img);
        else
            img.setImageResource(R.mipmap.icon_zhanweitu2);

        title.setText(bean.getName());

        if(bean.isCheck())
            title.setTextColor(context.getResources().getColor(R.color.basecolor));
        else
            title.setTextColor(context.getResources().getColor(R.color.black));


    }
}
