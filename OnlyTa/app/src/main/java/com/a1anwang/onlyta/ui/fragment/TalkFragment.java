package com.a1anwang.onlyta.ui.fragment;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.util.ToastUtils;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by a1anwang.com on 2018/1/2.
 */

public  class TalkFragment extends BaseFragment{
    private String mTargetId; //目标 Id
    private Conversation.ConversationType mConversationType; //会话类型

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


        mTargetId = "13262656236";

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
