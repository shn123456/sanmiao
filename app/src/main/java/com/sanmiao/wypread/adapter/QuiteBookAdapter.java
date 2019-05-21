package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.cloud.thirdparty.V;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.BookBean;
import com.sanmiao.wypread.bean.Classifies;
import com.sanmiao.wypread.bean.CollectionBook;
import com.sanmiao.wypread.ui.QuiteDetailsActivity;
import com.sanmiao.wypread.ui.BookListActivity;
import com.sanmiao.wypread.ui.QuiteDetailsActivity;
import com.sanmiao.wypread.ui.QuiteListActivity;
import com.sanmiao.wypread.ui.StartActivity;
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

public class QuiteBookAdapter extends SunStartBaseAdapter {
    public QuiteBookAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_book;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView className = holder.getTextView(R.id.read_calssName);
        TextView book1 = holder.getTextView(R.id.read_book1);
        TextView book2 = holder.getTextView(R.id.read_book2);
        TextView book3 = holder.getTextView(R.id.read_book3);
        ImageView img1 =holder.getImageView(R.id.read_bookImg1);
        ImageView img2= holder.getImageView(R.id.read_bookImg2);
        ImageView img3 = holder.getImageView(R.id.read_bookImg3);
        TextView more = holder.getTextView(R.id.read_more);
        ImageView bottom1 =holder.getImageView(R.id.read_bottom1);
        ImageView bottom2 =holder.getImageView(R.id.read_bottom2);
        ImageView bottom3 =holder.getImageView(R.id.read_bottom3);
        ImageView bian1 = holder.getImageView(R.id.bian1);
        ImageView bian2 = holder.getImageView(R.id.bian2);
        ImageView bian3 = holder.getImageView(R.id.bian3);
        LinearLayout lv1 =holder.get(R.id.lv1);
        LinearLayout lv2 =holder.get(R.id.lv2);
        LinearLayout lv3 =holder.get(R.id.lv3);


        int i= SharedPreferenceUtil.getIntData("bgcolor");
        if(i==1){
            bottom1.setImageResource(R.mipmap.icon_headset_difen);
            bottom2.setImageResource(R.mipmap.icon_headset_difen);
            bottom3.setImageResource(R.mipmap.icon_headset_difen);
        }else if(i==2){
            bottom1.setImageResource(R.mipmap.icon_headset_bohong);
            bottom2.setImageResource(R.mipmap.icon_headset_bohong);
            bottom3.setImageResource(R.mipmap.icon_headset_bohong);
        }else if(i==3){
            bottom1.setImageResource(R.mipmap.icon_headset_lan);
            bottom2.setImageResource(R.mipmap.icon_headset_lan);
            bottom3.setImageResource(R.mipmap.icon_headset_lan);
        }else if(i==4){
            bottom1.setImageResource(R.mipmap.icon_headset_caolv);
            bottom2.setImageResource(R.mipmap.icon_headset_caolv);
            bottom3.setImageResource(R.mipmap.icon_headset_caolv);
        }else if(i==5){
            bottom1.setImageResource(R.mipmap.icon_headset_yanzhi);
            bottom2.setImageResource(R.mipmap.icon_headset_yanzhi);
            bottom3.setImageResource(R.mipmap.icon_headset_yanzhi);
        }else{
            bottom1.setImageResource(R.mipmap.icon_headset);
            bottom2.setImageResource(R.mipmap.icon_headset);
            bottom3.setImageResource(R.mipmap.icon_headset);
        }

        final Classifies bean = (Classifies) list.get(position);
        className.setText(bean.getName());



        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, QuiteListActivity.class).putExtra("className",bean.getName()).putExtra("classID",bean.getClassifyID()));
            }
        });

        book1.setVisibility(View.VISIBLE);
        img1.setVisibility(View.VISIBLE);
        book2.setVisibility(View.VISIBLE);
        img2.setVisibility(View.VISIBLE);
        book3.setVisibility(View.VISIBLE);
        img3.setVisibility(View.VISIBLE);
        bian1.setVisibility(View.VISIBLE);
        bian2.setVisibility(View.VISIBLE);
        bian3.setVisibility(View.VISIBLE);
        lv1.setVisibility(View.VISIBLE);
        lv2.setVisibility(View.VISIBLE);
        lv3.setVisibility(View.VISIBLE);
        if(bean.getItems().size()==1){
            book1.setText(bean.getItems().get(0).getName());
            if(!TextUtils.isEmpty(bean.getItems().get(0).getImgUrl()))
                GlideUtil.ShowImage(context, MyUrl.imgUrl+bean.getItems().get(0).getImgUrl(),img1);
            else
                img1.setImageResource(R.mipmap.icon_zhanweitu2);

            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {context.startActivity(new Intent(context, QuiteDetailsActivity.class).putExtra("quiteID",bean.getItems().get(0).getBookID()));}
            });

            bottom1.setVisibility(View.VISIBLE);
            book2.setVisibility(View.GONE);
            img2.setVisibility(View.GONE);
            book3.setVisibility(View.GONE);
            img3.setVisibility(View.GONE);
            bian2.setVisibility(View.GONE);
            bian3.setVisibility(View.GONE);
            lv2.setVisibility(View.GONE);
            lv3.setVisibility(View.GONE);
        }else if (bean.getItems().size()==2){
            book1.setText(bean.getItems().get(0).getName());
            if(!TextUtils.isEmpty(bean.getItems().get(0).getImgUrl()))
                GlideUtil.ShowImage(context, MyUrl.imgUrl+bean.getItems().get(0).getImgUrl(),img1);
            else
                img1.setImageResource(R.mipmap.icon_zhanweitu2);

            book2.setText(bean.getItems().get(1).getName());
            if(!TextUtils.isEmpty(bean.getItems().get(1).getImgUrl()))
                GlideUtil.ShowImage(context,MyUrl.imgUrl+bean.getItems().get(1).getImgUrl(),img2);
            else
                img2.setImageResource(R.mipmap.icon_zhanweitu2);


            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {context.startActivity(new Intent(context, QuiteDetailsActivity.class).putExtra("quiteID",bean.getItems().get(0).getBookID()));}
            });


            img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, QuiteDetailsActivity.class).putExtra("quiteID",bean.getItems().get(1).getBookID()));
                }
            });

            bottom1.setVisibility(View.VISIBLE);
            bottom2.setVisibility(View.VISIBLE);
            book3.setVisibility(View.GONE);
            img3.setVisibility(View.GONE);
            bian3.setVisibility(View.GONE);
            lv3.setVisibility(View.GONE);
        }else if(bean.getItems().size()>=3){
            book1.setText(bean.getItems().get(0).getName());
            if(!TextUtils.isEmpty(bean.getItems().get(0).getImgUrl()))
                GlideUtil.ShowImage(context, MyUrl.imgUrl+bean.getItems().get(0).getImgUrl(),img1);
            else
                img1.setImageResource(R.mipmap.icon_zhanweitu2);

            book2.setText(bean.getItems().get(1).getName());
            if(!TextUtils.isEmpty(bean.getItems().get(1).getImgUrl()))
                GlideUtil.ShowImage(context,MyUrl.imgUrl+bean.getItems().get(1).getImgUrl(),img2);
            else
                img2.setImageResource(R.mipmap.icon_zhanweitu2);

            book3.setText(bean.getItems().get(2).getName());

            if(!TextUtils.isEmpty(bean.getItems().get(2).getImgUrl()))
                GlideUtil.ShowImage(context,MyUrl.imgUrl+bean.getItems().get(2).getImgUrl(),img3);
            else
                img3.setImageResource(R.mipmap.icon_zhanweitu2);


            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {context.startActivity(new Intent(context, QuiteDetailsActivity.class).putExtra("quiteID",bean.getItems().get(0).getBookID()));}
            });


            img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, QuiteDetailsActivity.class).putExtra("quiteID",bean.getItems().get(1).getBookID()));
                }
            });

            img3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, QuiteDetailsActivity.class).putExtra("quiteID",bean.getItems().get(2).getBookID()));
                }
            });
            bottom1.setVisibility(View.VISIBLE);
            bottom2.setVisibility(View.VISIBLE);
            bottom3.setVisibility(View.VISIBLE);
        }else
            return;

    }
}
