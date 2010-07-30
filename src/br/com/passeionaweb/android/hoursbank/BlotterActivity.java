package br.com.passeionaweb.android.hoursbank;

import java.util.Calendar;

import android.app.Dialog;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class BlotterActivity extends CheckpointListActivity {

	private Dialog addDialog = null;

	protected void fillData() {

		db.open();
		// Get all of the rows from the database and create the item list
		Cursor cursor = db.getAllCheckpoints();
		startManagingCursor(cursor);

		CheckpointsView chk = new CheckpointsView(this);
		CheckpointCursorAdapter adapter = new CheckpointCursorAdapter(this, CheckpointCursorAdapter.BLOTTER, cursor);
		setListAdapter(adapter);

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
		addDialog = new Dialog(this);
		addDialog.setTitle(R.string.dialog_add_checkpoint_title2);
		addDialog.setContentView(R.layout.dialog_new_checkpoint);
		((Button) addDialog.findViewById(R.id.btnDialogAddCheckpointOk)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int day = ((DatePicker) addDialog.findViewById(R.id.dtAddCheckpoint)).getDayOfMonth();
				int month = ((DatePicker) addDialog.findViewById(R.id.dtAddCheckpoint)).getMonth();
				int hour = ((TimePicker) addDialog.findViewById(R.id.tpAddCheckpoint)).getCurrentHour();
				int minute = ((TimePicker) addDialog.findViewById(R.id.tpAddCheckpoint)).getCurrentMinute();
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, day);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.set(Calendar.MINUTE, minute);
				insertCheckpoint(cal.getTimeInMillis());
				addDialog.dismiss();
			}
		});
		((Button) addDialog.findViewById(R.id.btnDialogAddCheckpointCancel)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addDialog.dismiss();
			}
		});

		return addDialog;

	}

	@Override
	protected Dialog createEditDialog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected long calculateMinHoursByPref() {
		// TODO Auto-generated method stub
		return 0;
	}
}
