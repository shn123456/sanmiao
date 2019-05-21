package com.sanmiao.wypread.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.SlideShow;

/**
 * 作者 Yapeng Wang
 * 时间 2017/3/14 0014.
 * 类说明{ 轮播图viewHolder}
 */

public class BannerHolder implements Holder<SlideShow> {
    private View thisView;
    @Override
    public View createView(Context context) {
        thisView = View.inflate(context, R.layout.banner_layout,null);
        return thisView;
    }

    @Override
    public void UpdateUI(Context context, int position, SlideShow data) {
        ImageView iv = (ImageView) thisView.findViewById(R.id.banner_iv);
        GlideUtil.ShowImage(context,MyUrl.imgUrl+data.getImgUrl(),iv);
    }

//    @Override
//    public void UpdateUI(Context context, int position, DataBean data) {
//        ImageView iv = (ImageView) thisView.findViewById(R.id.banner_iv);
//        GlideUtil.ShowImage(context, MyUrl.baseUrl+data.getImgurl(),iv);
//    }

}
