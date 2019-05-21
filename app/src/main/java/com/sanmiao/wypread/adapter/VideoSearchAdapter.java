package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.BookBean;
import com.sanmiao.wypread.bean.Books;
import com.sanmiao.wypread.utils.GlideUtil;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SunStartBaseAdapter;
import com.sanmiao.wypread.utils.ViewHolder;

import java.util.List;

import butterknife.InjectView;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/6 0006.
 * 类说明{}
 */

public class VideoSearchAdapter extends SunStartBaseAdapter {
    public VideoSearchAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_list;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView img=holder.getImageView(R.id.list_img);
        TextView title=holder.getTextView(R.id.list_title);
        ImageView VideoImg = holder.getImageView(R.id.list_video);
        LinearLayout lv=holder.get(R.id.list_lv);
        lv.setVisibility(View.GONE);
        VideoImg.setVisibility(View.VISIBLE);

        Books bean = (Books)list.get(position);


        if(!TextUtils.isEmpty(bean.getImgUrl()))
            GlideUtil.ShowImage(context, MyUrl.imgUrl+bean.getImgUrl(),img);
        else
            img.setImageResource(R.mipmap.icon_zhanweitu2);

        title.setText(bean.getName());

    }
}
