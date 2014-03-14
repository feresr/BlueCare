package com.workana.bluecare;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class AlertDialog extends Activity {

	private Button okBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_alert);
		okBtn = (Button) findViewById(R.id.OkBtn);
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}

		
		});
	}
	private void dismiss() {
		// TODO Auto-generated method stub
		this.finish();
	}
}
