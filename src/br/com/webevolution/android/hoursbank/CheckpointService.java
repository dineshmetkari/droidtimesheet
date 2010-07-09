package br.com.webevolution.android.hoursbank;

import java.util.Date;

import android.app.Service;
import android.appwidget.AppWidgetManager;
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
