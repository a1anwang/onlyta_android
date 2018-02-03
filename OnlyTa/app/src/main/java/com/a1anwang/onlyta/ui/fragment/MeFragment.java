package com.a1anwang.onlyta.ui.fragment;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;

import com.a1anwang.onlyta.R;

/**
 * Created by a1anwang.com on 2018/1/2.
 */

public  class MeFragment extends BaseFragment{

SwitchCompat switch_auto_location;
    @Override
    public int setContentLayout() {
        return R.layout.fragment_me;
    }

    @Override
    public void beforeInitView() {

    }

    @Override
    public void initView() {
        switch_auto_location=findViewById(R.id.switch_auto_location);
        switch_auto_location.setChecked(mySharedPreferences.getAutoRespondLocation());
        switch_auto_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mySharedPreferences.saveAutoRespondLocation(isChecked);
            }
        });
    }

    @Override
    public void afterInitView() {

    }

    @Override
    public void onClickEvent(View v) {

    }
}
