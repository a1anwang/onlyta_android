package com.a1anwang.onlyta;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.a1anwang.onlyta.util.LogUtils;

import io.rong.imkit.RongIM;

import static com.a1anwang.onlyta.util.LogUtils.TAG_1;

/**
 * Created by a1anwang.com on 2017/12/27.
 */

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG_1," App onCreate");
        RongIM.init(this);
    }




}
