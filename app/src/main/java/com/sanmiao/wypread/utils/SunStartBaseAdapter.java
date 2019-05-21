package com.sanmiao.wypread.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

/**
 * @Title: ${file_name}
 * @Description: ${todo}<recycle通用adapter>
 * @data: 2016/9/6 15:51
 * @version: V1.0
 */
public abstract class SunStartBaseAdapter extends RecyclerView.Adapter<RVHolder>{
    public List<?> list;

    public Context context;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    TextView tv;
    private boolean Load;
    public SunStartBaseAdapter(Context context, List<?> list, boolean Load) {
        this.list = list;
        this.context = context;
        tv = new TextView(context);
        this.Load = Load;
    }
    public void setTvString(String text){
        tv.setText(text);
    }

    @Override
    public RVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == TYPE_ITEM){
            view = LayoutInflater.from(context).inflate(onCreateViewLayoutID(viewType), parent,false);
            return new RVHolder(view);
        }else{
            tv.setPadding(20,20,20,20);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(tv);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if(Load){
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }else{
            return TYPE_ITEM;
        }

    }

    public abstract int onCreateViewLayoutID(int viewType);


    @Override
    public void onViewRecycled(final RVHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final RVHolder holder, final int position) {
        if(!(holder instanceof FooterViewHolder)){
            onBindViewHolder(holder.getViewHolder(), position);
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(null, v, holder.getPosition(), holder.getItemId());
                    }
                });
            }

            if(onItemLongClickListener !=null){
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemLongClickListener.onItemLongClick(null,v,holder.getPosition(),holder.getItemId());
                        return true;
                    }
                });
            }

        }
    }

    public abstract void onBindViewHolder(ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        if (Load){
            return list.size()+1;
        }else{
            return list.size();
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener;

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private AdapterView.OnItemLongClickListener onItemLongClickListener;

    public  AdapterView.OnItemLongClickListener getOnItemLongClickListener(){
        return  onItemLongClickListener;
    }
    public void setOnItemLongClcikListener(AdapterView.OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener=onItemLongClickListener;
    }


    class FooterViewHolder extends RVHolder {

        public FooterViewHolder(View view) {
            super(view);
        }

    }

}
