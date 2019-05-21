package com.sanmiao.wypread.dao;

import android.content.Context;

import com.sanmiao.wypread.Wypread;

import java.util.List;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/10 0010.
 * 类说明{完成对某一张表的具体操作}
 */

public class CommonUtils {
    private DaoManager daoManager;

    //构造方法
    public CommonUtils(Context context) {
        daoManager = DaoManager.getInstance();
        daoManager.initManager(context);
    }

    /**
     * 对数据库中Wypread表的插入操作
     * @param wypread
     * @return
     */
    public boolean insertWypread(Wypread wypread) {
        boolean flag = false;
        flag = daoManager.getDaoSession().insert(wypread) != -1 ? true : false;
        return flag;
    }

    /**
     * 批量插入
     * @param wypreads
     * @return
     */
    public boolean inserMultWypreads(final List<Wypread> wypreads) {
        //标识
        boolean flag = false;
        try {
            //插入操作耗时
            daoManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Wypread student : wypreads) {
                        daoManager.getDaoSession().insertOrReplace(student);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 修改
     * @param wypread
     * @return
     */
    public boolean updateWypread(Wypread wypread){
        boolean flag = false;
        try{
            daoManager.getDaoSession().update(wypread);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  flag;
    }


    /**
     * 删除
     *
     * @param wypread
     * @return
     */
    public boolean deleteWypread(Wypread wypread) {
        boolean flag = false;
        try {
            //删除指定ID
            daoManager.getDaoSession().delete(wypread);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询单条
     *
     * @param key
     * @return
     */
    public Wypread listOneWypread(long key) {
        return daoManager.getDaoSession().load(Wypread.class, key);
    }
    /**
     * 全部查询
     *
     * @return
     */
    public List<Wypread> listAll() {
        return daoManager.getDaoSession().loadAll(Wypread.class);
    }




}
