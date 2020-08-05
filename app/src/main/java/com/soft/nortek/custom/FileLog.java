package com.soft.nortek.custom;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileLog{

    /**
     * 保存日志到本地存储根目录下
     * @param message      保存的信息
     * @param fileName     保存的文件名称
     * @param messageTitle 保存的信息标题
     */
    public static void saveLog(String messageTitle,String message, String fileName) {
        String path = Environment.getExternalStorageDirectory() + "/TestWriteLog";
        File files = new File(path);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm:ss,EE", Locale.CHINA);
        String formatDate = dateFormat.format(date);
        if (!files.exists()) {
            files.mkdirs();
        }
        if (files.exists()) {
            try {
                FileWriter fw = new FileWriter(path + File.separator
                        + fileName + ".txt");
                fw.write(formatDate + " " + messageTitle + "\n");
                fw.write(message + "\n");
                fw.write("\n");
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
