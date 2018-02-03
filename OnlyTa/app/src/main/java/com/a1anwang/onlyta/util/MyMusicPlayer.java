package com.a1anwang.onlyta.util;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * Created by a1anwang.com on 2018/2/3.
 */

public class MyMusicPlayer {


    //提示音
    public  void startAlarm(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (notification == null) return;
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }

}
