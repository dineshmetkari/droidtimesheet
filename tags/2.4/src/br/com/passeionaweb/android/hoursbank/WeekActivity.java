package br.com.passeionaweb.android.hoursbank;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class WeekActivity extends CheckpointListActivity {
	private int week;
	public static final int MENU_SET_WEEK = 20;
	public static final int DIALOG_SET_WEEK = 10;

	protected void fillData() {

		db.open();
		// Get all of the rows from the database and create the item list
		Cursor cursor = db.getWeekCheckpoints(this, week);
		startManagingCursor(cursor);

		CheckpointsView chk = new CheckpointsView(this);

		String[] from = new String[] { CheckpointsView.KEY_DAY, CheckpointsView.KEY_TOTAL,
				CheckpointsView.KEY_BALANCE, CheckpointsView.KEY_IMAGE };
		int[] to = new int[] { R.id.txtCheckPoint, R.id.txtTotalHours, R.id.txtHourBalanceRow,
				R.id.imgCheckpointInOut };
		if (cursor.getCount() > 0) {
			List<HashMap<String, String>> list = chk.cursorToList(cursor, CheckpointsView.WEEK,
					week);
			SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.checkpoint_row, from, to);
			setListAdapter(adapter);
			findViewById(R.id.layoutContainer).setVisibility(View.VISIBLE);
		} else {
			setListAdapter(null);
		}
		long sum = chk.calculateTotalHours(cursor);
		String totalHours = chk.formatTotalHours(sum);
		((TextView) findViewById(R.id.lblTotalHours)).setText(totalHours);
		findViewById(R.id.layoutBalance).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.lblHoursBalance)).setText(chk.formatTotalHours(chk
				.getBalance()));
		if (chk.getBalance() >= 0) {
			((ImageView) findViewById(R.id.imgHoursBalance))
					.setImageResource(R.drawable.ic_btn_round_plus);
		} else {
			((ImageView) findViewById(R.id.imgHoursBalance))
					.setImageResource(R.drawable.ic_btn_round_minus);
		}
		db.close();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO implement this method to edit and delete all checkpoints related
		// to this day
		// DO NOT REMOVE THIS METHOD
		// This method is necessary to avoid the superclass method of creating
		// this context menu
		// DO NOT REMOVE THIS METHOD
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getApplicationContext(), DayActivity.class);

		String strDay = (String) ((TextView) v.findViewById(R.id.txtCheckPoint)).getText();
		Calendar day = Calendar.getInstance();
		day.set(Calendar.DAY_OF_WEEK, Integer.valueOf(strDay.split(" ")[1].split("/")[0]));
		day.set(Calendar.WEEK_OF_YEAR, Integer.valueOf(strDay.split(" ")[1].split("/")[1]) - 1);

		intent.putExtra("DAY", day.getTimeInMillis());
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * menu.add(Menu.NONE, MENU_SET_WEEK, Menu.NONE,
		 * R.string.menu_set_week).setIcon( android.R.drawable.ic_menu_week);
		 */
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		/*
		 * switch (item.getItemId()) { case MENU_SET_WEEK:
		 * showDialog(DIALOG_SET_WEEK); break; }
		 */
		return super.onMenuItemSelected(featureId, item);
	}

	private void setWeek(int week) {
		this.week = week;
		fillData();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
		super.onCreate(savedInstanceState);
	}
}
