package com.workana.bluecare;

import java.util.List;

import database.DbHelper;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DevicesAdapter extends BaseAdapter {

	private Context context;
	private DbHelper dbHelper;
	private LayoutInflater inflater;
	private List<Interaction> interactions;

	public DevicesAdapter(Context context) {
		this.context = context;
		dbHelper = new DbHelper(context);
		interactions = dbHelper.getAllInteractions();
	}

	@Override
	public int getCount() {
		return dbHelper.getAllInteractions().size();
	}

	@Override
	public Object getItem(int position) {
		return interactions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return interactions.indexOf(interactions.get(position));
	}

	@Override
	public void notifyDataSetChanged() {
		interactions = dbHelper.getAllInteractions();
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView txtDeviceName;
		TextView txtDeviceMac;
		TextView txtCreatedAt;
		ImageView deviceIcon;
		View v = convertView;
		if (v == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.device_view, null);
		}
		
		// Locate the TextViews
		txtDeviceName = (TextView) v.findViewById(R.id.deviceNameTxt);
		txtDeviceMac = (TextView) v.findViewById(R.id.macAddressTxt);
		txtCreatedAt = (TextView) v.findViewById(R.id.created_atTxt);
		deviceIcon = (ImageView) v.findViewById(R.id.device_icon);
		Log.e("SADJKAS",interactions.get(position).getAction().toString());
		if(!interactions.get(position).getAction()){
			deviceIcon.setImageResource(R.drawable.ic_action_cancel);
		}else{
			deviceIcon.setImageResource(R.drawable.ic_launcher);
		}
		txtDeviceName.setText(interactions.get(position).getName());
		txtDeviceMac.setText(interactions.get(position).getAdress());
		txtCreatedAt.setText(interactions.get(position).getDate(this.context));
		return v;
	}

}
