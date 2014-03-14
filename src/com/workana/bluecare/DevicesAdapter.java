package com.workana.bluecare;

import java.util.List;

import database.DbHelper;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
		
	
		txtDeviceName.setText(interactions.get(position).getName());
		txtDeviceMac.setText(interactions.get(position).getAdress());
		txtCreatedAt.setText(interactions.get(position).getDate(this.context));
		return v;
	}

}
