package br.com.webevolution.android.hoursbank;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import br.com.webevolution.android.hoursbank.db.DatabaseHelper;

public class Day extends ListActivity {
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
		Cursor cursor = db.getTodayCheckpoints();
		startManagingCursor(cursor);
		
		CheckPointListAdapter adapter = new CheckPointListAdapter(this, R.layout.checkpoint_row, cursor);
		setListAdapter(adapter);
		db.close();
		
		/*
		db.open();
		// Get all of the rows from the database and create the item list
		Cursor cursor = db.getTodayCheckpoints();
		startManagingCursor(cursor);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { DatabaseHelper.KEY_CHECKPOINT };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.txtCheckPoint };
		
		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter checkpoints = new SimpleCursorAdapter(this, R.layout.checkpoint_row, cursor, from, to);
		setListAdapter(checkpoints);
		db.close();*/
	}
}