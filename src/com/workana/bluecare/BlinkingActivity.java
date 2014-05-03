package com.workana.bluecare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

@SuppressWarnings("deprecation")
public class BlinkingActivity extends Activity {
	PowerManager manager;
	private WakeLock screenLock;
	private Handler mHandler = new Handler();
	private LinearLayout background;
	private KeyguardLock kgl;
	private boolean black = false;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		screenLock = ((PowerManager) getSystemService(POWER_SERVICE))
				.newWakeLock(PowerManager.FULL_WAKE_LOCK
						| PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
		manager = (PowerManager) getSystemService(Context.POWER_SERVICE);

		setContentView(R.layout.activity_blinking);
		background = (LinearLayout) findViewById(R.id.blinking_background);
		background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				disposeActivity();
			}
		});
		KeyguardManager kgm = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		boolean isKeyguardUp = kgm.inKeyguardRestrictedInputMode();
		kgl = kgm.newKeyguardLock("Blue care");

		if (isKeyguardUp) {
			kgl.disableKeyguard();
			isKeyguardUp = false;
		}

		screenLock.acquire();
		
		final SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		
		mHandler.post(onEverySecond);
		new Thread() {
			public void run() {
				int sleepingTime = sharedPref.getInt("alarm_duration", 2);
				try {
					if (sleepingTime != 61) { //61 is infinity
						sleep(sleepingTime * DateUtils.SECOND_IN_MILLIS);
					} else {
						return;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// STOP THE SOUND
				
				disposeActivity();
			}
		}.start();
		
		
	}

	private void disposeActivity() {
		mHandler.removeCallbacks(onEverySecond);
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
		this.finish();

	}

	public void onAttachedToWindow() {
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}

	private void turnOnOff() {

		if (black) {
			background.setBackgroundColor(Color.BLACK);
			black = false;
			Log.e("BLINKING", "BLACK");
		} else {
			background.setBackgroundColor(Color.WHITE);
			black = true;
			Log.e("BLINKING", "TRUE");
		}

	}

	@SuppressLint("Wakelock")
	@Override
	protected void onDestroy() {
		screenLock.release();

		super.onDestroy();

	}

	private Runnable onEverySecond = new Runnable() {
		public void run() {
			turnOnOff();
			mHandler.postDelayed(onEverySecond, 300);
		}
	};

}
