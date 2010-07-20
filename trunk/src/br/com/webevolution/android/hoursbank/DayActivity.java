package br.com.webevolution.android.hoursbank;

import java.util.Calendar;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import br.com.webevolution.android.hoursbank.db.DatabaseHelper;
import br.com.webevolution.android.hoursbank.setup.SetupActivity;

public class DayActivity extends ListActivity {
	private DatabaseHelper db;
	private static final int MENU_ADD = 2;
	private static final int DIALOG_ADD = 1;


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
		Cursor cursor = db.getCheckpointsByDay(Calendar.getInstance());
		startManagingCursor(cursor);

		CheckpointsView chk = new CheckpointsView(this);
		CheckPointListAdapter adapter = new CheckPointListAdapter(this, CheckPointListAdapter.DAY, cursor);
		setListAdapter(adapter);

		if (cursor.getCount() > 0) {
			findViewById(R.id.layoutContainer).setVisibility(View.VISIBLE);

			long sum = chk.calculateTotalHours(cursor);
			String totalHours = chk.formatTotalHours(sum);

			// TODO get from preferences the amount of hours to do for each day
			// and calculate the time to get out to set in the label!

			((TextView) findViewById(R.id.lblTotalHours)).setText(totalHours);

		}

		db.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ADD, Menu.NONE, R.string.menu_add_checkpoint)
		.setIcon(android.R.drawable.ic_menu_add);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch(item.getItemId()) {
			case HoursBank.MENU_SETTINGS:
				Intent intent = new Intent(this, SetupActivity.class);
				startActivity(intent);
				break;
			case MENU_ADD:
				showDialog(DIALOG_ADD);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_ADD:
				Calendar c = Calendar.getInstance();
				TimePickerDialog dialog = new TimePickerDialog(this, onAddCheckpointListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
				dialog.setTitle(R.string.dialog_add_checkpoint_title);
				return dialog;
		}
		return null;
	}
	
	private TimePickerDialog.OnTimeSetListener onAddCheckpointListener = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY,hourOfDay);
			cal.set(Calendar.MINUTE,minute);
			db.open();
			db.insertCheckpoint(cal.getTimeInMillis());
			db.close();
			fillData();
			
		}
	};
}