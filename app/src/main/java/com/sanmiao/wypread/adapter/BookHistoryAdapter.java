package com.sanmiao.wypread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanmiao.wypread.R;
import com.sanmiao.wypread.bean.BookBean;
import com.sanmiao.wypread.bean.CollectionBook;
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

public class BookHistoryAdapter extends SunStartBaseAdapter {
    @InjectView(R.id.history_img)
    ImageView historyImg;
    @InjectView(R.id.history_QuiteImg)
    ImageView historyQuiteImg;
    @InjectView(R.id.history_VideoImg)
    ImageView historyVideoImg;
    @InjectView(R.id.history_title)
    TextView historyTitle;
    @InjectView(R.id.history_name)
    TextView historyName;

    public BookHistoryAdapter(Context context, List<?> list, boolean Load) {
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

        CollectionBook bean = (CollectionBook)list.get(position);
        if(!TextUtils.isEmpty(bean.getImgUrl()))
            GlideUtil.ShowImage(context, MyUrl.imgUrl+bean.getImgUrl(),img);
        else
            img.setImageResource(R.mipmap.icon_zhanweitu2);

        title.setText(bean.getName());
        name.setText(bean.getWriter()+"  |  "+bean.getClassify());

    }
}
