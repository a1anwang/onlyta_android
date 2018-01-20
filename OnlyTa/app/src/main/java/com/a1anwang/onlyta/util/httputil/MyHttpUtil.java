package com.a1anwang.onlyta.util.httputil;

import android.content.Context;

import com.a1anwang.onlyta.util.LogUtils;
import com.a1anwang.onlyta.util.MyUtils;
import com.a1anwang.onlyta.util.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by a1anwang.com on 2018/1/17.
 */

public class MyHttpUtil {

    private static Context context;
    private static OkHttpClient okHttpClient;
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    public static List<Cookie> getCookies(String url){
        if(cookieStore.containsKey(url)){
            return  cookieStore.get(url);
        }else{
            return  null;
        }
    }
    public static void init(Context context){
        MyHttpUtil.context=context;
    }

    private static void initOkHttpClient(){
        if(okHttpClient==null){
            okHttpClient=new OkHttpClient.Builder().cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                    for (Cookie cookie:cookies){
//                        LogUtils.e("Alan"," cookie:"+cookie +" host"+url.host());
//
//                    }

                    cookieStore.put(url.host(), cookies);
                }
                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            }).build();
        }
    }
    public static MyHttpOperation  doGet(  String url, Map<String,String> params , final MyHttpCallBack httpCallBack){
        if (!MyUtils.hasNetwork(context)) {
            ToastUtils.showToast(context, "无网络连接",2000);
            httpCallBack.noNetwork();
            return null;
        }
        initOkHttpClient();

        Request request;

        if(params!=null&&params.size()>0){

            Set<String> keySet= params.keySet();
            url+="?";
            for (String key:keySet  ) {
                String value=params.get(key);
                url+=key+"="+value+"&";
            }
            url=url.substring(0,url.length()-1);
        }
        request = new Request.Builder() .url(url)   .build();

        LogUtils.e(LogUtils.TAG_1," get url:"+url);
        final String copy=url;
        Call call = okHttpClient.newCall(request);
        final MyHttpOperation myHttpOperation=new MyHttpOperation();
        myHttpOperation.call=call;
        myHttpOperation.call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(httpCallBack!=null){
                    httpCallBack.onFailure(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                LogUtils.e(LogUtils.TAG_1,"get url:"+copy+"\n response:"+result );
                if(httpCallBack!=null){
                    httpCallBack.onResponse(result);
                }
            }
        });
        return myHttpOperation;
    }

    public static MyHttpOperation  doPost(final String url, Map<String,String> params , final MyHttpCallBack httpCallBack){
        if (!MyUtils.hasNetwork(context)) {
            ToastUtils.showToast(context, "无网络连接",2000);
            httpCallBack.noNetwork();
            return null;
        }
        initOkHttpClient();
        RequestBody body;
        Request request;

        if(params!=null){
            FormBody.Builder fromBodyBuilder = new FormBody.Builder();
            Set<String> keySet= params.keySet();
            for (String key:keySet  ) {
                String value=params.get(key);
                fromBodyBuilder.add(key,value);
            }
            body=fromBodyBuilder.build();
            request = new Request.Builder()
                    .url(url).post(body)
                    .build();
        }else{
            request = new Request.Builder() .url(url)   .build();
        }
        LogUtils.e(LogUtils.TAG_1," post url:"+url);
        Call call = okHttpClient.newCall(request);
        final MyHttpOperation myHttpOperation=new MyHttpOperation();
        myHttpOperation.call=call;
        myHttpOperation.call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(httpCallBack!=null){
                    httpCallBack.onFailure(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                LogUtils.e(LogUtils.TAG_1,"post url:"+url+"\n response:"+result );
                if(httpCallBack!=null){
                    httpCallBack.onResponse(result);
                }
            }
        });
        return myHttpOperation;
    }


    public  interface MyHttpCallBack{
        public void onResponse(String response) ;
        public void noNetwork();
        public void onFailure(Exception e);

    }



}
