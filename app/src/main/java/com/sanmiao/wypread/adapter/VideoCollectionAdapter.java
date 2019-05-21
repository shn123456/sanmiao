package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
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
 * 时间 2017/5/5 0005.
 * 类说明{}
 */

public class VideoCollectionAdapter extends SunStartBaseAdapter {
    public VideoCollectionAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_collection;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView img = holder.getImageView(R.id.collection_img);
        TextView jindu =holder.getTextView(R.id.collection_jindu);
        TextView  bottom =holder.getTextView(R.id.collection_bottom);
        CheckBox check =holder.get(R.id.collection_check);
        ImageView videoImg = holder.getImageView(R.id.collection_VideoImg);
        videoImg.setVisibility(View.VISIBLE);
        int i= SharedPreferenceUtil.getIntData("bgcolor");
        if(i==1){
            videoImg.setImageResource(R.mipmap.icon_play_difen);
            check.setButtonDrawable(R.drawable.collection_check1);
        }else if(i==2){
            videoImg.setImageResource(R.mipmap.icon_play_bohong);
            check.setButtonDrawable(R.drawable.collection_check2);
        }else if(i==3){
            videoImg.setImageResource(R.mipmap.icon_play_lan);
            check.setButtonDrawable(R.drawable.collection_check3);
        }else if(i==4){
            videoImg.setImageResource(R.mipmap.icon_play_caolv);
            check.setButtonDrawable(R.drawable.collection_check4);
        }else if(i==5){
            videoImg.setImageResource(R.mipmap.icon_play_yanzhi);
            check.setButtonDrawable(R.drawable.collection_check5);
        }else{
            videoImg.setImageResource(R.mipmap.icon_play);
            check.setButtonDrawable(R.drawable.collection_check);
        }
        CollectionBook bean= (CollectionBook) list.get(position);
        //设置编辑状态  显示/隐藏
        if(bean.isBianji()){
            check.setVisibility(View.VISIBLE);
        }else{
            check.setVisibility(View.GONE);
        }
        //设置编辑状态  选中/未选中
        if(bean.isCheck()){
            check.setSelected(true);
        }else{
            check.setSelected(false);
        }

        if(!TextUtils.isEmpty(bean.getImgUrl()))
            GlideUtil.ShowImage(context, MyUrl.imgUrl+bean.getImgUrl(),img);
        else
            img.setImageResource(R.mipmap.icon_zhanweitu2);
        if(position<size){
            jindu.setText("已观看"+bean.getSchedule());
        }
        bottom.setText(bean.getName());
    }

    int size = 0 ;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
