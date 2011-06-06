package br.com.passeionaweb.android.hoursbank.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import br.com.passeionaweb.android.hoursbank.R;

public class BackupHelper {

    private static final String FILENAME_PREFIX    = "backup_";
    private static final String FILENAME_EXTENSION = "bkp";

    private Context             context;

    public BackupHelper(Context context) {
        this.context = context;
    }

    private boolean backupData(Cursor cursor) {
        try {

            File exportDir = getBackupFolder();
            // Make sure the dir exists
            exportDir.mkdir();
            Calendar cal = Calendar.getInstance();
            String filePrefixName = FILENAME_PREFIX + new SimpleDateFormat("yyyyMMddHHmmss").format(cal.getTimeInMillis());
            File bkpFile = new File(exportDir, filePrefixName + "." + FILENAME_EXTENSION);
            writeCheckpointsToOutputStream(cursor, new FileOutputStream(bkpFile));

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void writeCheckpointsToOutputStream(Cursor cursor, OutputStream out) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long timestamp = cursor.getLong(cursor.getColumnIndex(CheckpointsDatabaseHelper.KEY_CHECKPOINT));
            writer.append(String.valueOf(timestamp));
            if (!cursor.isLast()) {
                writer.append("\n");
            }
            cursor.moveToNext();
        }
        writer.flush();
        writer.close();
    }

    public boolean backupData() {
        CheckpointsDatabaseHelper db = new CheckpointsDatabaseHelper(context);
        db.open();
        Cursor cursor = db.getAllCheckpoints(true);
        boolean result = backupData(cursor);
        db.close();
        return result;
    }

    public boolean restoreData() {
        try {
            File backupFile = getLastBackup(getBackupFiles(getBackupFolder().listFiles()));
            ArrayList<Long> checkpoints = new ArrayList<Long>();
            BufferedReader reader = new BufferedReader(new FileReader(backupFile));
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    checkpoints.add(Long.valueOf(line));
                } catch (NumberFormatException e) {
                    // Should be the last line of the file
                }
            }
            if (checkpoints.size() > 0) {
                new CheckpointsDatabaseHelper(context).insertCheckpoints(checkpoints.toArray(new Long[checkpoints.size()]), true);
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private File[] getBackupFiles(File[] files) {
        HashSet<File> backups = new HashSet<File>();
        for (File file : files) {
            if (file.getName().startsWith(FILENAME_PREFIX)) {
                backups.add(file);
            }
        }

        if (backups.size() == 0) {
            return null;
        } else {
            return backups.toArray(new File[backups.size()]);
        }
    }

    private File getLastBackup(File[] backups) {
        long timestamp;
        long greaterTimestamp = 0;
        File lastBackup = null;
        for (File file : backups) {
            timestamp = Long.valueOf(file.getName().substring(FILENAME_PREFIX.length(), file.getName().indexOf(".")));
            if (timestamp >= greaterTimestamp) {
                greaterTimestamp = timestamp;
                lastBackup = file;
            }
        }
        return lastBackup;
    }

    private File getBackupFolder() {
        File root = Environment.getExternalStorageDirectory();
        File backupFolder = new File(root.getAbsolutePath() + "/" + context.getString(R.string.app_name));
        return backupFolder;
    }
}
