package br.com.passeionaweb.android.hoursbank;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import br.com.passeionaweb.android.hoursbank.db.DatabaseHelper;

public abstract class CheckpointListActivity extends ListActivity {
	protected static final int MENU_ADD = 10;
	protected static final int MENU_EDIT = 11;
	protected static final int MENU_DELETE = 12;
	protected static final int DIALOG_ADD = 1;
	protected static final int DIALOG_EDIT = 2;
	protected static final int TOAST_ADDED = 1;
	protected static final int TOAST_DELETED = 2;
	protected static final int TOAST_EDITED = 3;
	protected static final int TOAST_EXPORT_ERROR = 4;
	protected DatabaseHelper db;
	protected CheckpointsView chk;
	protected long editId;

	protected abstract void fillData();

	protected abstract Dialog createEditDialog();

	protected abstract Dialog createAddDialog();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DatabaseHelper(this);
		setContentView(R.layout.checkpoint_list);
		chk = new CheckpointsView(this);
		fillData();
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(R.string.context_menu_title);
		menu.setHeaderIcon(R.drawable.ic_dialog_time);
		menu.add(0, MENU_EDIT, 0, R.string.menu_edit);
		menu.add(0, MENU_DELETE, 0, R.string.menu_delete);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	protected void onResume() {
		fillData();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ADD, Menu.NONE, R.string.menu_add_checkpoint).setIcon(
				android.R.drawable.ic_menu_add);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
			case HoursBank.MENU_SETTINGS:
				Intent intent = new Intent(this, PreferencesActivity.class);
				startActivity(intent);
				break;
			case MENU_ADD:
				showDialog(DIALOG_ADD);
				break;
			case HoursBank.MENU_EXPORT:
				export();
				break;
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
		}
		return null;
	}

	protected void showToastMessage(int id) {
		switch (id) {
			case TOAST_ADDED:
				Toast.makeText(this, R.string.message_checkpoint_created, Toast.LENGTH_SHORT)
						.show();
				break;
			case TOAST_DELETED:
				Toast.makeText(this, R.string.message_checkpoint_deleted, Toast.LENGTH_SHORT)
						.show();
				break;
			case TOAST_EDITED:
				Toast.makeText(this, R.string.message_checkpoint_edited, Toast.LENGTH_SHORT).show();
				break;
			case TOAST_EXPORT_ERROR:
				Toast.makeText(this, R.string.export_error, Toast.LENGTH_SHORT).show();
				break;
		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case MENU_EDIT:
				editId = info.id;
				showDialog(DIALOG_EDIT);
				fillData();
				break;
			case MENU_DELETE:
				deleteCheckpoint(info.id);
				fillData();
				break;
		}
		return super.onContextItemSelected(item);
	}

	protected void deleteCheckpoint(long id) {
		db.open();
		db.deleteCheckpoint(id);
		showToastMessage(TOAST_DELETED);
		db.close();
	}

	protected void insertCheckpoint() {
		Calendar cal = Calendar.getInstance();
		insertCheckpoint(cal.getTimeInMillis());
	}

	protected void insertCheckpoint(long timeInMillis) {
		db.open();
		db.insertCheckpoint(timeInMillis);
		db.close();
		showToastMessage(TOAST_ADDED);
		fillData();
	}

	protected void editCheckpoint(long checkpoint) {
		db.open();
		db.editCheckpoint(editId, checkpoint);
		db.close();
		showToastMessage(TOAST_EDITED);
		fillData();
	}

	protected void export() {
		db.open();
		try {
			File csv = chk.generateCSV(db.getAllCheckpoints());
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/csv");
			String path = csv.getAbsolutePath();
			i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.export_email_subject));
			i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+path));
			startActivity(Intent.createChooser(i, "test"));
			
		} catch (IOException e) {
			showToastMessage(TOAST_EXPORT_ERROR);
			e.printStackTrace();
		}
		db.close();
	}
}
