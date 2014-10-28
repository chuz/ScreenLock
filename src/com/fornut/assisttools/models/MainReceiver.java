package com.fornut.assisttools.models;

import com.fornut.assisttools.MainActivity;
import com.fornut.assisttools.R;
import com.fornut.assisttools.R.string;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MainReceiver extends BroadcastReceiver {

	static boolean DBG = true;
	static String TAG = "ScreenLock-PhoneCallReceiver";
	
	Context mContext;
	
	TelephonyManager mTelephonyManager;
	
	DevicePolicyManager mDevicePolicyManager;
	ComponentName mComponentName;
	
	SharedPreferences mSharedPreferences;
	
	static final int MSG_BASE = 0;
	static final int MSG_LOCKNOW = MSG_BASE + 1;
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_LOCKNOW:
				lockScreenNow(mContext);
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(DBG)Log.d(TAG, "action:" + intent.getAction());
		
		if(mContext == null){
			mContext = context;
		}
		
		if(mTelephonyManager == null){
			mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		}
		
		if(mDevicePolicyManager == null){
			mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		}

		if(mComponentName == null){
			mComponentName = new ComponentName(context, AdminReceiver.class);
		}
		
		if(mSharedPreferences == null){
			mSharedPreferences = context.getSharedPreferences(MainActivity.SharedPreferences_Name, Context.MODE_PRIVATE);
		}
		
		if(DBG)Log.d(TAG, "mTelephonyManager.getCallState:" + mTelephonyManager.getCallState());
		switch (mTelephonyManager.getCallState()) {
		case TelephonyManager.CALL_STATE_RINGING:

			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			if(mSharedPreferences.getBoolean(MainActivity.CONFIG_PHONE_CALL_SCREENLOCK, false))mHandler.sendEmptyMessageDelayed(MSG_LOCKNOW, 3000);
			break;
		case TelephonyManager.CALL_STATE_IDLE:
			
			break;
		default:
			break;
		}
	}

	private void lockScreenNow(Context context) {
		if(context == null) 
			return;
		
		boolean isActive = mDevicePolicyManager.isAdminActive(mComponentName);
		if(isActive){
			mDevicePolicyManager.lockNow();
		}else{
			Toast.makeText(context, context.getResources().getString(R.string.toast_app_no_active), Toast.LENGTH_LONG).show();
		}
	}
	
}
