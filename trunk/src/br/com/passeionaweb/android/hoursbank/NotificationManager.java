package br.com.passeionaweb.android.hoursbank;

import java.util.Calendar;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import br.com.passeionaweb.android.hoursbank.db.CheckpointsDatabaseHelper;

public class NotificationManager {

	private final android.app.NotificationManager manager;
	private final Context context;
	private final CheckpointsDatabaseHelper db;

	public NotificationManager(Context context) {
		this.context = context;
		this.manager = (android.app.NotificationManager) context
				.getSystemService(Service.NOTIFICATION_SERVICE);
		this.db = new CheckpointsDatabaseHelper(context);
	}

	public void scheduleEndDayNotification() {
		db.open();
		if (PreferencesActivity.getNotificationEndDay(context)
				&& db.getStatus().equals(db.STATUS_IN)) {

			long when = getWhenEndDay();

			Notification notification = new Notification();

		}
		db.close();
	}

	private long getWhenEndDay() {
		long result = 0;
		long now = 0;
		long hoursDone = 0;
		long minHours = 0;
		Calendar day = Calendar.getInstance();
		now = day.getTimeInMillis();
		CheckpointsView chk = new CheckpointsView(context);
		minHours = chk.calculateTotalHours(db.getCheckpointsByDay(day));

		return result;
	}

}
