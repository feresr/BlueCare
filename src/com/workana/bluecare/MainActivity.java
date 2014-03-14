package com.workana.bluecare;

import database.ActionContract;
import database.DbHelper;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private BluetoothAdapter mBluetoothAdapter;
	private int REQUEST_ENABLE_BT = 1;
	private ListView historyListView;
	private Handler mHandler = new Handler();
	private DevicesAdapter historyAdapter;
	private TextView statusBar;
	private Intent i;
	private BroadcastReceiver mReciever = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				updateStatusBar(state);
			}

			if (action.equals(BluetoothReceiver.TAG)) {
				historyAdapter.notifyDataSetChanged();
			}
		}
	};
	private String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i(TAG, "onCreate");
		// 1. Set the views and get the adapter.
		registerReceiver(mReciever, new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED));
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		statusBar = (TextView) findViewById(R.id.statusbar);
		historyListView = (ListView) findViewById(R.id.history_listview);
		historyAdapter = new DevicesAdapter(this.getApplicationContext());
		historyListView.setAdapter(historyAdapter);
		mHandler.post(onEverySecond);
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothReceiver.TAG);
		this.registerReceiver(mReciever, filter);

		// 2. Does the device support BT at all?
		if (mBluetoothAdapter != null) {
			// 3. It does, great. Is it on?
			if (!mBluetoothAdapter.isEnabled()) {
				// 4. It's not, ask the user for permission to turn it on.
				updateStatusBar(BluetoothAdapter.STATE_OFF);
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				updateStatusBar(BluetoothAdapter.STATE_ON);
			}
		} else {
			showNoBTSupportedDialog();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// ON ACTION-BAR BUTTONS SELECTED
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_turnOnOff_bluetooth:
			if (mBluetoothAdapter != null) {
				if (mBluetoothAdapter.isEnabled()) {
					mBluetoothAdapter.disable();
				} else {
					mBluetoothAdapter.enable();
				}
			} else {
				showNoBTSupportedDialog();
			}
			break;
		case R.id.action_search:
			Intent settingsIntent = new Intent(
					android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
			startActivity(settingsIntent);
			break;
		case R.id.action_clear:
			new DbHelper(this).getWritableDatabase().delete(
					ActionContract.TABLE, null, null);
			historyAdapter.notifyDataSetChanged();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showNoBTSupportedDialog() {
		// 2.1 Show 'No BT supported' message.
	}

	public void updateStatusBar(int state) {
		switch (state) {
		case BluetoothAdapter.STATE_ON:
			statusBar.setText("BLUETOOTH ENABLED");
			statusBar.setBackgroundColor(Color.parseColor("#99CC00"));

			break;
		case BluetoothAdapter.STATE_OFF:
			statusBar.setText("BLUETOOTH DISABLED");
			statusBar.setBackgroundColor(Color.parseColor("#FF4444"));
			break;
		case BluetoothAdapter.STATE_TURNING_ON:
			statusBar.setText("TURNING ON");
			statusBar.setBackgroundColor(Color.parseColor("#FFBB33"));
			break;
		case BluetoothAdapter.STATE_TURNING_OFF:

			this.statusBar.setText("TURNING OFF");
			this.statusBar.setBackgroundColor(Color.parseColor("#FFBB33"));

			break;
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReciever);
		super.onDestroy();
	}
	
	
	  private Runnable onEverySecond = new Runnable() {
	        public void run() {
	           historyAdapter.notifyDataSetChanged();
	            mHandler.postDelayed(onEverySecond, DateUtils.SECOND_IN_MILLIS * 10);
	        }
	    };
	
}
