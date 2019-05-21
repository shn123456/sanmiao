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

import butterknife.InjectView;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/5 0005.
 * 类说明{}
 */

public class QuiteCollectionAdapter extends SunStartBaseAdapter {
    int size = 0;
    public QuiteCollectionAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_collection;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView img = holder.getImageView(R.id.collection_img);
        TextView  jindu =holder.getTextView(R.id.collection_jindu);
        TextView  bottom =holder.getTextView(R.id.collection_bottom);
        CheckBox check =holder.get(R.id.collection_check);
        ImageView quiteImg = holder.getImageView(R.id.collection_QuiteImg);
        quiteImg.setVisibility(View.VISIBLE);
        int i= SharedPreferenceUtil.getIntData("bgcolor");
        if(i==1){
            check.setButtonDrawable(R.drawable.collection_check1);
            quiteImg.setImageResource(R.mipmap.icon_headset_difen);
        }else if(i==2){
            check.setButtonDrawable(R.drawable.collection_check2);
            quiteImg.setImageResource(R.mipmap.icon_headset_bohong);
        }else if(i==3){
            check.setButtonDrawable(R.drawable.collection_check3);
            quiteImg.setImageResource(R.mipmap.icon_headset_lan);
        }else if(i==4){
            check.setButtonDrawable(R.drawable.collection_check4);
            quiteImg.setImageResource(R.mipmap.icon_headset_caolv);
        }else if(i==5){
            check.setButtonDrawable(R.drawable.collection_check5);
            quiteImg.setImageResource(R.mipmap.icon_headset_yanzhi);
        }else{
            check.setButtonDrawable(R.drawable.collection_check);
            quiteImg.setImageResource(R.mipmap.icon_headset);
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
            jindu.setText("已收听"+bean.getSchedule());
        }

        bottom.setText(bean.getName());

    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
