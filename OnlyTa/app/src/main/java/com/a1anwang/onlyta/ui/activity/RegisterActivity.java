package com.a1anwang.onlyta.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.ui.views.AuthButton;
import com.a1anwang.onlyta.ui.views.MyProgressDialog;
import com.a1anwang.onlyta.util.MConfig;
import com.a1anwang.onlyta.util.MD5Util;
import com.a1anwang.onlyta.util.MyImageLoader;
import com.a1anwang.onlyta.util.MyUtils;
import com.a1anwang.onlyta.util.ToastUtils;
import com.a1anwang.onlyta.util.httputil.MyHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegisterActivity extends BaseActivity {

	EditText edit_phone, edit_pwd, edit_code,edit_nickname;

	AuthButton btn_get_code;

    RadioGroup radiogroup_gender;

    ImageView imageview_head;
	@Override
	public boolean isFullScreen() {
		return false;
	}

	@Override
	public void setContentLayout() {
		setContentView(R.layout.activity_register);
	}

	@Override
	public void beforeInitView() {

	}

	@Override
	public void initView() {
        imageview_head= (ImageView) findViewById(R.id.imageview_head);
        MyImageLoader.getInstance().displayCircleImage(mContext,MConfig.URL_Default_boy,imageview_head);

        radiogroup_gender= (RadioGroup) findViewById(R.id.radiogroup_gender);
        radiogroup_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_boy:
                        MyImageLoader.getInstance().displayCircleImage(mContext,MConfig.URL_Default_boy,imageview_head);
                        break;
                    case R.id.radio_girl:
                        MyImageLoader.getInstance().displayCircleImage(mContext,MConfig.URL_Default_girl,imageview_head);

                        break;
                }
            }
        });
		edit_code = (EditText) findViewById(R.id.edit_code);
		edit_phone = (EditText) findViewById(R.id.edit_phone);
		edit_pwd = (EditText) findViewById(R.id.edit_pwd);

        edit_nickname = (EditText) findViewById(R.id.edit_nickname);

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
	int REQUEST_CODE_CHOOSE=1;
    public void backAction(View v) {
	    finish();
    }
    public void selectPicAction(View v) {


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

	public void registerAction(View v) {



		final String phoneNum = edit_phone.getText().toString();

		if (!MyUtils.isValidMobileNum(phoneNum)) {
			ToastUtils.showToast(this, getString(R.string.phonenum_is_wrong),
					2000);
			return;
		}

        final String nickname = edit_nickname.getText().toString();

        if (!MyUtils.isValidString(nickname)) {
            ToastUtils.showToast(this, getString(R.string.hint_nickname),
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

		String url = MConfig.ServerIP + "register.php";


		Map<String,String> param=new HashMap<>();
		param.put("pwd", MD5Util.md5(pwd));
		param.put("phoneNum", phoneNum);
        param.put("nickname", nickname);
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
								ToastUtils.showToast(RegisterActivity.this,
										String.format("注册成功，请重新登陆", phoneNum),
										2000);
								RegisterActivity.this.finish();
							} else if (err == 1) {
								// 缺少参数

							} else if (err == 2) {

								ToastUtils.showToast(RegisterActivity.this,
										String.format("手机号码已被注册", phoneNum),
										2000);
							} else if (err == 3) {

								ToastUtils
										.showToast(RegisterActivity.this,
												String.format("验证码不正确",
														phoneNum), 2000);
							} else if (err == 4) {

								ToastUtils
										.showToast(RegisterActivity.this,
												String.format("验证码已过期",
														phoneNum), 2000);
							} else {
								ToastUtils.showToast(RegisterActivity.this,
										"注册失败，请重试 ", 2000);

							}
						} catch (JSONException e) {
							ToastUtils.showToast(RegisterActivity.this,
									"注册失败，请重试 ", 2000);
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
        param.put("type", "1");
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
                                 ToastUtils.showToast(RegisterActivity.this,
                                         String.format("验证码已发送到%s", phoneNum),
                                         2000);
                                 btn_get_code.setToCountdown();
                             } else {
                                 ToastUtils.showToast(RegisterActivity.this,
                                         "获取失败，请重试 ", 2000);

                             }
                         } catch (JSONException e) {
                             ToastUtils.showToast(RegisterActivity.this,
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

    List<Uri> mSelected;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}
