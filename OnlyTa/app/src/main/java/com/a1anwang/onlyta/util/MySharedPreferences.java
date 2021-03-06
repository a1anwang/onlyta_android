package com.a1anwang.onlyta.util;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
	private static Context mContext;
	private SharedPreferences.Editor editor;
	private SharedPreferences preferences;

	private final String PREFERENCE_NAME = "onlyta";

	private static class MySharedPreferencesHold {
		/**
		 * 单例对象实例
		 */
		static final MySharedPreferences INSTANCE = new MySharedPreferences();
	}

	public static MySharedPreferences getInstance(Context context) {
		mContext = context;
		return MySharedPreferencesHold.INSTANCE;
	}

	/**
	 * private的构造函数用于避免外界直接使用new来实例化对象
	 */
	private MySharedPreferences() {
		preferences = mContext.getSharedPreferences(PREFERENCE_NAME, 0);
		editor = preferences.edit();
	}

	/**
	 * readResolve方法应对单例对象被序列化时候
	 */
	private Object readResolve() {
		return getInstance(mContext);
	}

	public void saveTargetId(int targetId) {
		editor.putInt("TargetId", targetId);
		editor.commit();
	}

	public int getTargetId( ){
		return preferences.getInt("TargetId",-1);
	}

	public void saveUserId(int userId) {
		editor.putInt("UserId", userId);
		editor.commit();
	}


	public int getUserId( ){
		return preferences.getInt("UserId",-1);
	}


	public void saveTargetNickname(String mTargetNickname) {
		editor.putString("TargetNickname", mTargetNickname);
		editor.commit();
	}


	public String getTargetNickname( ){
		return preferences.getString("TargetNickname","");
	}



	public void saveHeadImageURL(String url) {
		editor.putString("HeadImageURL", url);
		editor.commit();
	}


	public String getHeadImageURL( ){
		return preferences.getString("HeadImageURL","");
	}
	public void saveGender(int gender) {
		editor.putInt("gender", gender);
		editor.commit();
	}


	public int getGender( ){
		return preferences.getInt("gender",1);
	}



	public void saveNickname(String nickname) {
		editor.putString("Nickname", nickname);
		editor.commit();
	}


	public String getNickname( ){
		return preferences.getString("Nickname","");
	}

	public void savePhoneNum(String PhoneNum) {
		editor.putString("PhoneNum", PhoneNum);
		editor.commit();
	}


	public String getPhoneNum( ){
		return preferences.getString("PhoneNum","");
	}


	public void saveRongyunToken(String RongyunToken) {
		editor.putString("RongyunToken", RongyunToken);
		editor.commit();
	}


	public String getRongyunToken( ){
		return preferences.getString("RongyunToken","");
	}

	public void saveAutoRespondLocation(boolean flag) {
		editor.putBoolean("AutoRespondLocation", flag);
		editor.commit();
	}
	public boolean getAutoRespondLocation( ){
		return preferences.getBoolean("AutoRespondLocation",true);
	}

	public long getLastLocationRequestTime( ){
		return preferences.getLong("LastLocationRequestTime",0);
	}
	public void saveLastLocationRequestTime(long time ){
		editor.putLong("LastLocationRequestTime",time);
		editor.commit();
	}
}