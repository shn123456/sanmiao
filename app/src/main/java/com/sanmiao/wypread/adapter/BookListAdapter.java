package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.BookBean;
import com.sanmiao.wypread.bean.Books;
import com.sanmiao.wypread.utils.GlideUtil;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SunStartBaseAdapter;
import com.sanmiao.wypread.utils.ViewHolder;

import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/3 0003.
 * 类说明{}
 */

public class BookListAdapter extends SunStartBaseAdapter {
    public BookListAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_list;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView title = holder.getTextView(R.id.list_title);
        ImageView img = holder.getImageView(R.id.list_img);
        TextView name = holder.getTextView(R.id.list_name);

        Books bean = (Books)list.get(position);
        title.setText(bean.getName());
        name.setText(bean.getWriter()+ "  |  "+bean.getClassName());
        if(!TextUtils.isEmpty(bean.getImgUrl()))
            GlideUtil.ShowImage(context, MyUrl.imgUrl+bean.getImgUrl(),img);
        else
            img.setImageResource(R.mipmap.icon_zhanweitu2);

    }
}
