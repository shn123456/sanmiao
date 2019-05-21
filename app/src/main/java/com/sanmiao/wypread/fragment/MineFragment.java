package com.sanmiao.wypread.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.ui.MyCollectionActivity;
import com.sanmiao.wypread.ui.MyDownActivity;
import com.sanmiao.wypread.ui.MyHistoryActivity;
import com.sanmiao.wypread.ui.RegisterActivity;
import com.sanmiao.wypread.ui.SetActivity;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/2 0002.
 * 类说明{我的}
 */

public class MineFragment extends Fragment {
    View thisView;
    @InjectView(R.id.mine_name)
    TextView mineName;
    @InjectView(R.id.mine_Collection)
    TextView mineCollection;
    @InjectView(R.id.mine_History)
    TextView mineHistory;
    @InjectView(R.id.mine_down)
    TextView mineDown;
    @InjectView(R.id.mine_set)
    TextView mineSet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_mine, null);
        ButterKnife.inject(this, thisView);

        mineName.setText(SharedPreferenceUtil.getStringData("userName"));
        return thisView;
    }

    @OnClick({R.id.mine_Collection,R.id.mine_History,R.id.mine_down,R.id.mine_set})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.mine_Collection:
                startActivity(new Intent(getActivity(), MyCollectionActivity.class));
                break;
            case R.id.mine_History:
                startActivity(new Intent(getActivity(), MyHistoryActivity.class));
                break;
            case R.id.mine_down:
                startActivity(new Intent(getActivity(), MyDownActivity.class));
                break;
            case R.id.mine_set:
                startActivity(new Intent(getActivity(), SetActivity.class));
                break;

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
