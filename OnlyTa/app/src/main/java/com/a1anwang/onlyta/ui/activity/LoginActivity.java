package com.a1anwang.onlyta.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.model.UserAccount;
import com.a1anwang.onlyta.rongyunplugin.RongyunEvent;
import com.a1anwang.onlyta.util.MConfig;
import com.a1anwang.onlyta.util.MD5Util;
import com.a1anwang.onlyta.util.MyUtils;
import com.a1anwang.onlyta.util.ToastUtils;
import com.a1anwang.onlyta.util.httputil.MyHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.rong.imlib.RongIMClient;

public class LoginActivity extends BaseActivity {


    EditText edit_phone,edit_pwd;

    UserAccount userAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void beforeInitView() {

    }

    @Override
    public void initView() {
        edit_phone= (EditText) findViewById(R.id.edit_phone);
        edit_pwd= (EditText) findViewById(R.id.edit_pwd);
    }

    @Override
    public boolean isFullScreen() {
        return  false;
    }

    @Override
    public void afterInitView() {
        setHeadVisibility(View.GONE);
    }

    @Override
    public void onClickEvent(View v) {

    }



    public void forgetPwdAction(View v) {
        startActivity(new Intent(this, ForgetPwdActivity.class));
    }

    public void registerAction(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void phoneLoginAction(View v) {

        String phoneNum = edit_phone.getText().toString();

        if (!MyUtils.isValidMobileNum(phoneNum)) {

            ToastUtils.showToast(this, getString(R.string.phonenum_is_wrong),
                    2000);
            return;
        }

        String pwd = edit_pwd.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showToast(this, getString(R.string.pwd_is_empty), 2000);
            return;
        }

        showProgressDialog("登录中");

        String url = MConfig.ServerIP + "login.php";

        Map<String,String> param=new HashMap<>();
        param.put("phoneNum", phoneNum);
        param.put("pwd", MD5Util.md5(pwd));
        param.put("accountType", "Phone");


        MyHttpUtil.doPost(url, param, new MyHttpUtil.MyHttpCallBack() {
            @Override
            public void onResponse(final String response) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            int err = jsonObject.getInt("err");
                            if (err == 0) {
                                // 登陆成功
                                jsonObject = jsonObject.getJSONObject("data");

                                userAccount= new UserAccount();

                                userAccount.uid = jsonObject.getInt("uid");
                                userAccount.nickname = jsonObject
                                        .getString("nickname");
                                userAccount.phoneNum = jsonObject
                                        .getString("phoneNum");
                                userAccount.QQ_id = jsonObject
                                        .getString("QQ_id");
                                userAccount.WeiBo_id = jsonObject
                                        .getString("WeiBo_id");
                                userAccount.gender = jsonObject
                                        .getInt("gender");
                                userAccount.accountType = jsonObject
                                        .getString("accountType");
                                userAccount.headImageURL = jsonObject
                                        .getString("headImageURL");
                                userAccount.registerTime=jsonObject
                                        .getInt("registerTime");
                                userAccount.target_uid=jsonObject
                                        .getInt("target_uid");
                                userAccount.target_nickname=jsonObject
                                        .getString("target_nickname");
                                userAccount.rongyun_token=jsonObject
                                        .getString("rongyun_token");

                                connectRongyun(userAccount.rongyun_token);
                            } else if (err == 1) {
                                // 缺少参数
                                ToastUtils.showToast(LoginActivity.this,
                                        "登陆失败，请重试  1", 2000);
                                dismissProgressDialog();
                            } else if (err == 2) {
                                ToastUtils.showToast(LoginActivity.this,
                                        "帐号或者密码不正确，请重新输入", 2000);
                                dismissProgressDialog();
                            } else {
                                ToastUtils.showToast(LoginActivity.this,
                                        "登陆失败，请重试  2", 2000);
                                dismissProgressDialog();
                            }
                        } catch (JSONException e) {

                            Log.e("", " "+e.toString());

                            ToastUtils.showToast(LoginActivity.this,
                                    "登陆失败，请重试 3", 2000);
                            dismissProgressDialog();
                        }

                    }
                });

            }

            @Override
            public void noNetwork() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressDialog();
                        ToastUtils.showToast(mContext, getString(R.string.no_network), 2000);

                    }
                });

            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressDialog();
                        ToastUtils.showToast(mContext, getString(R.string.access_fail), 2000);

                    }
                });
            }
        });
    }

    public void connectRongyun(String token) {

        RongyunEvent.getInstance().connectIM(this, token, new RongyunEvent.RongyunConnectListener() {
            @Override
            public void onTokenIncorrect() {

            }

            @Override
            public void onSuccess(String userid) {
                dismissProgressDialog();
                application.userAccount=userAccount;
                mySharedPreferences.saveUserId(userAccount.uid);
                mySharedPreferences.saveTargetId(userAccount.target_uid);
                mySharedPreferences.saveNickname( userAccount.nickname );
                mySharedPreferences.savePhoneNum(userAccount.phoneNum);
                mySharedPreferences.saveHeadImageURL(userAccount.headImageURL);
                mySharedPreferences.saveGender(userAccount.gender);
                mySharedPreferences.saveRongyunToken(userAccount.rongyun_token);
                mySharedPreferences.saveTargetNickname(userAccount.target_nickname);
                startActivity(MainActivity.class);
                finish();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });
    }

}
