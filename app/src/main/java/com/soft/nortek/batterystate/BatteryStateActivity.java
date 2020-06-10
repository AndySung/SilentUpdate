package com.soft.nortek.batterystate;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.soft.nortek.silentupdate.R;

import java.util.ArrayList;

public class BatteryStateActivity extends AppCompatActivity implements BatteryChangedReceiver.Message {
    private BatteryChangedReceiver receiver;
    static final String SERVICE_NAME = "com.android.service.SystemService";
    private ArrayList<String> mData;
    private ListView batteryState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_battery_state);
        batteryState = findViewById(R.id.battery_state_list);
        mData = new ArrayList<>(10);
        /*mData.add("开启服务");
        mData.add("停止服务");
        mData.add("判断服务是否正在运行");
        mData.add("动态注册电量变化的广播接收者");
        mData.add("取消注册");
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mData));*/
        if (receiver == null) receiver = new BatteryChangedReceiver();
        registerReceiver(receiver, getIntentFilter());//电池的状态改变广播只能通过动态方式注册
        receiver.setMessage(this);
    }

   /* @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        switch (position) {
            case 0:
                startService(new Intent(this, SystemService.class));
                break;
            case 1:
                stopService(new Intent(this, SystemService.class));
                break;
            case 2:
                Toast.makeText(this, "服务是否在运行:" + isServiceWorked(this, SERVICE_NAME), Toast.LENGTH_SHORT).show();
                break;
            case 3:
                if (receiver == null) receiver = new BatteryChangedReceiver();
                registerReceiver(receiver, getIntentFilter());//电池的状态改变广播只能通过动态方式注册
                receiver.setMessage(this);
                break;
            case 4:
                if (receiver != null) {
                    unregisterReceiver(receiver);
                    receiver = null;
                } else Toast.makeText(this, "你还没有注册", Toast.LENGTH_SHORT).show();

                break;
        }
    }*/

    private IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);//This is a sticky broadcast containing the charging state, level, and other information about the battery.
        filter.addAction(Intent.ACTION_BATTERY_LOW);//Indicates low battery condition on the device. This broadcast corresponds to the "Low battery warning" system dialog.
        filter.addAction(Intent.ACTION_BATTERY_OKAY);//This will be sent after ACTION_BATTERY_LOW once the battery has gone back up to an okay state.
        return filter;
    }

    /*public static boolean isServiceWorked(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> services = (ArrayList<ActivityManager.RunningServiceInfo>) manager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).service.getClassName().equals(serviceName)) return true;
        }
        return false;
    }*/

    @Override
    public void getMsg(String str) {
        Log.i("str:",str);
        if(str.equals("Battery States")){
            mData.clear();
        }
        mData.add(str);

        batteryState.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mData));
        //  Log.i("length:",mData.size()+"");
        // setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mData));


    }
}
