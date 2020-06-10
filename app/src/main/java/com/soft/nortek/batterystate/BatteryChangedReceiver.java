package com.soft.nortek.batterystate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

/**
 * 电量改变的广播接收者
 */
public class BatteryChangedReceiver extends BroadcastReceiver {
	private Message message;
	@Override
	public void onReceive(Context context, Intent intent) {
		//Toast.makeText(context, "Action：" + intent.getAction(), Toast.LENGTH_SHORT).show();
		message.getMsg("Battery States");
		switch (intent.getAction()) {
			case Intent.ACTION_BATTERY_CHANGED://"android.intent.action.BATTERY_CHANGED"
				Log.i("battery", "==============电池电量改变：BATTERY_CHANGED_ACTION");
				int plugIn = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1);
				switch (plugIn) {
					case 0:
						Log.i("battery", "no connected");
						//message.getMsg("no connected");
						message.getMsg("AC powered：false");
						message.getMsg("USB powered：false");
						message.getMsg("Wireless powered：false");
						break;

					case BatteryManager.BATTERY_PLUGGED_AC:
						Log.i("battery", "AC powered：true");
						Log.i("battery", "USB powered：false");
						Log.i("battery", "Wireless powered：false");
						message.getMsg("AC powered：true");
						message.getMsg("USB powered：false");
						message.getMsg("Wireless powered：false");
						break;

					case BatteryManager.BATTERY_PLUGGED_USB:
						Log.i("battery", "AC powered：false");
						Log.i("battery", "USB powered：true");
						Log.i("battery", "Wireless powered：false");
						message.getMsg("AC powered：false");
						message.getMsg("USB powered：true");
						message.getMsg("Wireless powered：false");
						break;

					case BatteryManager.BATTERY_PLUGGED_WIRELESS:
						Log.i("battery", "AC powered：false");
						Log.i("battery", "USB powered：false");
						Log.i("battery", "Wireless powered：true");
						message.getMsg("AC powered：false");
						message.getMsg("USB powered：false");
						message.getMsg("Wireless powered：true");
						break;
				}

				Log.i("battery", "status: " + intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1));//如BATTERY_STATUS_CHARGING 正在充电
				Log.i("battery", "health: " + intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));//如BATTERY_HEALTH_COLD
				//Log.i("battery", "present: " + intent.getIntExtra(BatteryManager.EXTRA_PRESENT, -1));
				Log.i("battery", "level: " + intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
				Log.i("battery", "scale: " + intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
				Log.i("battery", "voltage: " + intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));
				Log.i("battery", "temperature: " + intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));
				Log.i("battery", "technology: " + intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY));//比如，对于锂电池是Li-ion

				message.getMsg("status: "  + intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1));
				message.getMsg("health: " + intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));
				message.getMsg("level: " + intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
				message.getMsg("scale: " + intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
				message.getMsg("voltage: " + intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));
				message.getMsg("temperature: " + intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));
				message.getMsg("technology: " + intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY));
				break;
			case Intent.ACTION_BATTERY_LOW:// "android.intent.action.BATTERY_LOW"
				Log.i("battery", "电池电量低：ACTION_BATTERY_LOW");
				message.getMsg("电池电量低：ACTION_BATTERY_LOW");
				break;
			case Intent.ACTION_BATTERY_OKAY:// "android.intent.action.BATTERY_OKAY"
				Log.i("battery", "电池已经从电量低恢复为正常：ACTION_BATTERY_OKAY");
				message.getMsg("电池已经从电量低恢复为正常：ACTION_BATTERY_OKAY");
				break;
			default:
				break;

		}
	}

	public interface Message{
		public void getMsg(String str);
	}

	public void setMessage(Message message){
		this.message = message;
	}

}