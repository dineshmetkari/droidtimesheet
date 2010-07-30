package br.com.passeionaweb.android.hoursbank;

import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MonthActivity extends CheckpointListActivity {
	protected void fillData() {

		db.open();
		// Get all of the rows from the database and create the item list
		Cursor cursor = db.getMonthCheckpoints();
		startManagingCursor(cursor);

		CheckpointsView chk = new CheckpointsView(this);

		String[] from = new String[] { CheckpointsView.KEY_DAY, CheckpointsView.KEY_TOTAL, CheckpointsView.KEY_BALANCE, CheckpointsView.KEY_IMAGE };
		int[] to = new int[] { R.id.txtCheckPoint, R.id.txtTotalHours, R.id.txtHourBalanceRow, R.id.imgCheckpointInOut };
		if (cursor.getCount() > 0) {
			List<HashMap<String, String>> list = chk.cursorToList(cursor, CheckpointsView.MONTH);
			SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.checkpoint_row, from, to);
			setListAdapter(adapter);
		}
		if (cursor.getCount() > 0) {
			findViewById(R.id.layoutContainer).setVisibility(View.VISIBLE);
			long sum = chk.calculateTotalHours(cursor);
			String totalHours = chk.formatTotalHours(sum);
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

	@Override
	protected long calculateMinHoursByPref() {

		// TODO
		return 0;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		//TODO implement this method to edit and delete all checkpoints related to this day
		//DO NOT REMOVE THIS METHOD 
		//This method is necessary to avoid the superclass method of creating this context menu
		//DO NOT REMOVE THIS METHOD
	}
	
	
	

}