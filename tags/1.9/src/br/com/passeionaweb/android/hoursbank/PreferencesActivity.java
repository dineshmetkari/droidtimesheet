package br.com.passeionaweb.android.hoursbank;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {

	public static final String KEY_FIRST_DAY = "first_day";
	public static final String KEY_MIN_LUNCH = "min_lunch";
	public static final String KEY_HOURS_MONDAY = "hours_monday";
	public static final String KEY_HOURS_TUESDAY = "hours_tuesday";
	public static final String KEY_HOURS_WEDNESDAY = "hours_wednesday";
	public static final String KEY_HOURS_THURSDAY = "hours_thursday";
	public static final String KEY_HOURS_FRIDAY = "hours_friday";
	public static final String KEY_HOURS_SATURDAY = "hours_saturday";
	public static final String KEY_HOURS_SUNDAY = "hours_sunday";

	public static int getFirstDayOfMonth(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.valueOf(prefs.getString(KEY_FIRST_DAY, "1"));
	}

	public static String getHoursPrefByDay(Context context, int dayOfWeek) {
		String defValue = "0:00";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		switch (dayOfWeek) {
			case Calendar.MONDAY:
				return prefs.getString(KEY_HOURS_MONDAY, defValue);
			case Calendar.TUESDAY:
				return prefs.getString(KEY_HOURS_TUESDAY, defValue);
			case Calendar.WEDNESDAY:
				return prefs.getString(KEY_HOURS_WEDNESDAY, defValue);
			case Calendar.THURSDAY:
				return prefs.getString(KEY_HOURS_THURSDAY, defValue);
			case Calendar.FRIDAY:
				return prefs.getString(KEY_HOURS_FRIDAY, defValue);
			case Calendar.SATURDAY:
				return prefs.getString(KEY_HOURS_SATURDAY, defValue);
			case Calendar.SUNDAY:
				return prefs.getString(KEY_HOURS_SUNDAY, defValue);
		}
		return defValue;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		if (getIntent().getBooleanExtra("ERROR", false)) {
			Toast t = Toast.makeText(this, getResources().getString(R.string.prefs_error),
					Toast.LENGTH_LONG);
			t.show();
		}
	}

}
