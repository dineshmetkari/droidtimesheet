package br.com.webevolution.android.hoursbank;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import br.com.webevolution.android.hoursbank.db.DatabaseHelper;



public class Day extends ListActivity {
	private DatabaseHelper db;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(this);
        
        TextView text = new TextView(this);
        text.setText("some text DAY");
        setContentView(text);
    }
    
    private void fillData() {
        // Get all of the rows from the database and create the item list
    	Cursor cursor = db.getTodayCheckpoints();
        startManagingCursor(cursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{DatabaseHelper.KEY_CHECKPOINT};
        
        //TODO 
        /*
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
            new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        setListAdapter(notes);*/
    }
}