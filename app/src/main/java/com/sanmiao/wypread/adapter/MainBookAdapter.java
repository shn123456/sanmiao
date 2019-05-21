package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.CollectionBook;
import com.sanmiao.wypread.bean.MainBean;
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

public class MainBookAdapter extends SunStartBaseAdapter {
    public MainBookAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_item;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView name = holder.getTextView(R.id.item_name);
        ImageView img =holder.getImageView(R.id.item_img);
        ImageView quiteImg = holder.getImageView(R.id.item_quiteImg);
        ImageView videoImg = holder.getImageView(R.id.item_videoImg);

        CollectionBook bean = (CollectionBook)list.get(position);
        name.setText(bean.getBookName());
        if (!TextUtils.isEmpty(bean.getImgUrl()))
            GlideUtil.ShowImage(context, MyUrl.imgUrl+bean.getImgUrl(),img);
        else
            img.setImageResource(R.mipmap.ic_launcher);
    }
}
