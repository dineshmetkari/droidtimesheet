package br.com.webevolution.android.hoursbank.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import br.com.webevolution.android.hoursbank.CheckpointService;
import br.com.webevolution.android.hoursbank.R;

public class CheckpointWidget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;
		
		// Create an Intent to launch ExampleActivity
		Intent intent = new Intent(context, CheckpointService.class);
		
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
		
		// Get the layout for the App Widget and attach an on-click listener to
		// the button
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.checkpoint_widget);
				
		views.setOnClickPendingIntent(R.id.widgetImage, pendingIntent);

		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			// Tell the AppWidgetManager to perform an update on the current App
			// Widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		context.stopService(new Intent(context,CheckpointService.class));
        super.onDisabled(context);
		
	}

}
