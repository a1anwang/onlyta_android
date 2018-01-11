package com.a1anwang.onlyta.ui.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.util.LogUtils;
import com.a1anwang.onlyta.util.MyUtils;
import com.a1anwang.onlyta.util.ToastUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import static com.a1anwang.onlyta.util.LogUtils.TAG_1;

public class LoginActivity extends BaseActivity {


    EditText edit_token;
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
        edit_token= (EditText) findViewById(R.id.edit_token);
    }

    @Override
    public boolean isFullScreen() {
        return  false;
    }

    @Override
    public void afterInitView() {

    }

    @Override
    public void onClickEvent(View v) {

    }


    public void connectIMAction(View v){
        String token=edit_token.getText().toString().trim();
        if(!MyUtils.notEmptyString(token)){
            ToastUtils.showToast(this,"请输入token",2000);
        }
        connectIM(token);
    }


    /**
     * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {RongIM.init(this);} 之后调用。</p>
     * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
     * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
     *
     * @param token    从服务端获取的用户身份令牌（Token）。

     */
    public void connectIM(String token) {

        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    LogUtils.e(TAG_1," onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    LogUtils.e(TAG_1," onSuccess userid:"+userid);

                    startActivity(MainActivity.class);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtils.e(TAG_1," onError errorCode:"+errorCode);
                }
            });
        }
    }

    public   String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
