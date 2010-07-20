package br.com.webevolution.android.hoursbank.db;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
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
	private static final String CREATE_SQL = "create table if not exists " + TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement ," + KEY_CHECKPOINT + " datetime not null)";
	public static final String STATUS_IN = "STATUS_IN";
	public static final String STATUS_OUT = "STATUS_OUT";
	public static final String[] SETUP_VALUES = { "19/07/2010 13:07", "19/07/2010 12:07", "19/07/2010 09:16", "16/07/2010 20:00", "16/07/2010 13:18", "16/07/2010 12:18", "16/07/2010 09:41",
			"15/07/2010 21:00", "15/07/2010 14:35", "15/07/2010 13:35", "15/07/2010 09:51", "14/07/2010 21:00", "14/07/2010 15:12", "14/07/2010 14:12", "14/07/2010 09:38", "13/07/2010 21:27",
			"13/07/2010 14:14", "13/07/2010 13:14", "13/07/2010 09:24", "12/07/2010 20:45", "12/07/2010 12:56", "12/07/2010 11:56", "12/07/2010 09:52", "08/07/2010 21:40", "08/07/2010 13:04",
			"08/07/2010 12:04", "08/07/2010 07:56", "07/07/2010 21:32", "07/07/2010 17:28", "07/07/2010 16:59", "07/07/2010 12:51", "07/07/2010 11:40", "07/07/2010 08:28", "06/07/2010 11:23",
			"06/07/2010 09:31", "05/07/2010 18:13", "05/07/2010 13:43", "05/07/2010 12:03", "05/07/2010 08:28", "02/07/2010 20:40", "02/07/2010 12:54", "02/07/2010 10:36", "02/07/2010 09:08",
			"01/07/2010 19:56", "01/07/2010 13:49", "01/07/2010 11:48", "01/07/2010 09:05" };

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

	public Cursor getCheckpointsByDay(Calendar day) {
		Calendar startDate = (Calendar) day.clone();
		Calendar endDate = (Calendar)day.clone();
		
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		
		endDate.set(Calendar.HOUR_OF_DAY, 23);
		endDate.set(Calendar.MINUTE, 59);
		endDate.set(Calendar.SECOND, 59);
		
		return db.query(TABLE_NAME, new String[] { KEY_ID, KEY_CHECKPOINT }, KEY_CHECKPOINT + " >= " + startDate.getTimeInMillis() +" AND "+ KEY_CHECKPOINT +" <= "+endDate.getTimeInMillis(), null, null, null, KEY_CHECKPOINT);

	}

	public Cursor getMonthCheckpoints() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return db.query(TABLE_NAME, new String[] { KEY_ID, KEY_CHECKPOINT }, KEY_CHECKPOINT + " >= " + calendar.getTimeInMillis(), null, null, null, KEY_CHECKPOINT);

	}

	public Cursor getAllCheckpoints() {
		return db.query(TABLE_NAME, new String[] { KEY_ID, KEY_CHECKPOINT }, null, null, null, null, KEY_CHECKPOINT);
	}

	public long insertCheckpoint() {
		ContentValues values = new ContentValues();
		long checkpoint = Calendar.getInstance().getTimeInMillis();
		values.put(KEY_CHECKPOINT, checkpoint);
		long result = db.insert(TABLE_NAME, null, values);
		if (result != -1) {
			return checkpoint;
		} else {
			return result;
		}
	}

	public String getStatus() {
		int todayCount = getCheckpointsByDay(Calendar.getInstance()).getCount();
		if (todayCount % 2 == 0) {
			return STATUS_OUT;
		} else {
			return STATUS_IN;
		}
	}

	public void setupTest() {
		db.execSQL("delete from " + TABLE_NAME);
		
		for(String a : SETUP_VALUES) {
			int day = Integer.valueOf(a.split(" ")[0].split("/")[0]);
			int month = Integer.valueOf(a.split(" ")[0].split("/")[1]);
			int year = Integer.valueOf(a.split(" ")[0].split("/")[2]);
			int hour = Integer.valueOf(a.split(" ")[1].split(":")[0]);
			int minute = Integer.valueOf(a.split(" ")[1].split(":")[1]);
			Date dt = new Date(year, month, day, hour, minute);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH,month-1);
			cal.set(Calendar.DAY_OF_MONTH,day);
			cal.set(Calendar.HOUR_OF_DAY,hour);
			cal.set(Calendar.MINUTE,minute);
			String date = cal.getTime().toString();
			ContentValues values = new ContentValues();
			values.put(KEY_CHECKPOINT, cal.getTimeInMillis() );
			db.insert(TABLE_NAME, null, values);
		}
		
	}
	
}