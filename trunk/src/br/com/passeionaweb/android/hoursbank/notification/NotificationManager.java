package br.com.passeionaweb.android.hoursbank.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import br.com.passeionaweb.android.hoursbank.CheckpointsView;
import br.com.passeionaweb.android.hoursbank.PreferencesActivity;
import br.com.passeionaweb.android.hoursbank.db.CheckpointsDatabaseHelper;

public class NotificationManager {

    private final Context                   context;
    private final CheckpointsDatabaseHelper db;
    public static final int                 NOTIFICATION_ENDDAY_ID = 0;

    public NotificationManager(Context context) {
        this.context = context;
        this.db = new CheckpointsDatabaseHelper(context);

    }

    public boolean isNotificationSystemEnabled() {
        return PreferencesActivity.isNotificationSystemEnabled(context);
    }

    public boolean isNotificationEndDayEnabled() {
        return isNotificationSystemEnabled() && PreferencesActivity.getNotificationEndDay(context);
    }

    public void scheduleEndDayNotification() {
        db.open();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarm = new Intent(NotificationReceiver.ACTION_NOTIFICATION_ENDDAY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm, 0);
        if (isNotificationEndDayEnabled() && db.getStatus().equals(CheckpointsDatabaseHelper.STATUS_IN)) {
            long when = new CheckpointsView(context).calculateTimeToGo();
            if (when >= 0) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
            } else {
                alarmManager.cancel(pendingIntent);
            }
        } else {
            alarmManager.cancel(pendingIntent);
        }
        db.close();
    }

}
