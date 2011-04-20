package br.com.passeionaweb.android.hoursbank.db;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class CloudBackupHelper extends BackupAgentHelper {
    private static final String KEY_PREFERENCES = "prefs";
    private static final String KEY_CHECKPOINTS = "check";

    // Simply allocate a helper and install it
    public void onCreate() {
        String defaultPrefName = getPackageName() + "_preferences";

        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, defaultPrefName);

        addHelper(KEY_PREFERENCES, helper);
    }

}
