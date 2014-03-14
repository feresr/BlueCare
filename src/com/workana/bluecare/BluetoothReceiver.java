package com.workana.bluecare;

import database.DbHelper;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothReceiver extends BroadcastReceiver {
	private DbHelper dbHelper;
	public static final String TAG = "com.workana.bluecare.interaction";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		final String action = intent.getAction();
		final BluetoothDevice device = intent
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
			Log.e(TAG, "Device connected");
			dbHelper = new DbHelper(context);
			dbHelper.registerConnection(device);
		}
		if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
			Log.e(TAG, "Device disconnected");
			dbHelper = new DbHelper(context);
			dbHelper.registerDisconnection(device);
		}
		
		Intent notifyChangesOnDb = new Intent(TAG); 
		context.sendBroadcast(notifyChangesOnDb);
		
	}
	
	
	

}
