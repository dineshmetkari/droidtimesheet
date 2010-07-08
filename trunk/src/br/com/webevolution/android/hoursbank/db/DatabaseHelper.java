package br.com.webevolution.android.hoursbank.db;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "droidtimesheetdata";
	private static final String TABLE_NAME = "CheckPoints";
	public static final String KEY_ID = "_id";
	public static final String KEY_CHECKPOINT = "checkpoint";
	private static final String CREATE_SQL = "create table " + TABLE_NAME
			+ " (" + KEY_ID + " integer primary key autoincrement ,"
			+ KEY_CHECKPOINT + " datetime not null)";

	private SQLiteDatabase db;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SQL);
	}

	public void open() {
		db = getWritableDatabase();
	}

	public void close() {
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO create the code to upgrade database according with versions
	}

	public Cursor getTodayCheckpoints(){
		Calendar calendar = Calendar.getInstance();
		calendar.clear(Calendar.HOUR);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		return db.query(TABLE_NAME, new String[]{KEY_ID,KEY_CHECKPOINT},KEY_CHECKPOINT +" >= "+calendar.getTimeInMillis() , null, null, null, KEY_CHECKPOINT);
	}
}