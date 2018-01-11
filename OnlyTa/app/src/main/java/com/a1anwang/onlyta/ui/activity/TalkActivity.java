package com.a1anwang.onlyta.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.util.LogUtils;

import static com.a1anwang.onlyta.util.LogUtils.TAG_1;

/**
 * Created by a1anwang.com on 2017/12/27.
 */

public class TalkActivity extends  BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_talk);
    }

    @Override
    public void beforeInitView() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void afterInitView() {
        LogUtils.e(TAG_1,"user name:"+getIntent().getData().getQueryParameter("title"));
    }

    @Override
    public void onClickEvent(View v) {

    }
}
