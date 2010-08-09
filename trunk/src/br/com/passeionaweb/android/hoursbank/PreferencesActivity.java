package br.com.passeionaweb.android.hoursbank;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {

	public static final String KEY_MIN_LUNCH = "min_lunch";
	public static final String KEY_HOURS_MONDAY = "hours_monday";
	public static final String KEY_HOURS_TUESDAY = "hours_tuesday";
	public static final String KEY_HOURS_WEDNESDAY = "hours_wednesday";
	public static final String KEY_HOURS_THURSDAY = "hours_thursday";
	public static final String KEY_HOURS_FRIDAY = "hours_friday";
	public static final String KEY_HOURS_SATURDAY = "hours_saturday";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		prefs.registerOnSharedPreferenceChangeListener(listener);
		
		if(getIntent().getBooleanExtra("ERROR", false)) {
			Toast t =  Toast.makeText(this, getResources().getString(R.string.prefs_error), Toast.LENGTH_LONG);
			t.show();
		}
	}

	public SharedPreferences.OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			String defValue = "0:00";
			String value = sharedPreferences.getString(key, defValue);
			if (value.indexOf(":") == -1 && (value.length() == 2 || value.length() == 1)) {
				value += ":00";
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
						getApplicationContext()).edit();
				editor.putString(key, value);
				editor.commit();
			}
		}
	};

}
