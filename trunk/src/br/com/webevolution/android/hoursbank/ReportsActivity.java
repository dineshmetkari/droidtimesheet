package br.com.webevolution.android.hoursbank;

import android.app.ListActivity;
import android.content.Intent;
import android.view.MenuItem;

public class ReportsActivity extends ListActivity {
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch(item.getItemId()) {
			case HoursBank.MENU_SETTINGS:
				Intent intent = new Intent(this, PreferencesActivity.class);
				startActivity(intent);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}