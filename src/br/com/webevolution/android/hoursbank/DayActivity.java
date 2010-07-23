package br.com.webevolution.android.hoursbank;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import br.com.webevolution.android.hoursbank.db.DatabaseHelper;

public class DayActivity extends CheckpointListActivity {

	private long hoursDone;
	private long minHours;

	protected TimePickerDialog.OnTimeSetListener onEditCheckpointListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minute);
			db.open();
			db.editCheckpoint(editId, cal.getTimeInMillis());
			db.close();
			showToastMessage(TOAST_EDITED);
			fillData();

		}
	};

	private boolean areHoursByDayDone() {
		return hoursDone > minHours;
	}

	@Override
	protected Dialog createEditDialog() {
		Calendar c = Calendar.getInstance();
		TimePickerDialog editDialog = new TimePickerDialog(this, onEditCheckpointListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
		editDialog.setTitle(R.string.dialog_add_checkpoint_title);
		return editDialog;
	}

	protected void fillData() {

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
				((ImageView) findViewById(R.id.imgHoursDone)).setImageResource(R.drawable.ic_btn_round_plus);
			} else {
				((ImageView) findViewById(R.id.imgHoursDone)).setImageResource(R.drawable.ic_btn_round_minus);
			}
		}
		db.close();
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		minHours = chk.unformatTotalHours(getTodayHoursPref());
	}

	@Override
	protected void onResume() {
		super.onResume();
		minHours = chk.unformatTotalHours(getTodayHoursPref());
	}

	@Override
	protected Dialog createAddDialog() {
		Calendar c = Calendar.getInstance();
		TimePickerDialog addDialog = new TimePickerDialog(this, onAddCheckpointListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
		addDialog.setTitle(R.string.dialog_add_checkpoint_title);
		return addDialog;
	}

}