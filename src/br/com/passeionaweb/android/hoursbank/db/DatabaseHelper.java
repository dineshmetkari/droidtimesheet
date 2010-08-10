package br.com.passeionaweb.android.hoursbank.db;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.com.passeionaweb.android.hoursbank.PreferencesActivity;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "droidtimesheetdata";
	private static final String TABLE_NAME = "CheckPoints";
	public static final String KEY_ID = "_id";
	public static final String KEY_CHECKPOINT = "checkpoint";
	private static final String CREATE_SQL = "create table if not exists " + TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement ," + KEY_CHECKPOINT + " datetime not null)";
	public static final String STATUS_IN = "STATUS_IN";
	public static final String STATUS_OUT = "STATUS_OUT";

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
		Calendar endDate = (Calendar) day.clone();

		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);

		endDate.set(Calendar.HOUR_OF_DAY, 23);
		endDate.set(Calendar.MINUTE, 59);
		endDate.set(Calendar.SECOND, 59);

		return db.query(TABLE_NAME, new String[] { KEY_ID, KEY_CHECKPOINT }, KEY_CHECKPOINT + " >= " + startDate.getTimeInMillis() + " AND " + KEY_CHECKPOINT + " <= " + endDate.getTimeInMillis(),
				null, null, null, KEY_CHECKPOINT );

	}

	public Cursor getMonthCheckpoints(Context context) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, PreferencesActivity.getFirstDayOfMonth(context));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return db.query(TABLE_NAME, new String[] { KEY_ID, KEY_CHECKPOINT }, KEY_CHECKPOINT + " >= " + calendar.getTimeInMillis(), null, null, null, KEY_CHECKPOINT);

	}

	public Cursor getAllCheckpoints() {
		return db.query(TABLE_NAME, new String[] { KEY_ID, KEY_CHECKPOINT }, null, null, null, null, KEY_CHECKPOINT + " DESC");
	}

	public long insertCheckpoint() {
		return insertCheckpoint(Calendar.getInstance().getTimeInMillis());
	}

	public long insertCheckpoint(long checkpoint) {
		ContentValues values = new ContentValues();
		values.put(KEY_CHECKPOINT, checkpoint);
		long result = db.insert(TABLE_NAME, null, values);
		if (result != -1) {
			return checkpoint;
		} else {
			return result;
		}
	}

	public int deleteCheckpoint(long id) {
		return db.delete(TABLE_NAME, KEY_ID + " = " + id, null);
	}

	public int editCheckpoint(long id, long timestamp) {
		ContentValues values = new ContentValues();
		values.put(KEY_CHECKPOINT, timestamp);
		return db.update(TABLE_NAME, values, KEY_ID + " = " + id, null);
	}

	public int updateCheckpoint(long id, long checkpoint) {
		ContentValues values = new ContentValues();
		values.put(KEY_CHECKPOINT, checkpoint);
		return db.update(TABLE_NAME, values, KEY_ID + " = " + id, null);
	}

	public String getStatus() {
		int todayCount = getCheckpointsByDay(Calendar.getInstance()).getCount();
		if (todayCount % 2 == 0) {
			return STATUS_OUT;
		} else {
			return STATUS_IN;
		}
	}
}