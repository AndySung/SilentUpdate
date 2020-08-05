package com.soft.nortek.silentupdate;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.soft.nortek.custom.FileLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class OTAActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private EditText filePath;
    private Button findOTABtn,echo_btn,cat_btn,recovery_btn;
    private Button mBtStart,mBtStop;
    public boolean isStop;
    public PannelServerThread mServerThread;
    Socket socket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ota_layout);
        /* 显示App icon左侧的back键 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bindView();
        onClickView();
        verifyStoragePermissions(this);
        disableClick(false,false,false,false);    //初始化不可点击
        TestWritePurview();
    }


    private void disableClick(boolean findEnable, boolean echoEnable, boolean catEnale, boolean recoveryEnable){
        findOTABtn.setEnabled(findEnable);
        echo_btn.setEnabled(echoEnable);
        cat_btn.setEnabled(catEnale);
        recovery_btn.setEnabled(recoveryEnable);
    }

    private void onClickView() {
        findOTABtn.setOnClickListener(this);
        mBtStart.setOnClickListener(this);
        mBtStop.setOnClickListener(this);
        echo_btn.setOnClickListener(this);
        cat_btn.setOnClickListener(this);
        recovery_btn.setOnClickListener(this);
    }

    private void bindView() {
        filePath = findViewById(R.id.file_path);
        findOTABtn = findViewById(R.id.find_ota_btn);
        mBtStart = findViewById(R.id.bt_start);
        mBtStop = findViewById(R.id.bt_stop);
        echo_btn = findViewById(R.id.echo_btn);
        cat_btn = findViewById(R.id.cat_btn);
        recovery_btn = findViewById(R.id.recovery_btn);
    }

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.find_ota_btn:
                ChooseSDFileDialog sdFileDialog = new ChooseSDFileDialog(this);
                sdFileDialog.setCallBack(new ChooseSDFileDialog.ChooseFileCallBack() {
                    @Override
                    public void onChoose(final File f) {
                        if(f.getName().contains("OTA") || f.getName().contains("ota")){
                            filePath.setText("/data/media/0/"+f.getName());
                            disableClick(true, true, false, false);    //连接上socket可以操作按钮
                        }else{
                            Toast.makeText(OTAActivity.this,"Please select an OTA file",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                sdFileDialog.show();
                break;
            case R.id.bt_start:
                stopServerSocket();
                isStop = false;
                mServerThread = new PannelServerThread();
                mServerThread.start();
                sendData("touch /cache/recovery/command");
                break;
            case R.id.bt_stop:
                stopServerSocket();
                break;
            case R.id.echo_btn:
                if(filePath.getText() == null || filePath.getText().equals("")){
                    Toast.makeText(OTAActivity.this, "Please select OTA file first", Toast.LENGTH_SHORT).show();
                }else {
                    sendData("echo \"--update_package=" + filePath.getText() + "\" > /cache/recovery/command");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendData("sync");
                Toast.makeText(OTAActivity.this,"Echo command execution completed",Toast.LENGTH_SHORT).show();
                disableClick(true, true, true, false);    //连接上socket可以操作按钮
                break;
            case R.id.cat_btn:
                sendData("cat /cache/recovery/command");
                Toast.makeText(OTAActivity.this,"Cat command execution completed",Toast.LENGTH_SHORT).show();
                disableClick(true, true, true, true);    //连接上socket可以操作按钮
                break;
            case R.id.recovery_btn:
                sendData("reboot recovery");
                Toast.makeText(OTAActivity.this,"Recovery command execution completed",Toast.LENGTH_SHORT).show();
                break;

        }
    }


    public void sendData(String msg){
        // final String returnServer = mEtMessage.getText().toString();
        if (socket != null && socket.isConnected()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (socket == null) return;
                        OutputStream om = socket.getOutputStream();
                        //om.write(Constant.SERVER_TEXT.getBytes());
                        om.write(msg.getBytes());
                        //om.write("\n".getBytes());//[10]
                        om.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("Lin", e.toString());
                    }
                }
            }).start();
        }else{
            Toast.makeText(OTAActivity.this, "Please start the service first", Toast.LENGTH_SHORT).show();
        }

    }

    public void stopServerSocket() {
        isStop = true;
        try {
            if (socket != null) {
                socket.close();
            }
            Toast.makeText(OTAActivity.this, "Stop Service", Toast.LENGTH_SHORT).show();
            mBtStart.setText("Start server");
            Log.i("Lin", "Stop Service");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void TestWritePurview(){
        //sdk 大于6.0的判断
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (OTAActivity.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    OTAActivity.this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                } else {
                    FileLog.saveLog("\n Test write permission \n", "Here is to test whether the directory \"/storage/emulated/0/\" has write permission", "TestWriteLog");
                }
            }

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null && socket.isConnected()) {
               socket.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("iiiii","onDestory");
    }





    class PannelServerThread extends Thread {
        public void run() {
            try {
                String ip = "127.0.0.1";
                int port = Integer.valueOf(8888);//获取portEditText中的端口号
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip, port));
                final String socketAddress = socket.getRemoteSocketAddress().toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OTAActivity.this, "Start Service", Toast.LENGTH_SHORT).show();
                        Log.i("Lin", "启动服务");
                        mBtStart.setText("Connected");
                        disableClick(true, false, false, false);    //连接上socket可以操作按钮
                        Toast.makeText(OTAActivity.this, "Successfully established a connection with the client: " + socketAddress, Toast.LENGTH_SHORT).show();
                        Log.i("Lin", "成功建立与客户端的连接 : " + socketAddress);
                    }
                });
                while (!isStop) {
                    byte[] b = new byte[1024 * 1024];
                    InputStream in = socket.getInputStream();
                    int len = in.read(b);
                    final String s = new String(b, 0, len);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mEtReceive.setText(mEtReceive.getText().toString() + socketAddress + " : " + s);
                            Log.i("Receive:", socketAddress + " : " + s);
                        }
                    });
                    Log.i("Lin", "反馈为：" + s);

                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Lin", e.toString());
            }
        }
    }



}


