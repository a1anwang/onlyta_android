package com.a1anwang.onlyta.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.ui.views.AuthButton;
import com.a1anwang.onlyta.ui.views.MyProgressDialog;
import com.a1anwang.onlyta.util.CropUtils;
import com.a1anwang.onlyta.util.LogUtils;
import com.a1anwang.onlyta.util.MConfig;
import com.a1anwang.onlyta.util.MD5Util;
import com.a1anwang.onlyta.util.MyImageLoader;
import com.a1anwang.onlyta.util.MyUtils;
import com.a1anwang.onlyta.util.ToastUtils;
import com.a1anwang.onlyta.util.httputil.MyHttpUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class RegisterActivity extends BaseActivity {

	EditText edit_phone, edit_pwd, edit_code,edit_nickname;

	AuthButton btn_get_code;

    RadioGroup radiogroup_gender;

    ImageView imageview_head;

    boolean selectHeadimage=false;//是否选择了图片作为头像

	int gender=1;
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
        MyImageLoader.getInstance().displayCircleImage(mContext,MConfig.URL_Default_boy,imageview_head,false);

        radiogroup_gender= (RadioGroup) findViewById(R.id.radiogroup_gender);
        radiogroup_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            	if(!selectHeadimage){
            		//如果还没有选择过头像图片,那么久切换 性别对应的默认头像
					switch (checkedId){
						case R.id.radio_boy:
							gender=1;
							MyImageLoader.getInstance().displayCircleImage(mContext,MConfig.URL_Default_boy,imageview_head,false);
							break;
						case R.id.radio_girl:
							gender=0;
							MyImageLoader.getInstance().displayCircleImage(mContext,MConfig.URL_Default_girl,imageview_head,false);

							break;
					}
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
	int REQUEST_CODE_CROP=2;
    public void backAction(View v) {
	    finish();
    }
    public void selectPicAction(View v) {

		Set<MimeType> mimeType =new HashSet<>();
		mimeType.add(MimeType.JPEG);
		mimeType.add(MimeType.PNG);
		Matisse.from(this)
				.choose(mimeType)
				.countable(true)
				.maxSelectable(1)
				.gridExpectedSize(200).theme(R.style.Matisse_Zhihu)//主题
				.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
				.thumbnailScale(0.85f)
				.imageEngine(new GlideEngine())
				.forResult(REQUEST_CODE_CHOOSE);

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

	String md5PWD;
    String mPhoneNum;
	String mNickname;
	String mCheckCode;
	public void registerAction(View v) {



		final String phoneNum = edit_phone.getText().toString();

		if (!MyUtils.isValidMobileNum(phoneNum)) {
			ToastUtils.showToast(this, getString(R.string.phonenum_is_wrong),
					2000);
			return;
		}
		mPhoneNum=phoneNum;

        final String nickname = edit_nickname.getText().toString();

        if (!MyUtils.isValidString(nickname)) {
            ToastUtils.showToast(this, getString(R.string.hint_nickname),
                    2000);
            return;
        }
		mNickname=nickname;


        String pwd = edit_pwd.getText().toString();

		if (pwd == null || pwd.length() < 6) {
			ToastUtils.showToast(this, getString(R.string.pwd_too_short), 2000);

			return;
		}
		md5PWD= MD5Util.md5(pwd);
		String code = edit_code.getText().toString();

		if (code.equals("")) {
			ToastUtils.showToast(this, getString(R.string.hint_code), 2000);

			return;
		}
		mCheckCode=code;
		showProgressDialog("");

		if(selectHeadimage){
			//选择了头像图片,那么就先获取 上传token把头像上传,然后注册用户
			getUploadToken(phoneNum);
		}else{
			//默认头像,那么直接注册
			doRegister(md5PWD,mPhoneNum,mNickname,mCheckCode,null);
		}



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



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
			List<Uri> mSelected = Matisse.obtainResult(data);
			Uri imageUri=mSelected.get(0);
			LogUtils.e(LogUtils.TAG_1," image uri:"+imageUri);
			startActivityForResult(CropUtils.invokeSystemCrop(imageUri), REQUEST_CODE_CROP);
		}else if(requestCode == REQUEST_CODE_CROP && resultCode == RESULT_OK){
			String path= CropUtils.getPath();
			LogUtils.e(LogUtils.TAG_1," crop path:"+path);
			MyImageLoader.getInstance().displayCircleImage(mContext,path,imageview_head,true);
			selectHeadimage=true;
		}
	}

	private void getUploadToken(String phoneNum) {
		String url = MConfig.ServerIP + "getQiniuUploadToken.php";


		Map<String,String> param=new HashMap<>();

		param.put("phoneNum", phoneNum);


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
								 JSONObject data=jsonObject.getJSONObject("data");
								 String qiniu_token=data.getString("token");
								 String fileKey=data.getString("fileKey");
								 LogUtils.e("alanwang","token:"+qiniu_token);
								 uploadImage(qiniu_token,fileKey ,CropUtils.getPath());
							} else if (err == 1) {
								// 缺少参数
								dismissProgressDialog();
							} else {
								ToastUtils.showToast(RegisterActivity.this,
										"注册失败，请重试 ", 2000);
								dismissProgressDialog();
							}
						} catch (JSONException e) {
							dismissProgressDialog();
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


	private void uploadImage(String qiniu_token, final String fileKey, String imagepath){



		//指定zone的具体区域
//FixedZone.zone0   华东机房
//FixedZone.zone1   华北机房
//FixedZone.zone2   华南机房
//FixedZone.zoneNa0 北美机房
//自动识别上传区域
//AutoZone.autoZone
Configuration config = new Configuration.Builder()
.build();
UploadManager uploadManager = new UploadManager(config);
		String  data = imagepath;//= <File对象、或 文件路径、或 字节数组>
		String key = fileKey;//= <指定七牛服务上的文件名，或 null>;
		String token = qiniu_token;//= <从服务端SDK获取>;
		uploadManager.put(data, key, token,
				new UpCompletionHandler() {
					@Override
					public void complete(String key, ResponseInfo info, JSONObject res) {
						//res包含hash、key等信息，具体字段取决于上传策略的设置
						if(info.isOK()) {
							Log.e("qiniu", "Upload Success");


							doRegister(md5PWD,mPhoneNum,mNickname,mCheckCode,fileKey);
						} else {
							Log.e("qiniu", "Upload Fail");
							dismissProgressDialog();
							ToastUtils.showToast(RegisterActivity.this,
									"注册失败，请重试 ", 2000);

							//如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
						}
						Log.e("qiniu", key + ",\r\n " + info + ",\r\n " + res);
					}


				}, null);
	}

	private  void doRegister(String md5PWD,String phoneNum,String nickname,String code,String headimageKey){
		String url = MConfig.ServerIP + "register.php";
		Map<String,String> param=new HashMap<>();
		param.put("pwd",md5PWD);
		param.put("phoneNum", phoneNum);
		param.put("nickname", nickname);
		param.put("authcode", code);
		param.put("gender", gender+"");
		if(headimageKey!=null){
			param.put("headimageKey", headimageKey);
		}

		MyHttpUtil.doPost(url, param, new MyHttpUtil.MyHttpCallBack() {
			@Override
			public void onResponse(final String response) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissProgressDialog();
						try {
							JSONObject jsonObject = new JSONObject(response);

							int err = jsonObject.getInt("err");
							if (err == 0) {
								// 成功
								ToastUtils.showToast(RegisterActivity.this, "注册成功，请重新登陆",
										2000);
								RegisterActivity.this.finish();
							} else if (err == 1) {
								// 缺少参数

							} else if (err == 2) {

								ToastUtils.showToast(RegisterActivity.this, "手机号码已被注册",
										2000);
							} else if (err == 3) {

								ToastUtils
										.showToast(RegisterActivity.this, "验证码不正确" , 2000);
							} else if (err == 4) {

								ToastUtils
										.showToast(RegisterActivity.this, "验证码已过期" , 2000);
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

}
