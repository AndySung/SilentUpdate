package com.soft.nortek.silentupdate;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private Button mButton,setprop_true,setprop_false,getprop_status;
    private TextView tv_text,prop_status;
    Context mContext;
    String TAG = "MainActivity";
    String SET_TRUE = "setprop resume_cx20921_ak7755 true \n";
    String SET_FALSE = "setprop resume_cx20921_ak7755 false \n";
    String GET_STATUS = "getprop resume_cx20921_ak7755 \n";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RootCmd.haveRoot();
        mContext = this;
        mButton = findViewById(R.id.button);
        tv_text = findViewById(R.id.tv_text);
        prop_status = findViewById(R.id.prop_status);
        setprop_true = findViewById(R.id.setprop_true);
        setprop_false = findViewById(R.id.setprop_false);
        getprop_status = findViewById(R.id.getprop_status);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                installSilentWithReflection("/data/local/tmp/app_update.apk",getPackageName());
                //用重启代码测试是否有系统权限
                /*PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                pm.reboot("");*/
            }
        });

        //setting true
        setprop_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                RootCmd.execRootCmd(SET_TRUE);
                try {
                    Runtime.getRuntime().exec(SET_TRUE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // Toast.makeText(MainActivity.this,"Has been set to true",Toast.LENGTH_SHORT).show();
            }
        });

        //setting false
        setprop_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                RootCmd.execRootCmd(SET_FALSE);
                try {
                    Runtime.getRuntime().exec(SET_FALSE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(MainActivity.this,"Has been set to false",Toast.LENGTH_SHORT).show();
            }
        });

        //read status
        getprop_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prop_status.setText(execRootCmd(GET_STATUS));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm.isDeviceOwnerApp(getPackageName())) {
            Log.d(TAG, "is device owner");
        } else {
            Log.d(TAG, "not device owner");
        }

        tv_text.setText("version code=" + BuildConfig.VERSION_CODE);

    }


    public void installSilentWithReflection(String filePath, String packName) {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            Method method = packageManager.getClass().getDeclaredMethod("installPackage",
                    new Class[]{Uri.class, IPackageInstallObserver.class, int.class, String.class});
            method.setAccessible(true);
            File apkFile = new File(filePath);
            Uri apkUri = Uri.fromFile(apkFile);
            method.invoke(packageManager, new Object[]{apkUri, new PackInstallObserver(), Integer.valueOf(2), packName});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    static class PackInstallObserver extends IPackageInstallObserver.Stub {
        @Override
        public void packageInstalled(String packageName, int returnCode) throws RemoteException {
            //returnCode
        }
    }

    /**
     * 执行命令并且输出结果
     */
    public static String execRootCmd(String cmd){
        try {
            //取得命令结果的输出流
            InputStream fis=Runtime.getRuntime().exec(cmd).getInputStream();
            //用一个读输出流类去读
            InputStreamReader isr=new InputStreamReader(fis);
            //用缓冲器读行
            BufferedReader br=new BufferedReader(isr);
            String result = null;
            //直到读完为止
            while((result=br.readLine())!=null)
            {
                System.out.println(result);
                return result;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

