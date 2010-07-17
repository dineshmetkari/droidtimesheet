package br.com.webevolution.android.hoursbank;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import br.com.webevolution.android.hoursbank.db.DatabaseHelper;

public class DayActivity extends ListActivity {
	private DatabaseHelper db;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DatabaseHelper(this);
		setContentView(R.layout.checkpoint_list);
		fillData();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fillData();

	}

	private void fillData() {

		db.open();
		// Get all of the rows from the database and create the item list
		Cursor cursor = db.getTodayCheckpoints();
		startManagingCursor(cursor);

		CheckPointListAdapter adapter = new CheckPointListAdapter(this, R.layout.checkpoint_row, cursor);
		setListAdapter(adapter);

		
		
		if (cursor.getCount() > 0) {
			findViewById(R.id.layoutContainer).setVisibility(View.VISIBLE);
			long totalHours = 0;
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				long checkpoint = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_CHECKPOINT));
				long now = Calendar.getInstance().getTimeInMillis();
				if ((cursor.getPosition() + 1) % 2 == 0) {
					cursor.moveToPrevious();
					long lastCheckpoint = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_CHECKPOINT));
					totalHours += checkpoint - lastCheckpoint;
					cursor.moveToNext();
				} else {
					if (cursor.isLast() && cursor.isFirst()) {
						// this case today has only one checkpoint
						totalHours = now - checkpoint;
					} else if (cursor.isLast()) {
						totalHours += now - checkpoint;
					}
				}
				cursor.moveToNext();
			}
			
			long timeInSeconds = totalHours /1000;
			long hours = timeInSeconds / 3600;
			long minutes = (timeInSeconds / 60) - (hours * 60);

			
			//TODO get from preferences the amount of hours to do for each day
			// and calculate the time to get out to set in the label!
						
			((TextView) findViewById(R.id.lblTotalHours)).setText(hours+":"+minutes);

		}

		db.close();
	}
}