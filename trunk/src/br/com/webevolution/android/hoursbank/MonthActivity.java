package br.com.webevolution.android.hoursbank;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import br.com.webevolution.android.hoursbank.db.DatabaseHelper;

public class MonthActivity extends ListActivity {
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
		super.onResume();
		fillData();

	}

	private void fillData() {

		db.open();
		// Get all of the rows from the database and create the item list
		Cursor cursor = db.getMonthCheckpoints();
		startManagingCursor(cursor);

		CheckpointsView chk = new CheckpointsView(this);
		CheckPointListAdapter adapter = new CheckPointListAdapter(this, CheckPointListAdapter.MONTH, cursor);
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
		switch(id) {
			case DIALOG_ADD:
				Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.dialog_new_checkpoint);
				dialog.setTitle(R.string.dialog_add_checkpoint_title2);
				//TODO get the preference 24/12h from user preferences
				((TimePicker)dialog.findViewById(R.id.tpAddCheckpoint)).setIs24HourView(true);
				return dialog;
		}
		return null;
	}
	
}