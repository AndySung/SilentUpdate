package com.soft.nortek.silentupdate;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
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

import com.soft.nortek.batterystate.BatteryStateActivity;
import com.soft.nortek.command.OtherCmmandActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import permissions.dispatcher.NeedsPermission;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button Btn_Update,BtnServer,BtnClient,BtnTX,BtnBatteryState,other_command_btn;
    private TextView tv_text,prop_status,mTvIp;
    Context mContext;
    String TAG = "MainActivity";
    String SET_TRUE = "setprop\bresume_cx20921_ak7755\btrue";
    String SET_FALSE = "setprop\bresume_cx20921_ak7755\bfalse";
    String GET_STATUS = "getprop resume_cx20921_ak7755";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RootCmd.haveRoot();
        mContext = this;
        initView();
        setOnClick();
    }

    private void setOnClick() {
        Btn_Update.setOnClickListener(this);
        BtnClient.setOnClickListener(this);
        BtnServer.setOnClickListener(this);
        BtnTX.setOnClickListener(this);
        BtnBatteryState.setOnClickListener(this);
        other_command_btn.setOnClickListener(this);
    }


    void initView(){
        Btn_Update = findViewById(R.id.btn_update);
        tv_text = findViewById(R.id.tv_text);
        BtnServer = findViewById(R.id.btn_server);
        BtnClient = findViewById(R.id.btn_client);
        mTvIp = findViewById(R.id.tv_ip);
        BtnTX = findViewById(R.id.btn_tx);
        BtnBatteryState = findViewById(R.id.btn_battery_state);
        other_command_btn = findViewById(R.id.other_command_btn);
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


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_battery_state:
                startActivity(new Intent(MainActivity.this, BatteryStateActivity.class));//电池服务
                break;
            case R.id.btn_server:
                startActivity(new Intent(MainActivity.this, ServerActivity.class));//服务器
                break;
            case R.id.btn_client:
                startActivity(new Intent(MainActivity.this, ClientActivity.class));//客户端
                break;
            case R.id.btn_update:
                installSilentWithReflection("/data/local/tmp/app_update.apk",getPackageName());
                //用重启代码测试是否有系统权限
                /*PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                pm.reboot("");*/
                break;
            case R.id.btn_tx:
                //去设置Wi-Fi相关数值
                startActivity(new Intent(MainActivity.this, WifiTxTestActivity.class));
                break;
            case R.id.other_command_btn:
                startActivity(new Intent(MainActivity.this, OtherCmmandActivity.class));
                break;
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

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void needsPermission() {
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//    }
}

