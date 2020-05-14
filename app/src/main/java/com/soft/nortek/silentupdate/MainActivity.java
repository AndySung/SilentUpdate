package com.soft.nortek.silentupdate;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.soft.nortek.silentupdate.adapter.LogAdapter;
import com.soft.nortek.silentupdate.data.HandShakeBean;
import com.soft.nortek.silentupdate.data.LogBean;
import com.soft.nortek.silentupdate.data.MsgDataBean;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.impl.client.action.ActionDispatcher;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    private Button mButton,setprop_true,setprop_false,getprop_status;
    private TextView tv_text,prop_status;
    Context mContext;
    String TAG = "MainActivity";
    String SET_TRUE = "setprop\bresume_cx20921_ak7755\btrue";
    String SET_FALSE = "setprop\bresume_cx20921_ak7755\bfalse";
    String GET_STATUS = "getprop resume_cx20921_ak7755";
    private RecyclerView mSendList;

    /***
     *** socket通信
     ***/
    //连接管理信息
    private ConnectionInfo mInfo;
    //连接按钮
    private Button mConnect;
    //连接的socket服务端的IP地址
    private EditText mIPET;
    //连接的socket服务端的port端口号
    private EditText mPortET;
    //连接管理类
    private IConnectionManager mManager;
    //发送的消息输入框
    private EditText mSendET;
    //OkSocket参数配置类
    private OkSocketOptions mOkOptions;
    //发送数据按钮
    private Button mSendBtn;

    //发送的log信息列表适配器
    private LogAdapter mSendLogAdapter = new LogAdapter();
    //接收的log信息列表适配器
    private LogAdapter mReceLogAdapter = new LogAdapter();

    /**Socket行为适配器**/
    private SocketActionAdapter adapter = new SocketActionAdapter() {
        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            mManager.send(new HandShakeBean());
            mConnect.setText("DisConnect");
            mIPET.setEnabled(false);
            mPortET.setEnabled(false);
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            if (e != null) {
                logSend("异常断开(Disconnected with exception):" + e.getMessage());
            } else {
                logSend("正常断开(Disconnect Manually)");
            }
            mIPET.setEnabled(true);
            mPortET.setEnabled(true);
            mConnect.setText("Connect");
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            logSend("连接失败(Connecting Failed)");
            mConnect.setText("Connect");
            mIPET.setEnabled(true);
            mPortET.setEnabled(true);
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            logRece(str);
        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            logSend(str);
        }

        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            logSend(str);
        }
    };



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
        mSendET = findViewById(R.id.send_et);
        mIPET = findViewById(R.id.ip);
        mPortET = findViewById(R.id.port);
        mConnect = findViewById(R.id.socketBtn);    //连接按钮
        mSendList = findViewById(R.id.send_list);
        mSendBtn = findViewById(R.id.send_btn);
        initManager();
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
               /* try {
                    Runtime.getRuntime().exec(SET_TRUE);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                sendToService(SET_TRUE);
                Toast.makeText(MainActivity.this,"Has been set to true",Toast.LENGTH_SHORT).show();
            }
        });

        //setting false
        setprop_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                RootCmd.execRootCmd(SET_FALSE);
                /*try {
                    Runtime.getRuntime().exec(SET_FALSE);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                sendToService(SET_FALSE);
                Toast.makeText(MainActivity.this,"Has been set to false",Toast.LENGTH_SHORT).show();
            }
        });

        //read status
        getprop_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prop_status.setText(execRootCmd(GET_STATUS));
            }
        });


        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    initManager();
                    mManager.connect();
                    mIPET.setEnabled(false);
                    mPortET.setEnabled(false);
                } else {
                    mConnect.setText("Disconnecting");
                    mManager.disconnect();
                }
            }
        });

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToService(mSendET.getText().toString());
            }
        });
    }


    public void sendToService(String msg){
        if (mManager == null) {
            return;
        }
        if (!mManager.isConnect()) {
            Toast.makeText(getApplicationContext(), "Unconnected", LENGTH_SHORT).show();
        } else {
            //String msg = mSendET.getText().toString();
            String msg1 = "{\"cmd\":55,\"data\":"+msg+"}";
            if (TextUtils.isEmpty(msg.trim())) {
                return;
            }

            MsgDataBean msgDataBean = new MsgDataBean(msg1);
            mManager.send(msgDataBean);
            mSendET.setText("");
        }
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


    /***
     * 通过IP和Port号连接socket
     * ***/
    private void initManager() {
        final Handler handler = new Handler();
        //连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
        mInfo = new ConnectionInfo(mIPET.getText().toString(), Integer.parseInt(mPortET.getText().toString()));
        /*  //不自动连接
        mOkOptions = new OkSocketOptions.Builder()
                .setReconnectionManager(new NoneReconnect())
                .setConnectTimeoutSecond(10)
                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
                    @Override
                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
                        handler.post(runnable);
                    }
                }) .build();*/
        ////获得当前连接通道的参配对象，设置掉线自动连接
        mOkOptions = new OkSocketOptions.Builder()
                .setConnectTimeoutSecond(10)
                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
                    @Override
                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
                        handler.post(runnable);
                    }
                })
                .build();
        mManager = OkSocket.open(mInfo).option(mOkOptions);
        mManager.registerReceiver(adapter);
    }

    //发送消息
    private void logSend(final String log) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            LogBean logBean = new LogBean(System.currentTimeMillis(), log);
            mSendLogAdapter.getDataList().add(0, logBean);
            mSendLogAdapter.notifyDataSetChanged();
        } else {
            final String threadName = Thread.currentThread().getName();
            new Handler(Looper.getMainLooper()).post(() -> logSend(threadName + " 线程打印(In Thread):" + log));
        }
    }

    //接收消息
    private void logRece(final String log) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            LogBean logBean = new LogBean(System.currentTimeMillis(), log);
            mReceLogAdapter.getDataList().add(0, logBean);
            mReceLogAdapter.notifyDataSetChanged();
        } else {
            final String threadName = Thread.currentThread().getName();
            new Handler(Looper.getMainLooper()).post(() -> logRece(threadName + " 线程打印(In Thread):" + log));

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // SerialPortManager.instance().close();
        if (mManager != null) {
            mManager.disconnect();
            mManager.unRegisterReceiver(adapter);
        }
    }
}

