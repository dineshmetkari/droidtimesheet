package br.com.webevolution.android.hoursbank;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import br.com.webevolution.android.hoursbank.db.DatabaseHelper;

public class CheckpointsView {

	private Context context;

	public static final int DAILY = 1;
	public static final int MONTHLY = 2;
	public static final int ALL = -1;

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
		long minutes = (hours * 60) +  Long.valueOf(totalHours.split(":")[1]);	
		long timeInSeconds = minutes * 60;
		long timestamp = timeInSeconds * 1000;
		return timestamp;
	}
	
		
}
