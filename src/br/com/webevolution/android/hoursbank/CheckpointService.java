package br.com.webevolution.android.hoursbank;

import java.util.Date;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;
import br.com.webevolution.android.hoursbank.db.DatabaseHelper;
import br.com.webevolution.android.hoursbank.widget.CheckpointWidget;

public class CheckpointService extends Service {
	private DatabaseHelper db;
	static final int RESULT_ERROR = -1;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		createCheckpoint();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	private void createCheckpoint() {
		// Creating the checkpoint and notifying the user
		db = new DatabaseHelper(this);
		db.open();
		showToastNotification(db.insertCheckpoint());
		
		//updating the widgets to display the correct drawnable element
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		int[] ids = manager.getAppWidgetIds(new ComponentName(this, CheckpointWidget.class));
		RemoteViews view = new RemoteViews(getPackageName(),R.layout.checkpoint_widget);
		int clockId = 0;
		String status = db.getStatus();
		//check the status and set the correct drawnable id
		if(DatabaseHelper.STATUS_IN.equals(status)) {
			clockId = R.drawable.red_clock;
		}else if(DatabaseHelper.STATUS_OUT.equals(status)) {
			clockId = R.drawable.blue_clock;
		}
		view.setImageViewResource(R.id.widgetImage, clockId);
		manager.updateAppWidget(ids, view);
		db.close();
	}

	private void showToastNotification(long resultCode) {
		Toast toast;
		if (resultCode != RESULT_ERROR) {
			toast = Toast.makeText(this, getResources().getString(R.string.message_checkpoint_create_success)+"\n"+ new Date(resultCode).toLocaleString() , Toast.LENGTH_SHORT);
		} else {
			toast = Toast.makeText(this, R.string.message_checkpoint_create_error, Toast.LENGTH_SHORT);
		}
		toast.show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
