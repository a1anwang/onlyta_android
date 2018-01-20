package com.a1anwang.onlyta.util.httputil;

import okhttp3.Call;

/**
 * Created by a1anwang.com on 2018/1/17.
 */

public class MyHttpOperation  {
    public Call call;

    public void cancel(){
        call.cancel();
    }
    public boolean iscancell(){
       return call.isCanceled();
    }
    public boolean isExecuted(){
        return call.isExecuted();
    }
}
