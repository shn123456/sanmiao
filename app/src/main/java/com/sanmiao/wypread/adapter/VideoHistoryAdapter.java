package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.BookBean;
import com.sanmiao.wypread.bean.CollectionBook;
import com.sanmiao.wypread.utils.GlideUtil;
import com.sanmiao.wypread.utils.MyUrl;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.SunStartBaseAdapter;
import com.sanmiao.wypread.utils.ViewHolder;

import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/6 0006.
 * 类说明{}
 */

public class VideoHistoryAdapter extends SunStartBaseAdapter {
    public VideoHistoryAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_history2;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView img=holder.getImageView(R.id.history_img);
        TextView title=holder.getTextView(R.id.history_title);
        TextView name=holder.getTextView(R.id.history_name);
        ImageView videoImg = holder.getImageView(R.id.history_VideoImg);
        videoImg.setVisibility(View.VISIBLE);

        int i= SharedPreferenceUtil.getIntData("bgcolor");
        if(i==1){
            videoImg.setImageResource(R.mipmap.icon_play_difen);
        }else if(i==2){
            videoImg.setImageResource(R.mipmap.icon_play_bohong);
        }else if(i==3){
            videoImg.setImageResource(R.mipmap.icon_play_lan);
        }else if(i==4){
            videoImg.setImageResource(R.mipmap.icon_play_caolv);
        }else if(i==5){
            videoImg.setImageResource(R.mipmap.icon_play_yanzhi);
        }else{
            videoImg.setImageResource(R.mipmap.icon_play);
        }




        CollectionBook bean = (CollectionBook)list.get(position);
        if(!TextUtils.isEmpty(bean.getImgUrl()))
            GlideUtil.ShowImage(context, MyUrl.imgUrl+bean.getImgUrl(),img);
        else
            img.setImageResource(R.mipmap.icon_zhanweitu2);

        title.setText(bean.getName());
        name.setText(bean.getWriter()+"  |  "+bean.getClassify());
        name.setVisibility(View.GONE);
    }
}
