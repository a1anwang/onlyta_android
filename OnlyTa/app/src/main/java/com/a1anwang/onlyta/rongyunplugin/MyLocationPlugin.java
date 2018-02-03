package com.a1anwang.onlyta.rongyunplugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.util.LogUtils;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;

/**
 * Created by a1anwang.com on 2018/1/24.
 */

public class MyLocationPlugin  implements IPluginModule {
    @Override
    public Drawable obtainDrawable(Context context) {
        LogUtils.e(LogUtils.TAG_1," context:"+context);

        Drawable drawable=context.getResources().getDrawable(R.drawable.icon_location_ta);
        LogUtils.e(LogUtils.TAG_1," drawable:"+drawable);

        return drawable;
    }

    @Override
    public String obtainTitle(Context context) {
        return "Ta的位置";
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        LogUtils.e("MyLocationPlugin onClick ");
        if(listener!=null){
            listener.onTA_Location_Cilck();
        }
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
    RongyunEvent.OnTA_Location_CilckListener listener;
    public void setOnClickListener(RongyunEvent.OnTA_Location_CilckListener listener) {
        this.listener=listener;
    }
}
