package br.com.passeionaweb.android.hoursbank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import br.com.passeionaweb.android.hoursbank.db.CheckpointsDatabaseHelper;

public class NoteEditor extends Activity {

    public static final String        EXTRA_ID = "_id";

    private long                      id       = 0L;
    private CheckpointsDatabaseHelper db;
    private TextView                  txtNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);
        Intent intent = getIntent();
        db = new CheckpointsDatabaseHelper(getBaseContext());
        if (intent.hasExtra(EXTRA_ID)) {
            id = intent.getExtras().getLong(EXTRA_ID);
        } else {
            finish();
        }
        txtNote = (TextView) findViewById(R.id.note);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNote();
    }

    @Override
    protected void onPause() {
        db.open();
        db.updateNote(id, txtNote.getText().toString());
        db.close();
        super.onPause();
    }

    @Override
    protected void onStop() {
        db.open();
        db.updateNote(id, txtNote.getText().toString());
        db.close();
        super.onStop();
    }

    private void loadNote() {
        db.open();
        txtNote.setText(db.getNoteById(id));
        db.close();

    }
}