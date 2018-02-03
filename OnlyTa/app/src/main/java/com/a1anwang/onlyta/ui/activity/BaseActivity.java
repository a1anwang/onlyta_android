package com.a1anwang.onlyta.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.a1anwang.onlyta.App;
import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.ui.views.MyProgressDialog;
import com.a1anwang.onlyta.util.LogUtils;
import com.a1anwang.onlyta.util.MySharedPreferences;

/**
 * Created by a1anwang.com on 2017/12/27.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * 是否全屏
     */
    public boolean fullScreen = false;


    public App application;
    public MySharedPreferences mySharedPreferences;
    protected Context mContext;


    private ViewFlipper mContentView;
    protected RelativeLayout mHeadLayout;
    protected ImageButton mBtnLeft;
    protected TextView   mHeadLeftText;
    protected Button mBtnRight;
    protected TextView mTitleText;
    protected TextView mHeadRightText;

    protected void onResume() {
        super.onResume();
        LogUtils.e(LogUtils.TAG_1," onResume:"+BaseActivity.this.getClass().getSimpleName());

    }

    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.e(LogUtils.TAG_1," onCreate:"+BaseActivity.this.getClass().getSimpleName());
        super.setContentView(R.layout.activity_base);
        mContext = this;
        application= (App) getApplication();
        mySharedPreferences=MySharedPreferences.getInstance(mContext);
        fullScreen= isFullScreen();
        setFullScreen(fullScreen);
        initHeaderView();
        setContentLayout();
        beforeInitView();
        initView();
        afterInitView();



    }

    protected   void initHeaderView(){

        // 初始化公共头部
        mContentView = (ViewFlipper) super.findViewById(R.id.layout_container);
        mHeadLayout = (RelativeLayout) super.findViewById(R.id.layout_head);
        mHeadLeftText= (TextView) findViewById(R.id.tv_left);
        mHeadRightText = (TextView) findViewById(R.id.text_right);
        mBtnLeft = (ImageButton) super.findViewById(R.id.btn_left);
        mBtnRight = (Button) super.findViewById(R.id.btn_right);
        mTitleText = (TextView) super.findViewById(R.id.tv_title);
    }





    /**
     * 是否全屏
     */
    public void setFullScreen(boolean fullScreen) {
        this.fullScreen=fullScreen;
        if (fullScreen) {
            //注释掉的部分是之前继承Activity时候设置全屏的代码
//         requestWindowFeature(Window.FEATURE_NO_TITLE);
//         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//         WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        } else {
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }
    public abstract boolean isFullScreen();
    /**
     * 设置布局文件
     */
    public abstract void setContentLayout();

    /**
     * 在实例化控件之前的逻辑操作
     */
    public abstract void beforeInitView();

    /**
     * 实例化控件
     */
    public abstract void initView();



    /**
     * 实例化控件之后的操作
     */
    public abstract void afterInitView();
    /**
     * onClick方法的封装
     */
    public abstract void onClickEvent(View v);


    @Override
    public void onClick(View v) {
        onClickEvent(v);
    }
    /**
     * 获得屏幕的宽度
     */
    public int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    /**
     * 获得屏幕的高度
     */
    public int getScreenHeigh() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeigh = dm.heightPixels;
        return screenHeigh;
    }


    @Override
    public void setContentView(View view) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        mContentView.addView(view, lp);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);
    }


    /**
     * 设置头部是否可见
     *
     * @param visibility
     */
    public void setHeadVisibility(int visibility) {
        mHeadLayout.setVisibility(visibility);
    }


    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
    }

    /**
     * 点击右按钮
     */
    public void onHeadRightButtonClick(View v) {

    }

    public ImageButton getHeadLeftButton() {
        return mBtnLeft;
    }

    public void setHeadLeftButton(ImageButton leftButton) {
        this.mBtnLeft = leftButton;
    }

    public Button getHeadRightButton() {
        return mBtnRight;
    }

    public void setHeadRightButton(Button rightButton) {
        this.mBtnRight = rightButton;
    }


    /**
     * 设置左边是否可见
     *
     * @param visibility
     */
    public void setHeadLeftButtonVisibility(int visibility) {
        mBtnLeft.setVisibility(visibility);
    }

    /**
     * 设置右边是否可见
     *
     * @param visibility
     */
    public void setHeadRightButtonVisibility(int visibility) {
        mBtnRight.setVisibility(visibility);
    }

    /**
     * 设置标题
     */
    public void setTitle(int titleId) {
        setTitle(getString(titleId), false);
    }

    /**
     * 设置标题
     */
    public void setTitle(int titleId, boolean hideLeftIcon) {
        setTitle(getString(titleId), hideLeftIcon);
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        setTitle(title, false);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title, boolean hideLeftIcon) {
        mTitleText.setText(title);
        if (hideLeftIcon) {
             mBtnLeft.setVisibility(View.GONE);
        } else {
            mBtnLeft.setVisibility(View.VISIBLE);
        }
    }



    // ----------Activity跳转----------//
    protected void startActivity(Class<?> targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
    }

    // 带参数跳转
    protected void startActivity(Class<?> targetClass, String key, String value) {
        Intent intent = new Intent(this, targetClass);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    // 带请求码跳转
    protected void startActivity(Class<?> targetClass, int requestCode) {
        Intent intent = new Intent(this, targetClass);
        startActivityForResult(intent, requestCode);
    }

    // 带参数和请求码跳转
    protected void startActivity(Class<?> targetClass, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, targetClass);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    private MyProgressDialog myProgressDialog;
    public void showProgressDialog(String title) {
        if (myProgressDialog == null) {
            myProgressDialog = new MyProgressDialog(this);

            myProgressDialog.setTitle(title);
        }

        myProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (myProgressDialog != null) {
            myProgressDialog.dismiss();
        }
    }

    /**
     * 回到桌面,相当于按下home键
     */
    public void toHome(){
        Intent home=new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }
}
