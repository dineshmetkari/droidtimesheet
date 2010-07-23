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

public class MonthActivity extends CheckpointListActivity {
	protected void fillData() {

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
	protected Dialog createAddDialog() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected Dialog createEditDialog() {
		// TODO Auto-generated method stub
		return null;
	}
}