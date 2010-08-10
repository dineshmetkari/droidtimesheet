package br.com.passeionaweb.android.hoursbank;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.widget.TabHost;
import br.com.passeionaweb.android.hoursbank.BlotterActivity;
import br.com.passeionaweb.android.hoursbank.DayActivity;
import br.com.passeionaweb.android.hoursbank.MonthActivity;

public class HoursBank extends TabActivity {
	private static final String TAB_BLOTTER = "blotter";
	private static final String TAB_DAY = "day";
	private static final String TAB_MONTH = "month";

	public static final int MENU_SETTINGS = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setupTabs();

		PreferenceManager.setDefaultValues(this, R.xml.settings, false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, R.string.menu_settings).setIcon(android.R.drawable.ic_menu_preferences);
		return super.onCreateOptionsMenu(menu);
	}

	private void setupTabs() {
		TabHost tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec(TAB_DAY)
		// TODO create an icon and set it here as a drawable
				.setIndicator(getResources().getText(R.string.tab_title_day)).setContent(new Intent(this, DayActivity.class)));

		tabHost.addTab(tabHost.newTabSpec(TAB_MONTH)
		// TODO create an icon and set it here as a drawable
				.setIndicator(getResources().getText(R.string.tab_title_month)).setContent(new Intent(this, MonthActivity.class)));

		tabHost.addTab(tabHost.newTabSpec(TAB_BLOTTER).setIndicator(getResources().getText(R.string.tab_title_blotter), getResources().getDrawable(R.drawable.ic_tab_graph)).setContent(
				new Intent(this, BlotterActivity.class)));
	}
}