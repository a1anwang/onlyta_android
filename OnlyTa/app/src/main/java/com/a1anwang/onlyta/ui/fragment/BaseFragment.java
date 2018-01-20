package com.a1anwang.onlyta.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a1anwang.onlyta.App;
import com.a1anwang.onlyta.ui.views.MyProgressDialog;
import com.a1anwang.onlyta.util.MySharedPreferences;

/**
 * Created by a1anwang.com on 2018/1/2.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener{
    protected Context mContext;

    private  View view;

    public MySharedPreferences mySharedPreferences;

    public App application;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mySharedPreferences=MySharedPreferences.getInstance(mContext);
        application= (App) getActivity().getApplication();
        beforeInitView();
        int layoutId=setContentLayout();
        view= inflater.inflate(layoutId, container, false);
        initView();

        afterInitView();

        return  view;
    }
    protected <T extends View> T findViewById( int id) {
        return view.findViewById(id);
    }



    /**
     * 设置布局文件
     */
    public abstract int setContentLayout();

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
    MyProgressDialog myProgressDialog;
    public void showProgressDialog(String title) {
        if (myProgressDialog == null) {
            myProgressDialog = new MyProgressDialog(mContext);

            myProgressDialog.setTitle(title);
        }

        myProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (myProgressDialog != null) {
            myProgressDialog.dismiss();
        }
    }
}
