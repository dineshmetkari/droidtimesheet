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
	private Dialog editDialog = null;

	protected void fillData() {

		db.open();
		// Get all of the rows from the database and create the item list
		Cursor cursor = db.getAllCheckpoints(false);
		startManagingCursor(cursor);

		CheckpointsView chk = new CheckpointsView(this);
		CheckpointCursorAdapter adapter = new CheckpointCursorAdapter(this,
				CheckpointCursorAdapter.BLOTTER, cursor);
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
		// TODO 24 - 12hrs preference
		
		TimePicker tp = (TimePicker) addDialog.findViewById(R.id.tpAddCheckpoint);
		tp.setIs24HourView(true);
		((Button) addDialog.findViewById(R.id.btnDialogAddCheckpointOk))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int day = ((DatePicker) addDialog.findViewById(R.id.dtAddCheckpoint))
								.getDayOfMonth();
						int month = ((DatePicker) addDialog.findViewById(R.id.dtAddCheckpoint))
								.getMonth();
						int hour = ((TimePicker) addDialog.findViewById(R.id.tpAddCheckpoint))
								.getCurrentHour();
						int minute = ((TimePicker) addDialog.findViewById(R.id.tpAddCheckpoint))
								.getCurrentMinute();
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.DAY_OF_MONTH, day);
						cal.set(Calendar.MONTH, month);
						cal.set(Calendar.HOUR_OF_DAY, hour);
						cal.set(Calendar.MINUTE, minute);
						insertCheckpoint(cal.getTimeInMillis());
						addDialog.dismiss();
					}
				});
		((Button) addDialog.findViewById(R.id.btnDialogAddCheckpointCancel))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						addDialog.dismiss();
					}
				});

		return addDialog;

	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		Calendar c = Calendar.getInstance();
		
		switch (id) {
			case DIALOG_EDIT:
				db.open();
				long checkpoint = db.getCheckpointById(editId);
				db.close();
				c.setTimeInMillis(checkpoint);
				((TimePicker) dialog.findViewById(R.id.tpAddCheckpoint)).setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
				((TimePicker) dialog.findViewById(R.id.tpAddCheckpoint)).setCurrentMinute(c.get(Calendar.MINUTE));
				((DatePicker) dialog.findViewById(R.id.dtAddCheckpoint)).updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				break;
			case DIALOG_ADD:
				((TimePicker) dialog.findViewById(R.id.tpAddCheckpoint)).setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
				((TimePicker) dialog.findViewById(R.id.tpAddCheckpoint)).setCurrentMinute(c.get(Calendar.MINUTE));
				((DatePicker) dialog.findViewById(R.id.dtAddCheckpoint)).updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				break;
		}
	}
	
	@Override
	protected Dialog createEditDialog() {
		editDialog = new Dialog(this);
		editDialog.setTitle(R.string.dialog_add_checkpoint_title2);
		editDialog.setContentView(R.layout.dialog_new_checkpoint);
		// TODO 24 - 12hrs preference
		((TimePicker) editDialog.findViewById(R.id.tpAddCheckpoint)).setIs24HourView(true);
		((Button) editDialog.findViewById(R.id.btnDialogAddCheckpointOk))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int day = ((DatePicker) editDialog.findViewById(R.id.dtAddCheckpoint))
								.getDayOfMonth();
						int month = ((DatePicker) editDialog.findViewById(R.id.dtAddCheckpoint))
								.getMonth();
						int hour = ((TimePicker) editDialog.findViewById(R.id.tpAddCheckpoint))
								.getCurrentHour();
						int minute = ((TimePicker) editDialog.findViewById(R.id.tpAddCheckpoint))
								.getCurrentMinute();
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.DAY_OF_MONTH, day);
						cal.set(Calendar.MONTH, month);
						cal.set(Calendar.HOUR_OF_DAY, hour);
						cal.set(Calendar.MINUTE, minute);
						editCheckpoint(cal.getTimeInMillis());
						editDialog.dismiss();
					}
				});
		((Button) editDialog.findViewById(R.id.btnDialogAddCheckpointCancel))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						editDialog.dismiss();
					}
				});

		return editDialog;
	}

}
