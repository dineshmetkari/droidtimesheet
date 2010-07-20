package br.com.webevolution.android.hoursbank;

import br.com.webevolution.android.hoursbank.db.DatabaseHelper;
import br.com.webevolution.android.hoursbank.setup.SetupActivity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class BlotterActivity extends ListActivity {
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
		Cursor cursor = db.getAllCheckpoints();
		startManagingCursor(cursor);

		CheckpointsView chk = new CheckpointsView(this);
		CheckPointListAdapter adapter = new CheckPointListAdapter(this, CheckPointListAdapter.BLOTTER, cursor);
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
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
			case HoursBank.MENU_SETTINGS:
				Intent intent = new Intent(this, SetupActivity.class);
				startActivity(intent);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
