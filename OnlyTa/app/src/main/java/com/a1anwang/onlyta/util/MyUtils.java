package com.a1anwang.onlyta.util;

/**
 * Created by a1anwang.com on 2017/12/27.
 */

public class MyUtils {

    public static boolean notEmptyString(String string){
        if(string!=null &&string.length()>0){
            return true;
        }
        return  false;
    }
}
