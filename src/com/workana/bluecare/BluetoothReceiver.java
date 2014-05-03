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
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateUtils;
import android.util.Log;

public class BluetoothReceiver extends BroadcastReceiver {
	private DbHelper dbHelper;
	private static int notificationId = 1;
	public static final String TAG = "com.workana.bluecare.interaction";
	public static boolean reconnecting = false;
	public static String lastDevice = "";
	private static BluetoothDevice device = null;
	private Intent notifyChangesOnDb = new Intent(TAG);
	private long[] pattern = new long[] { 1000, 60000 };

	@Override
	public void onReceive(final Context context, Intent intent) {
		final String action = intent.getAction();
		device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
			if (device.getAddress().equals(lastDevice)) {
				// Reconnecting same device
				reconnecting = true;
			} else {
				// Connecting new device
				dbHelper = new DbHelper(context);
				dbHelper.registerConnection(device);
				context.sendBroadcast(notifyChangesOnDb);
				lastDevice = device.getAddress();
			}
		}
		if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
			Log.e("device disconnect lastDevice", lastDevice);
			new Thread() {
				public void run() {
					reconnecting = false;
					dbHelper = new DbHelper(context);
					try {

						
						sleep(getSleepTime(context));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.e("lastDevice right before the question", lastDevice);
					if (reconnecting == false) {
						dbHelper.registerDisconnection(getDevice());
						context.sendBroadcast(notifyChangesOnDb);
						showNotification(context, getDevice());
						lastDevice = "";
					}
				}
			}.start();

		}

	}

	private int getSleepTime(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		
		String prefMode = sharedPref.getString("prefMode", "2000");
		if(prefMode.equals("custom")){
			prefMode = sharedPref.getString("customDelayPreference", "2000");
			
		}
		Log.e("CHECK THIS OUT",prefMode);
		int sleepTime = (int) (Integer.parseInt(prefMode) * DateUtils.SECOND_IN_MILLIS);
		if(sleepTime > 40*DateUtils.SECOND_IN_MILLIS){
			sleepTime = (int)(60*DateUtils.SECOND_IN_MILLIS);
		}
		return sleepTime;
	}

	public BluetoothDevice getDevice() {
		return device;
	}

	private void showNotification(final Context context, BluetoothDevice device) {
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

		final SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (sharedPref.getBoolean("notifications_new_message", false)) {
			Uri alarmSound = Uri.parse(sharedPref.getString(
					"notifications_new_message_ringtone", ""));

			if (alarmSound != null) {
				mBuilder.setSound(alarmSound);

			}

			if (sharedPref.getBoolean("notifications_new_message_vibrate",
					false)) {

				mBuilder.setVibrate(pattern);

			}

			if (sharedPref.getBoolean("notifications_new_message_light", false)) {
				mBuilder.setLights(Color.RED, 500, 200);
			}

			if (sharedPref
					.getBoolean("notifications_new_message_dialog", false)) {

				Intent i = new Intent(context, DialogActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				i.putExtra("DEVICE_NAME", device.getName());
				i.putExtra("DEVICE_ADDRESS", device.getAddress());

				context.startActivity(i);

			}

			if (sharedPref.getBoolean("notifications_new_message_blinking",
					false)) {

				Intent i = new Intent(context, BlinkingActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				context.startActivity(i);

			}

			mNotificationManager.notify(++notificationId, mBuilder.build());

			new Thread() {
				public void run() {
					int sleepingTime = sharedPref.getInt("alarm_duration", 2);
					try {
						if (sleepingTime != 61) { // 61 is infinity
							sleep(sleepingTime * DateUtils.SECOND_IN_MILLIS);
						} else {
							return;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// STOP THE SOUND
					NotificationManager mNotificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					mNotificationManager.cancelAll();
				}
			}.start();
		}

	}
}
