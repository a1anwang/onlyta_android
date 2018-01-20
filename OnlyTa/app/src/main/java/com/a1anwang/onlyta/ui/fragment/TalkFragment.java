package com.a1anwang.onlyta.ui.fragment;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.util.MConfig;
import com.a1anwang.onlyta.util.MyUtils;
import com.a1anwang.onlyta.util.ToastUtils;
import com.a1anwang.onlyta.util.httputil.MyHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by a1anwang.com on 2018/1/2.
 */

public  class TalkFragment extends BaseFragment{
    private String mTargetId; //目标 Id
    private Conversation.ConversationType mConversationType; //会话类型

    private LinearLayout layout_set;
    private FrameLayout layout_content;

    boolean hasTargetId;

    @Override
    public int setContentLayout() {
        return R.layout.fragment_talk;
    }

    @Override
    public void beforeInitView() {

        if(application.userAccount.target_uid<=0){
            hasTargetId=false;
        }else{
            hasTargetId=true;
            mTargetId=application.userAccount.target_uid+"";
        }

    }

    private Button btn_start_talk;
    private EditText edit_targetPhone;
    @Override
    public void initView() {
        layout_set=findViewById(R.id.layout_set);
        layout_content=findViewById(R.id.layout_content);

        btn_start_talk=findViewById(R.id.btn_start_talk);
        btn_start_talk.setOnClickListener(this);

        edit_targetPhone=findViewById(R.id.edit_targetPhone);

    }

    @Override
    public void afterInitView() {
        if(hasTargetId){
            layout_set.setVisibility(View.GONE);
            layout_content.setVisibility(View.VISIBLE);

            mConversationType = Conversation.ConversationType.PRIVATE;
        /* 新建 ConversationFragment 实例，通过 setUri() 设置相关属性*/
            ConversationFragment fragment = new ConversationFragment();
            Uri uri = Uri.parse("rong://" + mContext. getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                    .appendQueryParameter("targetId", mTargetId).build();
            fragment.setUri(uri);
        /* 加载 ConversationFragment */
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.layout_content, fragment);
            transaction.commit();
        }else{
            //还没有设置 targetId
            layout_set.setVisibility(View.VISIBLE);
            layout_content.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClickEvent(View v) {
        switch (v.getId()){
            case R.id.btn_start_talk:
                startTalk();
                break;
        }
    }

    private void startTalk() {
        String targetPhoneNum=edit_targetPhone.getText().toString().trim();
        if(!MyUtils.isValidMobileNum(targetPhoneNum)){
            ToastUtils.showToast(mContext,"请输入对方手机号码",2000);
            return;
        }

        setTargetId(targetPhoneNum);

    }

    private  void setTargetId(String targetPhoneNum){
        showProgressDialog("");

        String url = MConfig.ServerIP + "setTarget.php";

        Map<String,String> param=new HashMap<>();
        param.put("uid", application.userAccount.uid+"");
        param.put("targetPhoneNum",targetPhoneNum);



        MyHttpUtil.doPost(url, param, new MyHttpUtil.MyHttpCallBack() {
            @Override
            public void onResponse(final String response) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            int err = jsonObject.getInt("err");
                            if (err == 0) {
                                // 成功
                                JSONObject data=jsonObject.getJSONObject("data");
                                int target_uid=data.getInt("target_uid");
                                mySharedPreferences.saveTargetId(target_uid);
                                mTargetId=target_uid+"";
                                application.userAccount.target_uid=target_uid;
                                hasTargetId=true;
                                afterInitView();
                            } else if (err == 1) {
                                // 缺少参数
                                ToastUtils.showToast(mContext,
                                        "失败，请重试  1", 2000);
                                dismissProgressDialog();
                            } else if (err == 2) {
                                ToastUtils.showToast(mContext,
                                        "对方还未注册,请通知TA去注册", 2000);
                                dismissProgressDialog();
                            } else {
                                ToastUtils.showToast(mContext,
                                        "失败，请重试  2", 2000);
                                dismissProgressDialog();
                            }
                        } catch (JSONException e) {

                            Log.e("", " "+e.toString());

                            ToastUtils.showToast(mContext,
                                    "失败，请重试 3", 2000);
                            dismissProgressDialog();
                        }

                    }
                });

            }

            @Override
            public void noNetwork() {
                getActivity(). runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressDialog();
                        ToastUtils.showToast(mContext, getString(R.string.no_network), 2000);

                    }
                });

            }

            @Override
            public void onFailure(Exception e) {
                getActivity().runOnUiThread(new Runnable() {
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
