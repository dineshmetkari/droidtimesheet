package br.com.webevolution.android.hoursbank;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class HoursBank extends TabActivity {
	private static final String TAB_DAY = "day";
	private static final String TAB_MONTH = "month";
	private static final String TAB_OVERVIEW = "overview";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		TabHost tabHost = getTabHost();


		tabHost.addTab(tabHost.newTabSpec(TAB_DAY)
				// TODO create an icon and set it here as a drawnable
				.setIndicator(
						getResources().getText(R.string.tab_title_day))
				.setContent(new Intent(this, Day.class)));
		
		tabHost.addTab(tabHost.newTabSpec(TAB_MONTH)
				// TODO create an icon and set it here as a drawnable
				.setIndicator(
						getResources().getText(R.string.tab_title_month))
				.setContent(new Intent(this, Month.class)));
		
		tabHost.addTab(tabHost.newTabSpec(TAB_OVERVIEW)
				// TODO create an icon and set it here as a drawnable
				.setIndicator(
						getResources().getText(R.string.tab_title_overview))
				.setContent(new Intent(this, Overview.class)));

	}
}