package com.workana.bluecare;

import database.DbHelper;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class BluetoothReceiver extends BroadcastReceiver {
	private DbHelper dbHelper;
	private static int notificationId = 1;
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
			showNotification(context, device);
		}
		if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
			
			Log.e("PRENDII :d"," YEAH");
		}

		Intent notifyChangesOnDb = new Intent(TAG);
		context.sendBroadcast(notifyChangesOnDb);

	}

	private void showNotification(Context context, BluetoothDevice device) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				// NOTIFICATION ICON
				.setContentTitle("Device disconnected")
				.setContentText(device.getName() + " - " + device.getAddress());
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, MainActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mBuilder.setAutoCancel(true);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);

		if (sharedPref.getBoolean("notifications_new_message", false)) {
			Uri alarmSound = Uri.parse(sharedPref.getString(
					"notifications_new_message_ringtone", ""));

			mBuilder.setSound(alarmSound);

			if (sharedPref.getBoolean("notifications_new_message_vibrate",
					false)) {

				mBuilder.setVibrate(new long[] { 1000,2000});

			}

			if (sharedPref.getBoolean("notifications_new_message_light", false)) {
				mBuilder.setLights(Color.RED, 500, 200);
			}

			if (sharedPref
					.getBoolean("notifications_new_message_dialog", false)) {
				Intent i = new Intent(context, AlertDialog.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);

			}

			mNotificationManager.notify(++notificationId, mBuilder.build());
			
		}

		
	
	}

}
