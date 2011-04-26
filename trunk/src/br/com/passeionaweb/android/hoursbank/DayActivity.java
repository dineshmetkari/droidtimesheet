package br.com.passeionaweb.android.hoursbank;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import br.com.passeionaweb.android.hoursbank.db.CheckpointsDatabaseHelper;

public class DayActivity extends CheckpointListActivity {

    private long                                 hoursDone;
    private long                                 minHours;
    private Calendar                             day;
    private boolean                              today                    = true;

    protected TimePickerDialog.OnTimeSetListener onEditCheckpointListener = new OnTimeSetListener() {

                                                                              @Override
                                                                              public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                                                  Calendar cal = (Calendar) day.clone();
                                                                                  cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                                                  cal.set(Calendar.MINUTE, minute);
                                                                                  editCheckpoint(cal.getTimeInMillis());
                                                                              }
                                                                          };

    private boolean areHoursByDayDone() {
        return hoursDone > minHours;
    }

    private long getBalance() {
        if (hoursDone > minHours) {
            return hoursDone - minHours;
        } else {
            return minHours - hoursDone;
        }
    }

    protected Dialog createEditDialog() {
        TimePickerDialog editDialog = new TimePickerDialog(this, onEditCheckpointListener, 0, 0, true);
        editDialog.setTitle(R.string.dialog_add_checkpoint_title);
        return editDialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        Calendar c = Calendar.getInstance();
        TimePickerDialog tmDialog = null;
        switch (id) {
            case DIALOG_EDIT:
                db.open();
                long checkpoint = db.getCheckpointById(editId);
                db.close();
                c.setTimeInMillis(checkpoint);
                tmDialog = (TimePickerDialog) dialog;
                tmDialog.updateTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                break;
            case DIALOG_ADD:
                tmDialog = (TimePickerDialog) dialog;
                tmDialog.updateTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                break;
        }
    }

    protected void fillData() {

        db.open();
        if (day == null || today) {
            day = Calendar.getInstance();
        }
        // Get all of the rows from the database and create the item list
        Cursor cursor = db.getCheckpointsByDay(day);
        startManagingCursor(cursor);

        CheckpointCursorAdapter adapter = new CheckpointCursorAdapter(this, CheckpointCursorAdapter.DAY, cursor);
        setListAdapter(adapter);

        findViewById(R.id.layoutContainer).setVisibility(View.VISIBLE);
        hoursDone = chk.calculateTotalHours(cursor);
        String totalHours = chk.formatTotalHours(hoursDone);
        ((TextView) findViewById(R.id.lblTotalHours)).setText(totalHours);

        if (areHoursByDayDone()) {
            ((ImageView) findViewById(R.id.imgHoursBalance)).setImageResource(R.drawable.ic_btn_round_plus);
        } else {
            ((ImageView) findViewById(R.id.imgHoursBalance)).setImageResource(R.drawable.ic_btn_round_minus);
        }
        findViewById(R.id.layoutBalance).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.lblHoursBalance)).setText(chk.formatTotalHours(getBalance()));
        stopManagingCursor(cursor);
        if (!areHoursByDayDone() && !getIntent().hasExtra("DAY") && db.getStatus().equals(CheckpointsDatabaseHelper.STATUS_IN)) {
            long timetogo = System.currentTimeMillis() + minHours - hoursDone;
            Calendar calTimetogo = Calendar.getInstance();
            calTimetogo.setTimeInMillis(timetogo);
            if (timetogo > 0) {
                ((LinearLayout) findViewById(R.id.layoutTimeToGo)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.lblTimeToGo)).setText(chk.formatTotalHours(calTimetogo));
            } else {
                ((LinearLayout) findViewById(R.id.layoutTimeToGo)).setVisibility(View.GONE);
            }
        } else {
            ((LinearLayout) findViewById(R.id.layoutTimeToGo)).setVisibility(View.GONE);
        }
        db.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkDay();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDay();
    }

    protected void checkDay() {
        if (getIntent().hasExtra("DAY")) {
            Calendar day = Calendar.getInstance();
            day.setTimeInMillis(getIntent().getExtras().getLong("DAY"));
            this.day = day;
            minHours = chk.unformatTotalHours(PreferencesActivity.getHoursPrefByDay(this, day.get(Calendar.DAY_OF_WEEK)));
            today = false;
        } else {
            today = true;
            minHours = chk.unformatTotalHours(PreferencesActivity.getHoursPrefByDay(this, Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ADD, Menu.NONE, R.string.menu_add_checkpoint).setIcon(android.R.drawable.ic_menu_add);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ADD:
                return createAddDialog();
            case DIALOG_EDIT:
                return createEditDialog();
        }
        return super.onCreateDialog(id);
    }

    protected Dialog createAddDialog() {
        Calendar c = Calendar.getInstance();
        TimePickerDialog addDialog = new TimePickerDialog(this, onAddCheckpointListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        addDialog.setTitle(R.string.dialog_add_checkpoint_title);
        return addDialog;
    }

    protected TimePickerDialog.OnTimeSetListener onAddCheckpointListener = new OnTimeSetListener() {

                                                                             @Override
                                                                             public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                                                 Calendar cal = (Calendar) day.clone();
                                                                                 cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                                                 cal.set(Calendar.MINUTE, minute);
                                                                                 insertCheckpoint(cal.getTimeInMillis());
                                                                             }
                                                                         };

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        beginEdit(id);
    }

}