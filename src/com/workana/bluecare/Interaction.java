package com.workana.bluecare;




import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

public class Interaction {

	private String name;
	private String adress;
	private Boolean action;
	private long created_at;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public Boolean getAction() {
		return action;
	}

	public void setAction(Boolean acction) {
		this.action = acction;
	}

	public String getDate(Context context) {
		Log.e("INTERACTION",String.valueOf(this.created_at));
		String string = (String) DateUtils.getRelativeDateTimeString(context,created_at, DateUtils.SECOND_IN_MILLIS,DateUtils.HOUR_IN_MILLIS,0);
		return string;
	}

	public void setDate(long created_at) {
		this.created_at = created_at;
	}

	public Interaction() {

	}

	public Interaction( String name, String address, Boolean action, long created_at) {
		Log.e("ESTE",String.valueOf(created_at));
		this.name = name;
		this.adress = address;
		this.action = action;
		this.created_at = created_at;
	}

}
