package com.workana.bluecare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class DialogActivity extends Activity {

	private KeyguardLock kgl;
	private WakeLock screenLock;
	private TextView deviceNameTxt;
	private TextView deviceAddressTxt;
	private Button dismissBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			String deviceName = extras.getString("DEVICE_NAME");
			String deviceAddress = extras.getString("DEVICE_ADDRESS");
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			screenLock = ((PowerManager) getSystemService(POWER_SERVICE))
					.newWakeLock(PowerManager.FULL_WAKE_LOCK
							| PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");

			KeyguardManager kgm = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
			boolean isKeyguardUp = kgm.inKeyguardRestrictedInputMode();
			kgl = kgm
					.newKeyguardLock("Blue care");

			if (isKeyguardUp) {
				kgl.disableKeyguard();
				isKeyguardUp = false;
			}

			screenLock.acquire();
			
			setContentView(R.layout.dialog_alert);
			
			deviceNameTxt = (TextView) findViewById(R.id.deviceNameTxt);
			
			deviceAddressTxt = (TextView) findViewById(R.id.macAddressTxt);
			
			deviceNameTxt.setText(deviceName);
			deviceAddressTxt.setText(deviceAddress);
		
			dismissBtn = (Button) findViewById(R.id.OkBtn);
			
			dismissBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					disposeActivity();
				}
			});
		
		}
		
		
		
	}

	private void disposeActivity() {
		this.finish();

	}

	public void onAttachedToWindow() {
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}


	@SuppressLint("Wakelock")
	@Override
	protected void onDestroy() {
		screenLock.release();
		kgl.reenableKeyguard();
		super.onDestroy();

	}
	
	

}
