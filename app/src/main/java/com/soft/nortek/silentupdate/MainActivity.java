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

import java.io.File;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private Button mButton;
    private TextView tv_text;
    Context mContext;
    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mButton = findViewById(R.id.button);
        tv_text = findViewById(R.id.tv_text);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                installSilentWithReflection("/data/local/tmp/app_update.apk",getPackageName());

                //用重启代码测试是否有系统权限
                /*PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                pm.reboot("");*/
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
}

