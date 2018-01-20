package com.a1anwang.onlyta.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * Created by a1anwang.com on 2017/12/27.
 */

public class MyUtils {

    public static boolean isValidString(String string){
        if(string!=null &&string.length()>0){
            return true;
        }
        return  false;
    }
    /**
     * 验证手机格式
     */
    public static boolean isValidMobileNum(String mobilestring) {
		/*
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][3456789]\\d{9}";// "[1]"代表第1位为数字1，"[3456789]"代表第二位可以为3、4、5、6、7、8、9中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobilestring)) {
            return false;
        } else
            return mobilestring.matches(telRegex);
    }


    public static boolean hasNetwork(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conn == null) {
            return false;
        } else {
            NetworkInfo[] infos = conn.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo info : infos) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
