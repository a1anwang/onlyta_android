package com.a1anwang.onlyta.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.util.ToastUtils;

import io.rong.imkit.RongIM;

/**
 * Created by a1anwang.com on 2018/1/2.
 */

public  class TalkFragment extends BaseFragment{


    @Override
    public int setContentLayout() {
        return R.layout.fragment_talk;
    }

    @Override
    public void beforeInitView() {

    }

    private Button btn_start_talk;
    private EditText edit_targetid;
    @Override
    public void initView() {
        btn_start_talk=findViewById(R.id.btn_start_talk);
        btn_start_talk.setOnClickListener(this);

        edit_targetid=findViewById(R.id.edit_targetid);
    }

    @Override
    public void afterInitView() {

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
        String targetId=edit_targetid.getText().toString().trim();
        if(targetId==null||targetId.length()<=0){
            ToastUtils.showToast(mContext,"请输入对方id",2000);
            return;
        }

        RongIM.getInstance().startPrivateChat(mContext,targetId,targetId);
    }
}
