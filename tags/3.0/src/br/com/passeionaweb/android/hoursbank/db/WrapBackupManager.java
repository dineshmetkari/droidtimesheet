package br.com.passeionaweb.android.hoursbank.db;

import android.app.backup.BackupManager;
import android.content.Context;
import android.util.Log;

public class WrapBackupManager {
    private BackupManager wrappedInstance;

    static {
        try {
            Class.forName("android.app.backup.BackupManager");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkAvailable() {
    }

    public void dataChanged() {
        wrappedInstance.dataChanged();
    }

    public WrapBackupManager(Context context) {
        wrappedInstance = new BackupManager(context);
    }

}