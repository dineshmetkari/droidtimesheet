package br.com.passeionaweb.android.hoursbank;

import android.os.Bundle;
import android.preference.PreferenceActivity;
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
		if(getIntent().getBooleanExtra("ERROR", false)) {
			Toast t =  Toast.makeText(this, getResources().getString(R.string.prefs_error), Toast.LENGTH_LONG);
			t.show();
		}
	}
}
