package com.a1anwang.onlyta.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by a1anwang.com on 2018/1/23.
 */

public class CropUtils {
    private static String mFile;

    public static String getPath() {
        //resize image to thumb
        if (mFile == null) {
            mFile = Environment.getExternalStorageDirectory() + "/" +System.currentTimeMillis()+".jpg";
        }
        return mFile;
    }

    /**
     * 调用系统照片的裁剪功能，修改编辑头像的选择模式(适配Android7.0)
     */
    public static Intent invokeSystemCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        intent.putExtra("scale", true);

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        mFile=null;
        File out = new File(getPath());
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        return intent;
    }

}
