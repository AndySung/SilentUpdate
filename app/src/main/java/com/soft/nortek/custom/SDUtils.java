package com.soft.nortek.custom;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Describe：
 */

public class SDUtils {

    private static final String TAG = "文件工具类";

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();      //对应外部存储路径:/storage/emulated/0
//            sdDir1 = Environment.getDataDirectory();      //对应获取用户数据目录路径:/data
//            sdDir2 = Environment.getRootDirectory();      //对应获取系统分区根路径:/system
             // sdDir3 = Environment.getDownloadCacheDirectory();  //对应获取用户缓存目录路径:/cache
        } else {
            Log.d(TAG, "getSDPath: sd卡不存在");
        }
        Log.d(TAG, "getSDPath: " + sdDir.getAbsolutePath());
        return sdDir.getAbsolutePath();
    }
}
