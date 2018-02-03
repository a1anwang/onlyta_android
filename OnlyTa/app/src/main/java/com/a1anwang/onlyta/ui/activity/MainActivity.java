package com.a1anwang.onlyta.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.rongyunplugin.RongyunEvent;
import com.a1anwang.onlyta.ui.fragment.ContactFragment;
import com.a1anwang.onlyta.ui.fragment.FindFragment;
import com.a1anwang.onlyta.ui.fragment.MeFragment;
import com.a1anwang.onlyta.ui.fragment.TalkFragment;
import com.a1anwang.onlyta.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a1anwang.com on 2018/1/2.
 */

public class MainActivity extends  BaseActivity implements ViewPager.OnPageChangeListener {
    private ImageView mImageChats, mImageContact, mImageFind, mImageMe;
    private TextView mTextChats, mTextContact, mTextFind, mTextMe;
    //private ImageView moreImage, mSearchImageView;

    private ViewPager mViewPager;
    private List<Fragment> mFragments = new ArrayList<>();
    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void beforeInitView() {
        setCurrentUserInfo();

    }

    @Override
    public void onBackPressed() {
        toHome();
    }

    private void setCurrentUserInfo(){
        String uid=mySharedPreferences.getUserId()+"";
        String nickname=mySharedPreferences.getNickname();
        String headImageURL=mySharedPreferences.getHeadImageURL();
        RongyunEvent.getInstance().setCurrentUserinfo(uid,nickname,headImageURL);
        LogUtils.e(LogUtils.TAG_1,"headImageURL:"+headImageURL);
    }

    @Override
    public void initView() {
        RelativeLayout chatRLayout = (RelativeLayout) findViewById(R.id.layout_chat);
        RelativeLayout contactRLayout = (RelativeLayout) findViewById(R.id.layout_contact);
        RelativeLayout foundRLayout = (RelativeLayout) findViewById(R.id.layout_find);
        RelativeLayout mineRLayout = (RelativeLayout) findViewById(R.id.layout_me);
        mImageChats = (ImageView) findViewById(R.id.tab_img_chats);
        mImageContact = (ImageView) findViewById(R.id.tab_img_contact);
        mImageFind = (ImageView) findViewById(R.id.tab_img_find);
        mImageMe = (ImageView) findViewById(R.id.tab_img_me);

        mTextChats = (TextView) findViewById(R.id.tab_text_chats);
        mTextContact = (TextView) findViewById(R.id.tab_text_contact);
        mTextFind = (TextView) findViewById(R.id.tab_text_find);
        mTextMe = (TextView) findViewById(R.id.tab_text_me);

//        moreImage = (ImageView) findViewById(R.id.btn_more);
//        mSearchImageView = (ImageView) findViewById(R.id.btn_search);

        chatRLayout.setOnClickListener(this);
        contactRLayout.setOnClickListener(this);
        foundRLayout.setOnClickListener(this);
        mineRLayout.setOnClickListener(this);
//        moreImage.setOnClickListener(this);
//        mSearchImageView.setOnClickListener(this);

        mFragments.add(new TalkFragment());
        mFragments.add(new ContactFragment());
        mFragments.add(new FindFragment());
        mFragments.add(new MeFragment());

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mViewPager.setAdapter(fragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(mFragments.size());
        mViewPager.setOnPageChangeListener(this);
    }
    FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    };

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void afterInitView() {
        setHeadVisibility(View.GONE);

    }

    @Override
    public void onClickEvent(View v) {

        switch (v.getId()){
            case R.id.layout_chat:
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.layout_contact:
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.layout_find:
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.layout_me:
                mViewPager.setCurrentItem(3, false);
                break;
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changeTextViewColor();
        changeSelectedTabState( position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private void changeTextViewColor() {
        mImageChats.setImageResource( R.drawable.tab_chat);
        mImageContact.setImageResource(R.drawable.tab_contacts);
        mImageFind.setImageResource(R.drawable.tab_found);
        mImageMe.setImageResource(R.drawable.tab_me);
        mTextChats.setTextColor(getResources().getColor(R.color.tab_text_normal));
        mTextContact.setTextColor(getResources().getColor(R.color.tab_text_normal));
        mTextFind.setTextColor(getResources().getColor(R.color.tab_text_normal));
        mTextMe.setTextColor(getResources().getColor(R.color.tab_text_normal));
    }

    private void changeSelectedTabState(int position) {
        switch (position) {
            case 0:
                mTextChats.setTextColor(getResources().getColor(R.color.tab_text_checked));
                mImageChats.setImageResource(R.drawable.tab_chat_hover);
                break;
            case 1:
                mTextContact.setTextColor(getResources().getColor(R.color.tab_text_checked));
                mImageContact.setImageResource(R.drawable.tab_contacts_hover);
                break;
            case 2:
                mTextFind.setTextColor(getResources().getColor(R.color.tab_text_checked));
                mImageFind.setImageResource(R.drawable.tab_found_hover);
                break;
            case 3:
                mTextMe.setTextColor(getResources().getColor(R.color.tab_text_checked));
                mImageMe.setImageResource( R.drawable.tab_me_hover);
                break;
        }
    }

}
