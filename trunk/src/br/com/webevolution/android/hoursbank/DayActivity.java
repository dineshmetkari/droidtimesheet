package br.com.webevolution.android.hoursbank;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import br.com.webevolution.android.hoursbank.db.DatabaseHelper;

public class DayActivity extends ListActivity {
	private DatabaseHelper db;
	private long hoursDone;
	private long minHours;
	private CheckpointsView chk;
	private static final int MENU_ADD = 2;
	private static final int DIALOG_ADD = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DatabaseHelper(this);
		setContentView(R.layout.checkpoint_list);
		chk = new CheckpointsView(this);
		minHours = chk.unformatTotalHours(getTodayHoursPref());
		fillData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		minHours = chk.unformatTotalHours(getTodayHoursPref());
		fillData();

	}

	private void fillData() {

		db.open();
		// Get all of the rows from the database and create the item list
		Cursor cursor = db.getCheckpointsByDay(Calendar.getInstance());
		startManagingCursor(cursor);

		CheckPointListAdapter adapter = new CheckPointListAdapter(this, CheckPointListAdapter.DAY, cursor);
		setListAdapter(adapter);

		if (cursor.getCount() > 0) {
			findViewById(R.id.layoutContainer).setVisibility(View.VISIBLE);
			hoursDone = chk.calculateTotalHours(cursor);
			String totalHours = chk.formatTotalHours(hoursDone);
			((TextView) findViewById(R.id.lblTotalHours)).setText(totalHours);
			if (areHoursByDayDone()) {
				((ImageView) findViewById(R.id.imgHoursDone)).setImageResource(android.R.drawable.presence_online);
			} else {
				((ImageView) findViewById(R.id.imgHoursDone)).setImageResource(android.R.drawable.ic_notification_overlay);
			}
			
			String balance = chk.formatTotalHours(getHoursBalance());
			
		}
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ADD, Menu.NONE, R.string.menu_add_checkpoint).setIcon(android.R.drawable.ic_menu_add);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
			case HoursBank.MENU_SETTINGS:
				Intent intent = new Intent(this, PreferencesActivity.class);
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
		switch (id) {
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
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minute);
			db.open();
			db.insertCheckpoint(cal.getTimeInMillis());
			db.close();
			fillData();

		}
	};

	private boolean areHoursByDayDone() {
		String minHours = getTodayHoursPref();
		return hoursDone > new CheckpointsView(this).unformatTotalHours(minHours);
	}

	private long getHoursBalance() {
		if (areHoursByDayDone()) {
			return hoursDone - minHours;
		} else {
			return minHours - hoursDone ;
		}
	}

	private String getTodayHoursPref() {
		Calendar cal = Calendar.getInstance();
		String defValue = "0:00";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				return prefs.getString(PreferencesActivity.KEY_HOURS_MONDAY, defValue);
			case Calendar.TUESDAY:
				return prefs.getString(PreferencesActivity.KEY_HOURS_TUESDAY, defValue);
			case Calendar.WEDNESDAY:
				return prefs.getString(PreferencesActivity.KEY_HOURS_WEDNESDAY, defValue);
			case Calendar.THURSDAY:
				return prefs.getString(PreferencesActivity.KEY_HOURS_THURSDAY, defValue);
			case Calendar.FRIDAY:
				return prefs.getString(PreferencesActivity.KEY_HOURS_FRIDAY, defValue);
			case Calendar.SATURDAY:
				return prefs.getString(PreferencesActivity.KEY_HOURS_SATURDAY, defValue);
		}
		return defValue;
	}

}