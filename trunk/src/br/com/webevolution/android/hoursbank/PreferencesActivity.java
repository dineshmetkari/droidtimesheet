package br.com.webevolution.android.hoursbank;

import br.com.webevolution.android.hoursbank.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}
