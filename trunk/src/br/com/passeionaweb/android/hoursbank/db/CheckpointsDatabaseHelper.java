package br.com.passeionaweb.android.hoursbank.db;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.com.passeionaweb.android.hoursbank.PreferencesActivity;

public class CheckpointsDatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "droidtimesheetdata";
	private static final String TABLE_NAME = "CheckPoints";
	public static final String KEY_ID = "_id";
	public static final String KEY_CHECKPOINT = "checkpoint";
	private static final String CREATE_SQL = "create table if not exists " + TABLE_NAME + " ("
			+ KEY_ID + " integer primary key autoincrement ," + KEY_CHECKPOINT
			+ " datetime not null)";
	public static final String STATUS_IN = "STATUS_IN";
	public static final String STATUS_OUT = "STATUS_OUT";

	private SQLiteDatabase db;

	public CheckpointsDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SQL);
	}

	public void open() {
		if (db == null || !db.isOpen()) {
			db = getWritableDatabase();
		}
	}

	public void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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

		return db.query(TABLE_NAME, new String[] { KEY_ID, KEY_CHECKPOINT }, KEY_CHECKPOINT
				+ " >= " + startDate.getTimeInMillis() + " AND " + KEY_CHECKPOINT + " <= "
				+ endDate.getTimeInMillis(), null, null, null, KEY_CHECKPOINT);

	}

	public long getCheckpointById(long id) {
		Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID, KEY_CHECKPOINT }, KEY_ID
				+ " = " + id, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getLong(cursor.getColumnIndex(KEY_CHECKPOINT));
		} else {
			return -1;
		}
	}

	public Cursor getMonthCheckpoints(Context context, int month) {

		int firstDay = Integer.valueOf(PreferencesActivity.getFirstDayOfMonth(context));
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		calendar.clear();
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.YEAR, year);

		if (calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)
				&& calendar.get(Calendar.DAY_OF_MONTH) < firstDay) {
			calendar.roll(Calendar.MONTH, false);
		}
		calendar.set(Calendar.DAY_OF_MONTH, firstDay);

		Calendar calLastDay;
		if (calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
			calLastDay = Calendar.getInstance();
			calLastDay.set(Calendar.HOUR_OF_DAY,0);
			calLastDay.clear(Calendar.MINUTE);
			calLastDay.clear(Calendar.SECOND);
		} else {
			calLastDay = (Calendar) calendar.clone();
			calLastDay.roll(Calendar.MONTH, true);
		}
		return getCheckpointsByCalendar(context, calendar, calLastDay);
	}

	public Cursor getCheckpointsByCalendar(Context context, Calendar calFrom, Calendar calTo) {
		return db.query(TABLE_NAME, new String[] { KEY_ID, KEY_CHECKPOINT }, KEY_CHECKPOINT
				+ " >= " + calFrom.getTimeInMillis() + " AND " + KEY_CHECKPOINT + " < "
				+ calTo.getTimeInMillis(), null, null, null, KEY_CHECKPOINT);
	}

	public Cursor getWeekCheckpoints(Context context, int week) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		calendar.clear();
		calendar.set(Calendar.WEEK_OF_YEAR, week);
		calendar.set(Calendar.YEAR, year);
		Calendar calLastDay = (Calendar) calendar.clone();
		calLastDay.roll(Calendar.WEEK_OF_YEAR, true);
		return getCheckpointsByCalendar(context, calendar, calLastDay);
	}

	public Cursor getAllCheckpoints(boolean asc) {
		String orderBy = " DESC";
		if (asc) {
			orderBy = "";
		}
		return db.query(TABLE_NAME, new String[] { KEY_ID, KEY_CHECKPOINT }, null, null, null,
				null, KEY_CHECKPOINT + orderBy);
	}

	public long removeSeconds(long checkpoint) {
		Calendar calCheckpoint = Calendar.getInstance();
		calCheckpoint.setTimeInMillis(checkpoint);
		calCheckpoint.set(Calendar.SECOND, 0);
		return calCheckpoint.getTimeInMillis();
	}

	public long insertCheckpoint(long checkpoint) {
		checkpoint = removeSeconds(checkpoint);
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

	public boolean existsCheckpoint(long checkpoint) {
		return db.query(TABLE_NAME, new String[] { KEY_CHECKPOINT },
				KEY_CHECKPOINT + " = " + checkpoint, null, null, null, null).getCount() > 0;
	}

	public void insertCheckpoints(Long[] checkpoints, boolean unique) {
		open();
		db.beginTransaction();
		try {
			for (long checkpoint : checkpoints) {
				if (!unique || !existsCheckpoint(checkpoint)) {
					insertCheckpoint(checkpoint);
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		close();
	}

	public int eraseDatabase() {
		return db.delete(TABLE_NAME, "1", null);
	}

	public boolean deleteCheckpoints(long start, long end) {
		return db.delete(TABLE_NAME, KEY_CHECKPOINT + " >= " + start + KEY_CHECKPOINT + " <= "
				+ end, null) > 0;
	}

}