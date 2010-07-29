package br.com.webevolution.android.hoursbank;

import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MonthActivity extends CheckpointListActivity {
	protected void fillData() {

		db.open();
		// Get all of the rows from the database and create the item list
		Cursor cursor = db.getMonthCheckpoints();
		startManagingCursor(cursor);

		CheckpointsView chk = new CheckpointsView(this);
		
		
		String[] from = new String[] {CheckpointsView.KEY_DAY,CheckpointsView.KEY_TOTAL};
		int[] to = new int[] {R.id.txtCheckPoint,R.id.txtTotalHours}; 
		List<HashMap<String,String>> list = chk.cursorToList(cursor, CheckpointsView.MONTH);
		SimpleAdapter adapter = new SimpleAdapter(this, list , R.layout.checkpoint_row, from, to);
		
		//CheckpointCursorAdapter adapter = new CheckpointCursorAdapter(this, CheckpointCursorAdapter.MONTH, cursor);
		try {
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
		}catch(Exception e) {
			e.printStackTrace();
		}
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