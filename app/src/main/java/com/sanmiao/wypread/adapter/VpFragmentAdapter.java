package com.sanmiao.wypread.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 作者：Bruce Lee
 * 时间 2017/3/14 14:28
 * 类说明{viewpager适配器}
 */
public class VpFragmentAdapter extends FragmentPagerAdapter {
    public List<Fragment> list;
    public VpFragmentAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list=list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
