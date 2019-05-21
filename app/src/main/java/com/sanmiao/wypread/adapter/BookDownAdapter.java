package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
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

public class BookDownAdapter extends SunStartBaseAdapter{
    public BookDownAdapter(Context context, List<?> list, boolean Load) {
        super(context, list, Load);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_history;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView img=holder.getImageView(R.id.history_img);
        TextView title=holder.getTextView(R.id.history_title);
        TextView name=holder.getTextView(R.id.history_name);
        CheckBox check=holder.get(R.id.history_check);

        int i= SharedPreferenceUtil.getIntData("bgcolor");
        if(i==1){
            check.setButtonDrawable(R.drawable.collection_check1);
        }else if(i==2){
            check.setButtonDrawable(R.drawable.collection_check2);
        }else if(i==3){
            check.setButtonDrawable(R.drawable.collection_check3);
        }else if(i==4){
            check.setButtonDrawable(R.drawable.collection_check4);
        }else if(i==5){
            check.setButtonDrawable(R.drawable.collection_check5);
        }else{
            check.setButtonDrawable(R.drawable.collection_check);
        }

        Wypread bean = (Wypread)list.get(position);
        //设置编辑状态
        if(bean.isBianji())
            check.setVisibility(View.VISIBLE);
        else
            check.setVisibility(View.GONE);

        if(bean.isCheck()){
            check.setSelected(true);
        }else{
            check.setSelected(false);
        }

        if(!TextUtils.isEmpty(bean.getImgPath()))
            GlideUtil.ShowImage(context, MyUrl.imgUrl+bean.getImgPath(),img);
        else
            img.setImageResource(R.mipmap.icon_zhanweitu2);

        title.setText(bean.getName()==null?"":bean.getName());
        name.setText((bean.getWriteName()==null?"":bean.getWriteName())+"  |  "+(bean.getClassfiy()==null?"":bean.getClassfiy()));

    }
}
