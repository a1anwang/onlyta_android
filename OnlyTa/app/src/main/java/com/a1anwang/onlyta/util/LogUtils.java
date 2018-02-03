package com.a1anwang.onlyta.util;

import android.util.Log;

public class LogUtils {
	public static String  TAG_1="TAG_1";
	
	public static boolean open=true;
	
	
	public static void e(String TAG,String msg){
		if(open)
		Log.e(TAG, msg);
	}
	public static void e(String msg){
		if(open)
			Log.e(TAG_1, msg);
	}
}
