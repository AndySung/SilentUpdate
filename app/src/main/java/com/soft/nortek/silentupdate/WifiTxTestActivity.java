package com.soft.nortek.silentupdate;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.GsonBuilder;
import com.soft.nortek.adapter.ChannelBondingAdapter;
import com.soft.nortek.adapter.RFChannelAdapter;
import com.soft.nortek.adapter.RateAdapter;
import com.soft.nortek.adapter.WifiModelAdapter;
import com.soft.nortek.bean.WifiModelRateBean;
import com.soft.nortek.bean.WifiSetChannelBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class WifiTxTestActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner spChannelBondingValue, spRFChannelValue, spWifiModelValue, spWifiRateValue;
    private EditText power_et, mEtReceive;
    private TextView clearLog;
    private Button mBtStart,mBtStop,mBtSend;

    private ChannelBondingAdapter adpChannelBond;
    private RFChannelAdapter adpRFChannel;

    //设置model和rate
    private WifiSetChannelBean mWifiSetChannelBean;
    private WifiModelAdapter adpWifiModel;
    private RateAdapter adpRate;
    private WifiModelRateBean mWifiModelRateBean;

    WifiManager mWifiManager;

    Button InsmodBtn,TXStartBtn,TXStopBtn;

    Socket socket;
    public PannelServerThread mServerThread;
    public boolean isStop;



    private String ChannelBondingValue,RFChannelValue,ModelValue,RateValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 显示App icon左侧的back键 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.wifi_tx_test);
        initView();
        loadData();
        register();
        closeWifi();
        setOnClick();

    }

    private void setOnClick() {
        TXStartBtn.setOnClickListener(this);
        TXStopBtn.setOnClickListener(this);
        InsmodBtn.setOnClickListener(this);
        mBtStart.setOnClickListener(this);
        mBtStop.setOnClickListener(this);
        mBtSend.setOnClickListener(this);
        clearLog.setOnClickListener(this);
    }

    //关闭Wi-Fi
    private void closeWifi() {
        mWifiManager.setWifiEnabled(false); //关闭wifi
    }

    private void initView() {
        mWifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //set channel
        spChannelBondingValue = findViewById(R.id.sp_channel_bonding);
        spRFChannelValue = findViewById(R.id.sp_rf_channel);
        adpChannelBond = new ChannelBondingAdapter(this);
        adpRFChannel = new RFChannelAdapter(this);
        power_et = findViewById(R.id.et_power);

        //set model and rate
        spWifiModelValue = findViewById(R.id.sp_model);
        spWifiRateValue = findViewById(R.id.sp_rate);
        adpWifiModel = new WifiModelAdapter(this);
        adpRate = new RateAdapter(this);

        TXStartBtn = findViewById(R.id.btn_tx_start);
        TXStopBtn = findViewById(R.id.btn_tx_stop);
        InsmodBtn =findViewById(R.id.btn_insmod);

        mBtStart = findViewById(R.id.bt_start);
        mBtStop = findViewById(R.id.bt_stop);
        mBtSend = findViewById(R.id.bt_send);

        mEtReceive = findViewById(R.id.et_receive);
        clearLog = findViewById(R.id.clearLog);

        TXStartBtn.setBackgroundColor(Color.parseColor("#b3b2b2"));
        TXStopBtn.setBackgroundColor(Color.parseColor("#b3b2b2"));

    }

    private void loadData() {
        try {
            //set channel
            InputStream inputStream = getApplicationContext().getAssets().open("wifi_channel.json");
            mWifiSetChannelBean = new GsonBuilder().create().fromJson(new InputStreamReader(inputStream), WifiSetChannelBean.class);
            spChannelBondingValue.setAdapter(adpChannelBond);
            adpChannelBond.setChannelBondingList(mWifiSetChannelBean.getChannelBondingList());
            spRFChannelValue.setAdapter(adpRFChannel);
            adpRFChannel.setRFChannelList(mWifiSetChannelBean.getChannelBondingList().get(0).getRFChannels());

            //set model and rate
            InputStream inputStreamModel = getApplicationContext().getAssets().open("wifi_model.json");
            mWifiModelRateBean = new GsonBuilder().create().fromJson(new InputStreamReader(inputStreamModel), WifiModelRateBean.class);
            spWifiModelValue.setAdapter(adpWifiModel);
            adpWifiModel.setModelList(mWifiModelRateBean.getModelList());
            spWifiRateValue.setAdapter(adpRate);
            adpRate.setRateList(mWifiModelRateBean.getModelList().get(0).getRates());

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void register() {
        spChannelBondingValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adpRFChannel.setRFChannelList(mWifiSetChannelBean.getChannelBondingList().get(position).getRFChannels());
                String RFChannel = mWifiSetChannelBean.getChannelBondingList().get(position).toString();
                ChannelBondingValue = RFChannel;
               // Toast.makeText(WifiTxTestActivity.this,RFChannel,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spWifiModelValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adpRate.setRateList(mWifiModelRateBean.getModelList().get(position).getRates());
                String Rate = mWifiModelRateBean.getModelList().get(position).toString();
                ModelValue = Rate;
               // Toast.makeText(WifiTxTestActivity.this,Rate,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spWifiRateValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              //  adpRate.setRateList(mWifiModelRateBean.getModelList().get(position).getRates());

               // List<WifiModelRateBean.RateBean> Rate =  mWifiModelRateBean.getModelList().get(position).getRates();
                RateValue = adpRate.getRateSetValue();
               // Toast.makeText(WifiTxTestActivity.this,adpRate.getName()+":Rate",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spRFChannelValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //  adpRate.setRateList(mWifiModelRateBean.getModelList().get(position).getRates());


                // List<WifiModelRateBean.RateBean> Rate =  mWifiModelRateBean.getModelList().get(position).getRates();
                RFChannelValue = adpRFChannel.getRFChannelSetValue();
                //Toast.makeText(WifiTxTestActivity.this,adpRFChannel.getName()+":RFChannel",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWifiManager.setWifiEnabled(true); //开wifi
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_insmod:
                Insmod();
                break;
            case R.id.btn_tx_start:
                if(!ChannelBondingValue.equals("None")){            //不是None 则为n_40
                    Execommand2Tx(RFChannelValue, RateValue, power_et.getText().toString(), "3");
                }else {
                    Execommand2Tx(RFChannelValue, RateValue, power_et.getText().toString(), "0");
                }
                break;
            case R.id.btn_tx_stop:
                /***
                 * 强发命令（b,g,a,n_20）
                 * **/
                String SET_TX_STOP = "iwpriv wlan0 tx 0";
                sendData(SET_TX_STOP);
                Toast.makeText(WifiTxTestActivity.this, "Stop successful", Toast.LENGTH_SHORT).show();
                TXStartBtn.setEnabled(true);
                TXStartBtn.setBackgroundColor(Color.parseColor("#89c506"));
                break;
            case R.id.bt_start:
                stopServerSocket();
                isStop = false;
                mServerThread = new PannelServerThread();
                mServerThread.start();
                break;
            case R.id.bt_stop:
                stopServerSocket();
                break;
            case R.id.bt_send:
                sendData("rmmod wlan");
                InsmodBtn.setEnabled(true);
                InsmodBtn.setBackgroundColor(Color.parseColor("#fdae28"));
                break;
            case R.id.clearLog:
                mEtReceive.setText("");
                break;
            default:
                break;
        }
    }

    public void Execommand2Tx(String setChannel,String setRate,String setPower,String is5G){
       // Toast.makeText(WifiTxTestActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
        mEtReceive.setText("");
        TXStartBtn.setEnabled(false);
        TXStartBtn.setBackgroundColor(Color.parseColor("#b3b2b2"));
        /***
         * 强发命令（b,g,a,n_20）
         * **/
        String FTM = "iwpriv wlan0 ftm 1";
        String SET_CB = "iwpriv wlan0 set_cb "+is5G;
        String SET_CHANNEL = "iwpriv wlan0 set_channel "+setChannel;
        String ENA_CHAIN = "iwpriv wlan0 ena_chain 2";
        String PWR_CNTL_MODE = "iwpriv wlan0 pwr_cntl_mode 1";
        String SET_TXRATE = "iwpriv wlan0 set_txrate "+setRate;
        String SET_TXPOWER = "iwpriv wlan0 set_txpower "+setPower;
        String SET_TX_START = "iwpriv wlan0 tx 1";     //tx 1，启动tx
        try {
            sendData(FTM);
            Thread.sleep(50);
            sendData(SET_CB);
            Thread.sleep(50);
            sendData(SET_CHANNEL);
            Thread.sleep(50);
            sendData(ENA_CHAIN);
            Thread.sleep(500);
            sendData(PWR_CNTL_MODE);
            Thread.sleep(500);
            sendData(SET_TXRATE);
            Thread.sleep(500);
            sendData(SET_TXPOWER);
            Thread.sleep(500);
            sendData(SET_TX_START);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Insmod(){
        if (socket != null && socket.isConnected()){
            try {
                sendData("insmod /system/lib/modules/wlan.ko con_mode=5");
                Thread.sleep(100);
                sendData("killall ptt_socket_app");
                Thread.sleep(100);
                sendData("ptt_socket_app -v -d -f");
                Toast.makeText(WifiTxTestActivity.this, "Insmod OK", Toast.LENGTH_SHORT).show();
                InsmodBtn.setEnabled(false);
                InsmodBtn.setBackgroundColor(Color.parseColor("#b3b2b2"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            TXStartBtn.setEnabled(true);
            TXStartBtn.setBackgroundColor(Color.parseColor("#89c506"));
            TXStopBtn.setEnabled(true);
            TXStopBtn.setBackgroundColor(Color.parseColor("#fc2a35"));
        }else{
            Toast.makeText(WifiTxTestActivity.this, "Please start the service first", Toast.LENGTH_SHORT).show();
        }
    }


    public void sendData(String msg){
        // final String returnServer = mEtMessage.getText().toString();
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
    }

    public void stopServerSocket() {
        isStop = true;
        try {
            if (socket != null) {
                socket.close();
            }
            Toast.makeText(WifiTxTestActivity.this, "Stop Service", Toast.LENGTH_SHORT).show();
            mBtStart.setText("Start server");
            Log.i("Lin", "Stop Service");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        Toast.makeText(WifiTxTestActivity.this, "Start Service", Toast.LENGTH_SHORT).show();
                        Log.i("Lin", "启动服务");
                        mBtStart.setText("Connected");
                        Toast.makeText(WifiTxTestActivity.this, "Successfully established a connection with the client: " + socketAddress, Toast.LENGTH_SHORT).show();
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
                            mEtReceive.setText(mEtReceive.getText().toString() + socketAddress + " : " + s);
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
