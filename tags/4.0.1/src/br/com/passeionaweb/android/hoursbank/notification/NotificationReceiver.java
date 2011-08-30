package br.com.passeionaweb.android.hoursbank.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import br.com.passeionaweb.android.hoursbank.HoursBank;
import br.com.passeionaweb.android.hoursbank.R;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_NOTIFICATION_ENDDAY = "br.com.passeionaweb.android.hoursbank.notification.ENDDAY";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_NOTIFICATION_ENDDAY.equals(intent.getAction())) {
            Notification notification = new Notification(R.drawable.black_clock, context.getText(R.string.notification_title_time_to_go), System.currentTimeMillis());
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
            notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.ledARGB = 0xff00ff00;
            notification.ledOnMS = 300;
            notification.ledOffMS = 1000;
            notification.vibrate = new long[] { 0, 100, 200, 300 };
            Intent notificationIntent = new Intent(context, HoursBank.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            notification.setLatestEventInfo(context, context.getText(R.string.notification_title_time_to_go), context.getText(R.string.notification_time_to_go), contentIntent);
            android.app.NotificationManager manager = (android.app.NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
            manager.notify(NotificationManager.NOTIFICATION_ENDDAY_ID, notification);
        }
    }

}