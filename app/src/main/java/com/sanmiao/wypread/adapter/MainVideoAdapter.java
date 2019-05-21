package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.CollectionBook;
import com.sanmiao.wypread.bean.MainBean;
import com.sanmiao.wypread.utils.GlideUtil;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.SunStartBaseAdapter;
import com.sanmiao.wypread.utils.ViewHolder;

import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/2 0002.
 * 类说明{}
 */

public class MainVideoAdapter extends SunStartBaseAdapter {
    public MainVideoAdapter(Context context, List<?> list, boolean Load) {
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
        ImageView videoImg = holder.getImageView(R.id.item_videoImg);
        videoImg.setVisibility(View.VISIBLE);
        int i= SharedPreferenceUtil.getIntData("bgcolor");
        if(i==1){
            videoImg.setImageResource(R.mipmap.icon_play2_difen);
        }else if(i==2){
            videoImg.setImageResource(R.mipmap.icon_play2_bohong);
        }else if(i==3){
            videoImg.setImageResource(R.mipmap.icon_play2_lan);
        }else if(i==4){
            videoImg.setImageResource(R.mipmap.icon_play2_caolv);
        }else if(i==5){
            videoImg.setImageResource(R.mipmap.icon_play2_yanzhi);
        }else{
            videoImg.setImageResource(R.mipmap.icon_play2);
        }

        CollectionBook bean = (CollectionBook)list.get(position);
        name.setText(bean.getBookName());
        if (!TextUtils.isEmpty(bean.getImgUrl()))
            GlideUtil.ShowImage(context, MyUrl.imgUrl+bean.getImgUrl(),img);
        else
            img.setImageResource(R.mipmap.ic_launcher);
    }
}
