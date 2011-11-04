package br.com.passeionaweb.android.hoursbank.widget;

import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;
import br.com.passeionaweb.android.hoursbank.CheckpointsView;
import br.com.passeionaweb.android.hoursbank.R;
import br.com.passeionaweb.android.hoursbank.db.CheckpointsDatabaseHelper;

public class CheckpointWidget extends AppWidgetProvider {
    public static final String ACTION_CLICK = "br.com.passeionaweb.android.hoursbank.widget.CLICK";
    static final int           RESULT_ERROR = -1;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateWidgets(context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ACTION_CLICK)) {
            createCheckpoint(context);
        }

    }

    private void createCheckpoint(Context context) {
        // Creating the checkpoint and notifying the user
        try {
            CheckpointsView chk = new CheckpointsView(context);
            showToastNotification(context, chk.insertCheckpoint());
            CheckpointWidget.updateWidgets(context);
        } catch (Exception e) {
            Toast.makeText(context, "Error creating checkpoint: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showToastNotification(Context context, long resultCode) {
        Toast toast;
        if (resultCode != RESULT_ERROR) {
            toast = Toast.makeText(context, context.getResources().getString(R.string.message_checkpoint_create_success) + "\n" + new Date(resultCode).toLocaleString(), Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(context, R.string.message_checkpoint_create_error, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    private static int getDrawableId(String status) {
        if (CheckpointsDatabaseHelper.STATUS_IN.equals(status)) {
            return R.drawable.red_clock;
        } else {
            return R.drawable.blue_clock;
        }
    }

    public static void updateWidgets(Context context) {
        // Create an Intent to launch the Action click
        Intent intent = new Intent(ACTION_CLICK);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Get the layout for the App Widget and attach an on-click listener to
        // the button
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.checkpoint_widget);

        views.setOnClickPendingIntent(R.id.widgetImage, pendingIntent);

        CheckpointsDatabaseHelper db = new CheckpointsDatabaseHelper(context);
        db.open();
        int clockId = getDrawableId(db.getStatus());
        views.setImageViewResource(R.id.widgetImage, clockId);
        db.close();
        // Perform this loop procedure for each App Widget that belongs to this
        // provider
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName provider = getWidgetComponent();
        appWidgetManager.updateAppWidget(provider, views);

    }
    
    public static ComponentName getWidgetComponent() {
        return new ComponentName("br.com.passeionaweb.android.hoursbank", "br.com.passeionaweb.android.hoursbank.widget.CheckpointWidget");
    }
}
