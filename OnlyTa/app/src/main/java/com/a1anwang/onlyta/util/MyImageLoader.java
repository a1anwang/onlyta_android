package com.a1anwang.onlyta.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by a1anwang.com on 2018/1/20.
 */

public class MyImageLoader {
    private  static volatile  MyImageLoader instance;

    public static MyImageLoader getInstance(){
        if(instance==null){
            Class clazz=MyImageLoader.class;
            synchronized ((clazz)){
                if(instance==null){
                    instance=new MyImageLoader();
                }
            }
        }
        return instance;
    }
    public void displayCircleImage(Context mcontext, String url, ImageView imageView){
        Glide.with(mcontext).load(url).transform(new GlideCircleTransform(mcontext)).into(imageView);
    }

    public void displayImage(Context mcontext, String url, ImageView imageView){
        Glide.with(mcontext).load(url).centerCrop().into(imageView);
    }
}
