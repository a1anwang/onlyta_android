package com.a1anwang.onlyta;

import android.app.Application;

import com.a1anwang.onlyta.model.UserAccount;
import com.a1anwang.onlyta.rongyunplugin.RongyunEvent;
import com.a1anwang.onlyta.service.MainService;
import com.a1anwang.onlyta.util.LogUtils;
import com.a1anwang.onlyta.util.MySharedPreferences;
import com.a1anwang.onlyta.util.httputil.MyHttpUtil;

import io.rong.imkit.RongIM;

import static com.a1anwang.onlyta.util.LogUtils.TAG_1;

/**
 * Created by a1anwang.com on 2017/12/27.
 */



public class App extends Application{
    public UserAccount userAccount;

    public MainService mainService;
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG_1," App onCreate");
        MyHttpUtil.init(this);
        RongIM.init(this);
        RongyunEvent.getInstance().afterInit();

        initUserAccount();
        //
    }

    private void initUserAccount() {
        MySharedPreferences sharedPreferences=MySharedPreferences.getInstance(this);
        int userId= sharedPreferences.getUserId();
        if(userId>0){
            //登录过
            userAccount=new UserAccount();
            userAccount.uid=userId;
            userAccount.rongyun_token=sharedPreferences.getRongyunToken();
            userAccount.headImageURL=sharedPreferences.getHeadImageURL();
            userAccount.gender=sharedPreferences.getGender();
            userAccount.nickname=sharedPreferences.getNickname();
            userAccount.target_uid=sharedPreferences.getTargetId();
            userAccount.phoneNum=sharedPreferences.getPhoneNum();
            userAccount.target_nickname=sharedPreferences.getTargetNickname();
        }else{
            //未登录过
        }
    }


}
