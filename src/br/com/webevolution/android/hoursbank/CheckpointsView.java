package br.com.webevolution.android.hoursbank;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import br.com.webevolution.android.hoursbank.db.DatabaseHelper;

public class CheckpointsView {

	private Context context;

	public static final int MONTH = 2;
	public static final int ALL = -1;
	public static final String KEY_DAY = "DAY";
	public static final String KEY_TOTAL = "TOTAL";
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
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		switch (type) {
			case MONTH:
				cursor.moveToFirst();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_CHECKPOINT)));
				ArrayList<Integer> days = getDaysByMonth(cal.get(Calendar.MONTH));
				ArrayList<Long> listCheckpoints = new ArrayList<Long>();
				for (int day : days) {
					while (!cursor.isAfterLast()) {
						cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_CHECKPOINT)));
						if (cal.get(Calendar.DAY_OF_MONTH) == day) {
							listCheckpoints.add(cal.getTimeInMillis());
						}else {
							map = new HashMap<String, String>();
							map.put(KEY_DAY,String.valueOf(day));
							long totalHours = calculateTotalHours(listCheckpoints);
							String formated =  formatTotalHours(totalHours);
							map.put(KEY_TOTAL ,formated);
							list.add(map);
							listCheckpoints = new ArrayList<Long>();
							break;
						}
						cursor.moveToNext();
					}
					

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

}
