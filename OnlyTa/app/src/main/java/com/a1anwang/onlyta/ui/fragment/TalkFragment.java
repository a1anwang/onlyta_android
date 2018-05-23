package com.a1anwang.onlyta.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.service.MainService;
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

public  class TalkFragment extends BaseFragment {
    private String mTargetId; //目标 Id



    private Button btn_start_talk;
    private EditText edit_targetPhone;

    private LinearLayout layout_set;
    private LinearLayout layout_content;
    private TextView tv_title;
    boolean hasTargetId;

BroadcastReceiver receiver=new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(MainService.Action_Update_Target_Nickname)){
            String nickname=intent.getStringExtra(MainService.Extra_Target_Nickname);

            tv_title.setText(nickname);
        }
    }
};

    @Override
    public int setContentLayout() {
        return R.layout.fragment_talk;
    }

    @Override
    public void beforeInitView() {

        getActivity().registerReceiver(receiver,new IntentFilter(MainService.Action_Update_Target_Nickname));

        if(application.userAccount.target_uid<=0){
            hasTargetId=false;
        }else{
            hasTargetId=true;
            mTargetId=application.userAccount.target_uid+"";
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void initView() {
        tv_title=findViewById(R.id.tv_title);
        layout_set=findViewById(R.id.layout_set);
        layout_content=findViewById(R.id.layout_content);

        btn_start_talk=findViewById(R.id.btn_start_talk);
        btn_start_talk.setOnClickListener(this);

        edit_targetPhone=findViewById(R.id.edit_targetPhone);

    }

    @Override
    public void afterInitView() {
        if(hasTargetId){

            //设置 消息接收监听,和 位置请求监听
            application.mainService.setTargetId(mTargetId);

            layout_set.setVisibility(View.GONE);
            layout_content.setVisibility(View.VISIBLE);

        /* 新建 ConversationFragment 实例，通过 setUri() 设置相关属性*/
            ConversationFragment fragment = new ConversationFragment();
            Uri uri = Uri.parse("rong://" + mContext. getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
                    .appendQueryParameter("targetId", mTargetId).build();
            fragment.setUri(uri);
        /* 加载 ConversationFragment */
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.layout_content, fragment);
            transaction.commit();
            tv_title.setText(application.userAccount.target_nickname);

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
                runOnUiThread(new Runnable() {

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
                                String target_nickname=data.getString("target_nickname");
                                mySharedPreferences.saveTargetNickname(target_nickname);
                                mySharedPreferences.saveTargetId(target_uid);
                                mTargetId=target_uid+"";
                                application.userAccount.target_uid=target_uid;
                                application.userAccount.target_nickname=target_nickname;
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
