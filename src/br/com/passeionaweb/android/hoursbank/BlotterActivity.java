package br.com.passeionaweb.android.hoursbank;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import br.com.passeionaweb.android.hoursbank.db.BackupHelper;
import br.com.passeionaweb.android.hoursbank.db.CheckpointsDatabaseHelper;

public class BlotterActivity extends CheckpointListActivity {

	private Dialog addDialog = null;
	private Dialog editDialog = null;
	private static final int MENU_BACKUP = 20;
	private static final int MENU_RESTORE = 21;
	private static final int MENU_ERASE = 22;
	private static final int DIALOG_ERASE_DATA = 20;

	protected void fillData() {

		db.open();
		// Get all of the rows from the database and create the item list
		Cursor cursor = db.getAllCheckpoints(false);
		startManagingCursor(cursor);

		CheckpointsView chk = new CheckpointsView(this);
		CheckpointCursorAdapter adapter = new CheckpointCursorAdapter(this,
				CheckpointCursorAdapter.BLOTTER, cursor);
		setListAdapter(adapter);

		findViewById(R.id.layoutContainer).setVisibility(View.VISIBLE);

		long sum = chk.calculateTotalHours(cursor);
		String totalHours = chk.formatTotalHours(sum);

		((TextView) findViewById(R.id.lblTotalHours)).setText(totalHours);
		stopManagingCursor(cursor);
		db.close();
	}

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
				((TimePicker) dialog.findViewById(R.id.tpAddCheckpoint)).setCurrentHour(c
						.get(Calendar.HOUR_OF_DAY));
				((TimePicker) dialog.findViewById(R.id.tpAddCheckpoint)).setCurrentMinute(c
						.get(Calendar.MINUTE));
				((DatePicker) dialog.findViewById(R.id.dtAddCheckpoint)).updateDate(c
						.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				break;
			case DIALOG_ADD:
				((TimePicker) dialog.findViewById(R.id.tpAddCheckpoint)).setCurrentHour(c
						.get(Calendar.HOUR_OF_DAY));
				((TimePicker) dialog.findViewById(R.id.tpAddCheckpoint)).setCurrentMinute(c
						.get(Calendar.MINUTE));
				((DatePicker) dialog.findViewById(R.id.dtAddCheckpoint)).updateDate(c
						.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				break;
		}
	}

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

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		beginEdit(id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ADD, Menu.NONE, R.string.menu_add_checkpoint).setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, MENU_BACKUP, Menu.NONE, R.string.menu_backup).setIcon(
				android.R.drawable.ic_menu_save);
		menu.add(Menu.NONE, MENU_RESTORE, Menu.NONE, R.string.menu_restore).setIcon(
				android.R.drawable.ic_menu_revert);
		menu.add(Menu.NONE, MENU_ERASE, Menu.NONE, R.string.menu_erase).setIcon(
				android.R.drawable.ic_menu_delete);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		String message;
		switch (item.getItemId()) {
			case MENU_BACKUP:
				BackupHelper bkpAgent = new BackupHelper(getApplicationContext());
				boolean bkpDone = bkpAgent.backupData();
				if (bkpDone) {
					message = getString(R.string.message_backup_done)
							+ Environment.getExternalStorageDirectory() + "\\"
							+ getString(R.string.app_name);

				} else {
					message = getString(R.string.message_backup_error);
				}
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
				break;
			case MENU_RESTORE:
				if (new BackupHelper(getApplicationContext()).restoreData()) {
					message = getString(R.string.message_restore_done);
				} else {
					message = getString(R.string.message_restore_error);
				}
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
				fillData();
				break;
			case MENU_ERASE:
				showDialog(DIALOG_ERASE_DATA);
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_ADD:
				return createAddDialog();
			case DIALOG_EDIT:
				return createEditDialog();
			case DIALOG_ERASE_DATA:
				return new AlertDialog.Builder(this).setCancelable(false).setIcon(
						android.R.drawable.ic_dialog_alert).setTitle(R.string.menu_erase)
						.setMessage(R.string.dialog_erase_data).setNegativeButton(
								getString(R.string.no), new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
									}
								}).setPositiveButton(getString(R.string.yes),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										CheckpointsDatabaseHelper db = new CheckpointsDatabaseHelper(getBaseContext());
										db.open();
										if (db.eraseDatabase() > 0) {
											Toast.makeText(getBaseContext(),
													getString(R.string.message_erase_done),
													Toast.LENGTH_LONG).show();
										} else {
											Toast.makeText(getBaseContext(),
													getString(R.string.message_erase_error),
													Toast.LENGTH_LONG).show();
										}
										db.close();
										fillData();
									}
								}).create();
			default:
				return super.onCreateDialog(id);

		}
	}
}
