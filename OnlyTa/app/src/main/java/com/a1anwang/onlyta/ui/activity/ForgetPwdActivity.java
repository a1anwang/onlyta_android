package com.a1anwang.onlyta.ui.activity;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.ui.views.AuthButton;
import com.a1anwang.onlyta.ui.views.MyProgressDialog;
import com.a1anwang.onlyta.util.MConfig;
import com.a1anwang.onlyta.util.MD5Util;
import com.a1anwang.onlyta.util.MyUtils;
import com.a1anwang.onlyta.util.ToastUtils;
import com.a1anwang.onlyta.util.httputil.MyHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ForgetPwdActivity extends BaseActivity {

	EditText edit_phone, edit_pwd, edit_code;

	AuthButton btn_get_code;

	@Override
	public boolean isFullScreen() {
		return false;
	}

	@Override
	public void setContentLayout() {
		setContentView(R.layout.activity_resetpwd);

	}

	@Override
	public void beforeInitView() {

	}

	@Override
	public void initView() {
		edit_code = (EditText) findViewById(R.id.edit_code);
		edit_phone = (EditText) findViewById(R.id.edit_phone);
		edit_pwd = (EditText) findViewById(R.id.edit_pwd);

		btn_get_code = (AuthButton) findViewById(R.id.btn_get_code);
		btn_get_code.setNormalStyle(getResources().getColor(R.color.blue),
				Color.WHITE, "获取验证码");

		btn_get_code.setCountdownStyle(
				getResources().getColor(R.color.light_gray), Color.DKGRAY);
		btn_get_code.setToNormal();
	}

	@Override
	public void afterInitView() {
		setHeadVisibility(View.GONE);
	}

	@Override
	public void onClickEvent(View v) {

	}
	public void backAction(View v) {
		finish();
	}
	public void getCodeAction(View v) {

		String phoneNum = edit_phone.getText().toString();

		if (!MyUtils.isValidMobileNum(edit_phone.getText().toString())) {

			ToastUtils.showToast(this, getString(R.string.phonenum_is_wrong),
					2000);
			return;
		}

		queryAuthCode(phoneNum);

	}

	public void submitAction(View v) {

		final String phoneNum = edit_phone.getText().toString();

		if (!MyUtils.isValidMobileNum(phoneNum)) {
			ToastUtils.showToast(this, getString(R.string.phonenum_is_wrong),
					2000);
			return;
		}
		String pwd = edit_pwd.getText().toString();

		if (pwd == null || pwd.length() < 6) {
			ToastUtils.showToast(this, getString(R.string.pwd_too_short), 2000);

			return;
		}

		String code = edit_code.getText().toString();

		if (code.equals("")) {
			ToastUtils.showToast(this, getString(R.string.hint_code), 2000);

			return;
		}

		final MyProgressDialog myProgressDialog = new MyProgressDialog(this);
		myProgressDialog.show();

		String url = MConfig.ServerIP + "changepwd.php";

		Map<String,String> param=new HashMap<>();
		param.put("phoneNum", phoneNum);
		param.put("pwd", MD5Util.md5(pwd));
		param.put("authcode", code);

		MyHttpUtil.doPost(url, param, new MyHttpUtil.MyHttpCallBack() {
			@Override
			public void onResponse(final String response) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						myProgressDialog.dismiss();
						try {
							JSONObject jsonObject = new JSONObject(response);

							int err = jsonObject.getInt("err");
							if (err == 0) {
								// 成功
								ToastUtils.showToast(ForgetPwdActivity.this,
										String.format("修改成功，请重新登陆", phoneNum),
										2000);
								ForgetPwdActivity.this.finish();
							} else if (err == 1) {
								// 缺少参数

							} else if (err == 2) {

								ToastUtils.showToast(ForgetPwdActivity.this,
										String.format("手机号码不存在", phoneNum),
										2000);
							} else if (err == 3) {

								ToastUtils
										.showToast(ForgetPwdActivity.this,
												String.format("验证码不正确",
														phoneNum), 2000);
							} else if (err == 4) {

								ToastUtils
										.showToast(ForgetPwdActivity.this,
												String.format("验证码已过期",
														phoneNum), 2000);
							} else {
								ToastUtils.showToast(ForgetPwdActivity.this,
										"修改失败，请重试 ", 2000);

							}
						} catch (JSONException e) {
							ToastUtils.showToast(ForgetPwdActivity.this,
									"修改失败，请重试 ", 2000);
							myProgressDialog.dismiss();
						}

					}
				});

			}

			@Override
			public void noNetwork() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						myProgressDialog.dismiss();
						ToastUtils.showToast(mContext, getString(R.string.no_network), 2000);
					}
				});
			}
			@Override
			public void onFailure(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						myProgressDialog.dismiss();
						ToastUtils.showToast(mContext, getString(R.string.access_fail), 2000);
					}
				});
			}
		});
	}

	private void queryAuthCode(final String phoneNum) {

		final MyProgressDialog myProgressDialog = new MyProgressDialog(this);
		myProgressDialog.show();

		String url = MConfig.ServerIP + "getauthcode.php";
		Map<String,String> param=new HashMap<>();
		param.put("targetPhoneNum", phoneNum);
		param.put("type", "2");
		MyHttpUtil.doPost(url, param, new MyHttpUtil.MyHttpCallBack() {
			@Override
			public void onResponse(final String response) {
				runOnUiThread(new Runnable() {

					@Override
					public void run()  	{
						myProgressDialog.dismiss();
						try {
							JSONObject jsonObject = new JSONObject(response);

							int err = jsonObject.getInt("err");
							if (err == 0) {
								// 成功
								ToastUtils.showToast(ForgetPwdActivity.this,
										String.format("验证码已发送到%s", phoneNum),
										2000);
								btn_get_code.setToCountdown();
							} else {
								ToastUtils.showToast(ForgetPwdActivity.this,
										"获取失败，请重试 ", 2000);

							}
						} catch (JSONException e) {
							ToastUtils.showToast(ForgetPwdActivity.this,
									"获取失败，请重试 ", 2000);
						}

					}
				});
			}

			@Override
			public void noNetwork() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						myProgressDialog.dismiss();
						ToastUtils.showToast(mContext, getString(R.string.no_network), 2000);
					}
				});
			}
			@Override
			public void onFailure(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						myProgressDialog.dismiss();
						ToastUtils.showToast(mContext, getString(R.string.access_fail), 2000);
					}
				});
			}
		});
	}
}
