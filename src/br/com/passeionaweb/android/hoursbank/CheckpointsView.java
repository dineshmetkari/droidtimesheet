package br.com.passeionaweb.android.hoursbank;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;
import br.com.passeionaweb.android.hoursbank.db.CheckpointsDatabaseHelper;
import br.com.passeionaweb.android.hoursbank.notification.NotificationManager;

public class CheckpointsView {

    private Context                  context;
    private long                     balance         = 0;
    private String                   csvPrefixName;
    private String                   csvHeader;
    private String                   csvExportFormat = "dd/MM/yyyy HH:mm:ss";

    public static final int          MONTH           = 2;
    public static final int          WEEK            = 3;
    public static final int          ALL             = -1;
    public static final String       KEY_DAY         = "DAY";
    public static final String       KEY_TOTAL       = "TOTAL";
    public static final String       KEY_BALANCE     = "BALANCE";
    public static final String       KEY_IMAGE       = "IMAGE";
    public CheckpointsDatabaseHelper db;

    public CheckpointsView(Context context) {
        this.context = context;
        this.db = new CheckpointsDatabaseHelper(context);
    }

    public long calculateTotalHours(Cursor cursor) {
        long totalHours = 0;
        long now = Calendar.getInstance().getTimeInMillis();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long checkpoint = cursor.getLong(cursor.getColumnIndex(CheckpointsDatabaseHelper.KEY_CHECKPOINT));
            if ((cursor.getPosition() + 1) % 2 == 0) {
                cursor.moveToPrevious();
                long lastCheckpoint = cursor.getLong(cursor.getColumnIndex(CheckpointsDatabaseHelper.KEY_CHECKPOINT));
                totalHours += checkpoint - lastCheckpoint;
                cursor.moveToNext();
            } else {
                if (cursor.isLast() && cursor.isFirst()) {
                    // this case has only one checkpoint
                    totalHours = now - checkpoint;
                } else if (cursor.isLast()) {
                    totalHours += now - checkpoint;
                }
            }
            cursor.moveToNext();
        }
        return totalHours;
    }

    public long calculateTotalHours(ArrayList<Long> listCheckpoints) {
        long totalHours = 0;
        long now = Calendar.getInstance().getTimeInMillis();
        for (int index = 0; index < listCheckpoints.size(); index++) {
            long checkpoint = listCheckpoints.get(index);
            if ((index + 1) % 2 == 0) {
                long lastCheckpoint = listCheckpoints.get(index - 1);
                totalHours += checkpoint - lastCheckpoint;
            } else {
                if (listCheckpoints.size() == 1) {
                    // this case has only one checkpoint
                    totalHours = now - checkpoint;
                } else if (index == listCheckpoints.size() - 1) {
                    totalHours += now - checkpoint;
                }
            }
        }
        return totalHours;
    }

    public String formatTotalHours(Calendar totalHours) {
        return totalHours.get(Calendar.HOUR_OF_DAY) + ":" + totalHours.get(Calendar.MINUTE);
    }

    public String formatTotalHours(long totalHours) {
        if (totalHours < 0) {
            totalHours *= -1;
        }
        long timeInSeconds = totalHours / 1000;
        long hours = timeInSeconds / 3600;
        long minutes = (timeInSeconds / 60) - (hours * 60);
        String sHours;
        if (hours < 10) {
            sHours = "0" + hours;
        } else {
            sHours = String.valueOf(hours);
        }
        String sMinutes;
        if (minutes < 10) {
            sMinutes = "0" + minutes;
        } else {
            sMinutes = String.valueOf(minutes);
        }
        return sHours + ":" + sMinutes;

    }

    public long unformatTotalHours(String totalHours) {
        try {
            long hours = Long.valueOf(totalHours.split(":")[0]);
            long minutes = (hours * 60) + Long.valueOf(totalHours.split(":")[1]);
            long timeInSeconds = minutes * 60;
            long timestamp = timeInSeconds * 1000;
            return timestamp;
        } catch (Exception E) {
            Intent intent = new Intent(context, PreferencesActivity.class);
            intent.putExtra("ERROR", true);
            context.startActivity(intent);
            return 0;
        }

    }

    public List<HashMap<String, String>> cursorToList(Cursor cursor, int type, int month) {
        HashMap<String, String> map = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        switch (type) {
            case MONTH:
            case WEEK:

                cursor.moveToFirst();
                boolean hasCheckpoints = false;
                Calendar checkpoint = Calendar.getInstance();
                checkpoint.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(CheckpointsDatabaseHelper.KEY_CHECKPOINT)));
                Calendar lastCheckpoint = (Calendar) checkpoint.clone();
                ArrayList<Long> listCheckpoints = new ArrayList<Long>();
                while (!cursor.isAfterLast() || hasCheckpoints) {
                    if (!cursor.isAfterLast()) {
                        checkpoint.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(CheckpointsDatabaseHelper.KEY_CHECKPOINT)));
                    }

                    if (!cursor.isAfterLast() && checkpoint.get(Calendar.DAY_OF_MONTH) == lastCheckpoint.get(Calendar.DAY_OF_MONTH)
                            && checkpoint.get(Calendar.MONTH) == lastCheckpoint.get(Calendar.MONTH)) {
                        listCheckpoints.add(checkpoint.getTimeInMillis());
                        lastCheckpoint = (Calendar) checkpoint.clone();
                        hasCheckpoints = true;
                    } else {// day has changed

                        long totalHours = calculateTotalHours(listCheckpoints);
                        String formated = formatTotalHours(totalHours);
                        map = new HashMap<String, String>();
                        map.put(KEY_DAY, new SimpleDateFormat("E dd/MM").format(new Date(lastCheckpoint.getTimeInMillis())));
                        map.put(KEY_TOTAL, formated);
                        long hoursBalance = 0;
                        long minHours = unformatTotalHours(PreferencesActivity.getHoursPrefByDay(context, lastCheckpoint.get(Calendar.DAY_OF_WEEK)));
                        hoursBalance = totalHours - minHours;
                        balance += hoursBalance;
                        map.put(KEY_BALANCE, formatTotalHours(hoursBalance));
                        // setting the image Res Id to bind with the view
                        int imgId = 0;
                        if (totalHours >= minHours) {
                            imgId = R.drawable.ic_btn_round_plus;
                        } else {
                            imgId = R.drawable.ic_btn_round_minus;
                        }
                        map.put(KEY_IMAGE, String.valueOf(imgId));
                        // add the created map to the collection
                        list.add(map);
                        listCheckpoints = new ArrayList<Long>();
                        lastCheckpoint = (Calendar) checkpoint.clone();
                        hasCheckpoints = false;
                    }

                    if (!cursor.isAfterLast() && hasCheckpoints) {
                        cursor.moveToNext();
                    }
                }
                break;
        }

        return list;
    }

    public ArrayList<Calendar> getDaysByMonth(int month) {
        Calendar minDay = Calendar.getInstance();
        Calendar maxDay = Calendar.getInstance();
        minDay.set(Calendar.MONTH, month);
        minDay.clear(Calendar.HOUR_OF_DAY);
        minDay.clear(Calendar.MINUTE);
        minDay.clear(Calendar.SECOND);
        int firstDayPref = Integer.valueOf(PreferencesActivity.getFirstDayOfMonth(context));
        if (minDay.get(Calendar.DAY_OF_MONTH) < firstDayPref) {
            minDay.roll(Calendar.MONTH, false);
            minDay.set(Calendar.DAY_OF_MONTH, firstDayPref);
            maxDay.set(Calendar.DAY_OF_MONTH, firstDayPref);
            maxDay.add(Calendar.DAY_OF_MONTH, -1);
        } else {
            minDay.set(Calendar.DAY_OF_MONTH, firstDayPref);
            minDay.set(Calendar.MONTH, month);
        }

        ArrayList<Calendar> list = new ArrayList<Calendar>();

        while (minDay.before(maxDay)) {
            list.add((Calendar) minDay.clone());
            minDay.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    static public int getImageResId(int count) {
        if (count % 2 == 0) {
            return R.drawable.ic_checkout;
        } else {
            return R.drawable.ic_checkin;
        }
    }

    public String getLunchPref() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PreferencesActivity.KEY_MIN_LUNCH, "1:00");
    }

    public long getBalance() {
        return balance;
    }

    public File generateCSV(Cursor cursor) throws IOException {
        File root = Environment.getExternalStorageDirectory();
        File exportDir = new File(root.getAbsolutePath() + "/" + context.getString(R.string.app_name));
        // Make sure the dir exists
        exportDir.mkdir();
        csvPrefixName = context.getString(R.string.app_name).replace(" ", "_");
        File gpxfile = new File(exportDir, csvPrefixName + ".csv");
        FileWriter writer = new FileWriter(gpxfile);

        csvHeader = context.getString(R.string.csv_header);
        writer.append(csvHeader + "\n");

        cursor.moveToFirst();
        long lastTimestamp = 0;
        SimpleDateFormat dfExcel = new SimpleDateFormat(csvExportFormat);
        while (!cursor.isAfterLast()) {
            long timestamp = cursor.getLong(cursor.getColumnIndex(CheckpointsDatabaseHelper.KEY_CHECKPOINT));
            String timestampFormatted = (dfExcel.format(new Date(timestamp)));
            writer.append(timestampFormatted);
            writer.append(",");
            if (cursor.getPosition() % 2 != 0) {
                writer.append(formatTotalHours(timestamp - lastTimestamp));
                writer.append(",\n");
            }
            lastTimestamp = timestamp;
            cursor.moveToNext();
        }
        writer.flush();
        writer.close();
        return gpxfile;
    }

    public long insertCheckpoint() {
        return insertCheckpoint(Calendar.getInstance().getTimeInMillis());
    }

    public long insertCheckpoint(long checkpoint) {
        long result;
        db.open();
        result = db.insertCheckpoint(checkpoint);
        db.close();
        NotificationManager manager = new NotificationManager(context);
        manager.scheduleEndDayNotification();
        return result;

    }

    public void deleteCheckpoint(long id) {
        db.open();
        db.deleteCheckpoint(id);
        db.close();
        NotificationManager manager = new NotificationManager(context);
        manager.scheduleEndDayNotification();
    }

    public void editCheckpoint(long editId, long checkpoint) {
        db.open();
        db.editCheckpoint(editId, checkpoint);
        db.close();
        NotificationManager manager = new NotificationManager(context);
        manager.scheduleEndDayNotification();
    }

    public long calculateTimeToGo() {
        return calculateTimeToGo(System.currentTimeMillis());
    }

    public long calculateTimeToGo(long day) {
        Calendar dayCal = Calendar.getInstance();
        dayCal.setTimeInMillis(day);
        long result = 0;
        long now = 0;
        long hoursDone = 0;
        long minHours = 0;
        now = System.currentTimeMillis();
        CheckpointsView chk = new CheckpointsView(context);
        db.open();
        hoursDone = chk.calculateTotalHours(db.getCheckpointsByDay(dayCal));
        db.close();
        minHours = chk.unformatTotalHours(PreferencesActivity.getHoursPrefByDay(context, dayCal.get(Calendar.DAY_OF_WEEK)));
        result = now + minHours - hoursDone;
        return result;
    }

}
