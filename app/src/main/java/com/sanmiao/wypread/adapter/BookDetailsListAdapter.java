package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.Likes;
import com.sanmiao.wypread.utils.GlideUtil;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SunStartBaseAdapter;
import com.sanmiao.wypread.utils.ViewHolder;

import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/17 0017.
 * 类说明{}
 */

public class BookDetailsListAdapter extends SunStartBaseAdapter {
    public BookDetailsListAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_item;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView img=holder.getImageView(R.id.item_img);
        TextView name =holder.getTextView(R.id.item_name);

        Likes likes = (Likes) list.get(position);
        if (!TextUtils.isEmpty(likes.getImgUrl())){
            GlideUtil.ShowImage(context, MyUrl.imgUrl+likes.getImgUrl(),img);
        }else
            img.setImageResource(R.mipmap.icon_zhanweitu2);

        name.setText(likes.getName());

    }
}
