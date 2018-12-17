package com.a1anwang.onlyta.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.rongyunplugin.RongyunEvent;
import com.a1anwang.onlyta.service.MainService;
import com.a1anwang.onlyta.util.LogUtils;

import io.rong.imlib.RongIMClient;

/**
 * Created by a1anwang.com on 2018/1/17.
 */

public class LaunchActivity extends  BaseActivity implements Animation.AnimationListener {
    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_launch);
    }

    @Override
    public void beforeInitView() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void afterInitView() {
        setHeadVisibility(View.GONE);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

        Resources res = getResources();

        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());

        Animation launchAnimation = AnimationUtils.loadAnimation(this,
                R.anim.launch_animation);
        launchAnimation.setFillEnabled(true);
        launchAnimation.setFillAfter(true);
        layout.setAnimation(launchAnimation);
        launchAnimation.setAnimationListener(this);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().build());


    }


    private void startAndBindMainService() {
        Intent intent=new Intent(this, MainService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }else{
            startService(intent);
        }
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e(" onServiceConnected");
            application.mainService=((MainService.LocalBinder) service).getService();
            if(application.userAccount==null){
                //未登录
                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                LaunchActivity.this.finish();
            } else {
                //已登录,先连接rongyun,连接过程是在 Mainservice里面进行
                if(RongyunEvent.getInstance().isConnectedIM()){
                    LogUtils.e("已成功连接IM 直接进入主页面");
                    startActivity(MainActivity.class);
                    finish();
                }else{
                    LogUtils.e("未连接IM 等待连接IM成功");
                    RongyunEvent.getInstance().addConnectListener(new RongyunEvent.RongyunConnectListener() {
                        @Override
                        public void onTokenIncorrect() {

                        }

                        @Override
                        public void onSuccess(String userid) {
                            LogUtils.e("成功连接IM 直接进入主页面");
                            startActivity(MainActivity.class);
                            finish();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onClickEvent(View v) {

    }


    @Override
    public void onAnimationEnd(Animation arg0) {
        startAndBindMainService();

    }

    @Override
    public void onAnimationRepeat(Animation arg0) {

    }

    @Override
    public void onAnimationStart(Animation arg0) {
        // TODO Auto-generated method stub

    }


//    public void connectIM(String token) {
//
//        RongyunEvent.getInstance().connectIM(this, token, new RongyunEvent.RongyunConnectListener() {
//            @Override
//            public void onTokenIncorrect() {
//
//            }
//
//            @Override
//            public void onSuccess(String userid) {

//            }
//
//            @Override
//            public void onError(RongIMClient.ErrorCode errorCode) {
//
//            }
//        });
//
//    }

}
