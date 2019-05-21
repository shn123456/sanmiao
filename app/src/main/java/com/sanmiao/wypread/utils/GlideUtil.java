package com.sanmiao.wypread.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sanmiao.wypread.R;

/**
 * @Title: ${file_name}
 * @Description: ${todo}<Glide封装实体类>
 * @data: 2016/10/20 13:42
 * @version: V1.0
 */
public class GlideUtil {
    private static int LoadingImg = R.drawable.loading2;
    /**
     * 设置普通图片
     * @param context
     * @param Url
     * @param iv
     */
   public static void ShowImage(Context context, String Url, ImageView iv){
        Glide.with(context).load(Url).asBitmap().placeholder(LoadingImg).error(R.mipmap.icon_zhanweitu2).into(iv);
    }

    /**
     * 设置圆角图片
     * @param context
     * @param url
     * @param iv
     * @param rudius
     */
    public static void ShowRoundCornerImg(Context context,String url, ImageView iv, int rudius){
        Glide.with(context).load(url).placeholder(LoadingImg).transform(new GlideRoundTransform(context,rudius)).into(iv);
    }

    /**
     * 设置圆形图片
     * @param context
     * @param url
     * @param iv
     */
    public static void ShowCircleImg(Context context,String url, ImageView iv){
        Glide.with(context).load(url).placeholder(LoadingImg).transform(new GlideCircleTransform(context)).into(iv);
    }
    public static void setLoadingImg(int loadingImgId){
        LoadingImg = loadingImgId;
    }
}
