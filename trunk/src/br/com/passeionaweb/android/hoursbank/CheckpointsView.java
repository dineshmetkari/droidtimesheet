package br.com.passeionaweb.android.hoursbank;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import br.com.passeionaweb.android.hoursbank.db.DatabaseHelper;

public class CheckpointsView {

	private Context context;

	public static final int MONTH = 2;
	public static final int ALL = -1;
	public static final String KEY_DAY = "DAY";
	public static final String KEY_TOTAL = "TOTAL";
	public static final String KEY_BALANCE = "BALANCE";
	public static final String KEY_IMAGE = "IMAGE";

	public CheckpointsView(Context context) {
		this.context = context;
	}

	public long calculateTotalHours(Cursor cursor) {
		long totalHours = 0;
		long now = Calendar.getInstance().getTimeInMillis();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			long checkpoint = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_CHECKPOINT));
			if ((cursor.getPosition() + 1) % 2 == 0) {
				cursor.moveToPrevious();
				long lastCheckpoint = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_CHECKPOINT));
				totalHours += checkpoint - lastCheckpoint;
				cursor.moveToNext();
			} else {
				if (cursor.isLast() && cursor.isFirst()) {
					// this case has only one checkpoint
					totalHours = now - checkpoint;
				} else if (cursor.isLast()) {
					totalHours += now - checkpoint;
				}
			}
			cursor.moveToNext();
		}
		return totalHours;
	}

	public long calculateTotalHours(ArrayList<Long> listCheckpoints) {
		long totalHours = 0;
		long now = Calendar.getInstance().getTimeInMillis();
		for (int index = 0; index < listCheckpoints.size(); index++) {
			long checkpoint = listCheckpoints.get(index);
			if ((index + 1) % 2 == 0) {
				long lastCheckpoint = listCheckpoints.get(index - 1);
				totalHours += checkpoint - lastCheckpoint;
			} else {
				if (listCheckpoints.size() == 1) {
					// this case has only one checkpoint
					totalHours = now - checkpoint;
				} else if (index == listCheckpoints.size() - 1) {
					totalHours += now - checkpoint;
				}
			}
		}
		return totalHours;
	}

	public String formatTotalHours(long totalHours) {
		long timeInSeconds = totalHours / 1000;
		long hours = timeInSeconds / 3600;
		long minutes = (timeInSeconds / 60) - (hours * 60);
		String sHours;
		if (hours < 10) {
			sHours = "0" + hours;
		} else {
			sHours = String.valueOf(hours);
		}
		String sMinutes;
		if (minutes < 10) {
			sMinutes = "0" + minutes;
		} else {
			sMinutes = String.valueOf(minutes);
		}
		return sHours + ":" + sMinutes;
	}

	public long unformatTotalHours(String totalHours) {
		long hours = Long.valueOf(totalHours.split(":")[0]);
		long minutes = (hours * 60) + Long.valueOf(totalHours.split(":")[1]);
		long timeInSeconds = minutes * 60;
		long timestamp = timeInSeconds * 1000;
		return timestamp;
	}

	public List<HashMap<String, String>> cursorToList(Cursor cursor, int type) {
		HashMap<String, String> map = null;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		switch (type) {
			case MONTH:
				cursor.moveToFirst();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_CHECKPOINT)));
				// get all this based on the month eg:(1 - 30)
				ArrayList<Integer> days = getDaysByMonth(cal.get(Calendar.MONTH));
				ArrayList<Long> listCheckpoints = new ArrayList<Long>();
				// interact over all days
				long lastCheckpoint = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_CHECKPOINT));
				for (int day : days) {
					// interact over all checkpoints of that month
					while (!cursor.isAfterLast()) {
						cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_CHECKPOINT)));
						// still the same day, so keep inserting in the
						// listCheckpoint
						if (cal.get(Calendar.DAY_OF_MONTH) == day) {
							listCheckpoints.add(cal.getTimeInMillis());
							lastCheckpoint = cal.getTimeInMillis();
						}else if(cal.get(Calendar.DAY_OF_MONTH) > day) {
							break;
						}
						cursor.moveToNext();
					}
					// now the day was changed, so what is in the
					// listCheckpoints are the checkpoints of a
					// particular day
					// create a hash map that represent the row
					// which includes the Day, the total Hours, balance, and an
					// Image to be binded to the view
					if (listCheckpoints.size() > 0) {
						map = new HashMap<String, String>();
						map.put(KEY_DAY, new SimpleDateFormat("E dd/MM").format(new Date(lastCheckpoint)));
						long totalHours = calculateTotalHours(listCheckpoints);
						String formated = formatTotalHours(totalHours);
						map.put(KEY_TOTAL, formated);
						// calculating the hours balance to put in the map
						cal.setTimeInMillis(lastCheckpoint);
						long hoursBalance = 0;
						long minHours = unformatTotalHours(getHoursPrefByDay(cal.get(Calendar.DAY_OF_WEEK)));
						if (totalHours > minHours) {
							hoursBalance = totalHours - minHours;
						} else {
							hoursBalance = minHours - totalHours;
						}
						map.put(KEY_BALANCE, formatTotalHours(hoursBalance));
						// setting the image Res Id to bind with the view
						map.put(KEY_IMAGE, String.valueOf(getImageResId(totalHours)));
						// add the created map to the collection
						list.add(map);
						// clear the listCheckpoints and stop the while
						// iteration
						listCheckpoints = new ArrayList<Long>();
					}
					cursor.moveToFirst();
				}

		}

		return list;
	}

	/*
	 * month is one of Calendar final fields.
	 */
	public ArrayList<Integer> getDaysByMonth(int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int day = 1; day <= maxDay; day++) {
			list.add(day);
		}
		return list;
	}

	static public int getImageResId(int count) {
		if (count % 2 == 0) {
			return R.drawable.ic_checkout;
		} else {
			return R.drawable.ic_checkin;
		}
	}

	public int getImageResId(long totalHours) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(totalHours);
		String hours = getHoursPrefByDay(cal.get(Calendar.DAY_OF_WEEK));
		if (totalHours >= unformatTotalHours(hours)) {
			return R.drawable.ic_btn_round_plus;
		} else {
			return R.drawable.ic_btn_round_minus;
		}
	}

	public String getHoursPrefByDay(int dayOfWeek) {
		String defValue = "0:00";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		switch (dayOfWeek) {
			case Calendar.MONDAY:
				return prefs.getString(PreferencesActivity.KEY_HOURS_MONDAY, defValue);
			case Calendar.TUESDAY:
				return prefs.getString(PreferencesActivity.KEY_HOURS_TUESDAY, defValue);
			case Calendar.WEDNESDAY:
				return prefs.getString(PreferencesActivity.KEY_HOURS_WEDNESDAY, defValue);
			case Calendar.THURSDAY:
				return prefs.getString(PreferencesActivity.KEY_HOURS_THURSDAY, defValue);
			case Calendar.FRIDAY:
				return prefs.getString(PreferencesActivity.KEY_HOURS_FRIDAY, defValue);
			case Calendar.SATURDAY:
				return prefs.getString(PreferencesActivity.KEY_HOURS_SATURDAY, defValue);
		}
		return defValue;
	}

	public String getLunchPref() {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(PreferencesActivity.KEY_MIN_LUNCH, "1:00");
	}
}
