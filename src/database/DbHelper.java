package database;

import java.util.ArrayList;
import java.util.List;

import com.workana.bluecare.Interaction;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateUtils;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper { //
	private static final String TAG = DbHelper.class.getSimpleName();

	public DbHelper(Context context) {
		super(context, ActionContract.DB_NAME, null, ActionContract.DB_VERSION);
		//
	}

	// Called only once first time we create the database
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String
				.format("create table %s (%s int primary key, %s text,%s text,%s int, %s int DEFAULT CURRENT_TIMESTAMP)",
						ActionContract.TABLE, ActionContract.Column.ID,
						ActionContract.Column.NAME, ActionContract.Column.MAC,
						ActionContract.Column.ACTION,
						ActionContract.Column.CREATED_AT);
		//
		Log.d(TAG, "onCreate with SQL: " + sql);
		db.execSQL(sql); //
	}

	public void registerConnection(BluetoothDevice device) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ActionContract.Column.NAME, device.getName()); // Contact
																	// Name
		values.put(ActionContract.Column.MAC, device.getAddress()); // Contact
																	// Phone
																	// Number
		values.put(ActionContract.Column.ACTION, 1);
		values.put(ActionContract.Column.CREATED_AT, System.currentTimeMillis());
		
		// Inserting Row
		db.insert(ActionContract.TABLE, null, values);
		db.close(); // Closing database connection
		Log.i(TAG, device.getAddress() + " Device added to the DB");
	}

	public void registerDisconnection(BluetoothDevice device) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(ActionContract.Column.NAME, device.getName()); // Contact
																	// Name
		values.put(ActionContract.Column.MAC, device.getAddress()); // Contact
																	// Phone
																	// Number
		values.put(ActionContract.Column.ACTION, 0);
		values.put(ActionContract.Column.CREATED_AT, System.currentTimeMillis());
		// Inserting Row
		db.insert(ActionContract.TABLE, null, values);
		db.close(); // Closing database connection
		Log.e(TAG, device.getAddress() + " Device added to the DB");
	}

	public List<Interaction> getAllInteractions() {
		List<Interaction> ineractionsList = new ArrayList<Interaction>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + ActionContract.TABLE;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Interaction interaction = new Interaction();
				interaction.setName(cursor.getString(1));
				interaction.setAdress(cursor.getString(2));
				interaction
						.setAction(Boolean.parseBoolean(cursor.getString(3)));
				interaction.setDate(cursor.getLong(4));
				// Adding contact to list
				ineractionsList.add(interaction);
			} while (cursor.moveToNext());
		}

		// return contact list
		return ineractionsList;
	}

	public Interaction getInteraction(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(ActionContract.TABLE,
				new String[] { ActionContract.Column.NAME,
						ActionContract.Column.MAC,
						ActionContract.Column.ACTION,
						ActionContract.Column.CREATED_AT },
				ActionContract.Column.ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			Log.e(TAG, cursor.toString());
		}
		Interaction interaction = new Interaction(cursor.getString(0),
				cursor.getString(1), Boolean.parseBoolean(cursor.getString(2)),
				cursor.getInt(3));

		return interaction;
	}

	// Gets called whenever existing version != new version, i.e. schema changed
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//
		// Typically you do ALTER TABLE ...
		db.execSQL("drop table if exists " + ActionContract.TABLE);
		onCreate(db);
	}
}