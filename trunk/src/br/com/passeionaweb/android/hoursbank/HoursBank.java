package br.com.passeionaweb.android.hoursbank;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.Window;
import android.widget.TabHost;
import br.com.passeionaweb.android.billing.util.UpgradeChecker;
import br.com.passeionaweb.android.hoursbank.design.TabHelper;

public class HoursBank extends TabActivity {
    private static final String TAB_BLOTTER   = "blotter";
    private static final String TAB_DAY       = "day";
    private static final String TAB_WEEK      = "week";
    private static final String TAB_MONTH     = "month";

    public static final int     MENU_SETTINGS = 0;
    public static final int     MENU_EXPORT   = 1;

    public static Dialog        upgradeDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        setupTabs();
        UpgradeChecker.showUpgradeDialog(this); 

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_EXPORT, Menu.NONE, R.string.menu_export).setIcon(android.R.drawable.ic_menu_share);
        menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, R.string.menu_settings).setIcon(android.R.drawable.ic_menu_preferences);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupTabs() {
        TabHost tabHost = getTabHost();
        
        TabHelper.addTab(tabHost, R.string.tab_title_day, TAB_DAY, R.drawable.ic_tab_day, new Intent(this, DayActivity.class));
        
        TabHelper.addTab(tabHost, R.string.tab_title_week, TAB_WEEK, R.drawable.ic_tab_week, new Intent(this, WeekActivity.class));
        
        
        TabHelper.addTab(tabHost, R.string.tab_title_month, TAB_MONTH, R.drawable.ic_tab_month, new Intent(this, MonthActivity.class));
        
        TabHelper.addTab(tabHost, R.string.tab_title_blotter, TAB_BLOTTER, R.drawable.ic_tab_graph, new Intent(this, BlotterActivity.class));
        
        
        /*tabHost.addTab(tabHost.newTabSpec(TAB_DAY).setIndicator(getResources().getText(R.string.tab_title_day), getResources().getDrawable(R.drawable.ic_tab_day)).setContent(
                new Intent(this, DayActivity.class)));*/
        
        /*tabHost.addTab(tabHost.newTabSpec(TAB_WEEK).setIndicator(getString(R.string.tab_title_week), getResources().getDrawable(R.drawable.ic_tab_week)).setContent(
                new Intent(this, WeekActivity.class)));

        tabHost.addTab(tabHost.newTabSpec(TAB_MONTH).setIndicator(getResources().getText(R.string.tab_title_month), getResources().getDrawable(R.drawable.ic_tab_month)).setContent(
                new Intent(this, MonthActivity.class)));

        tabHost.addTab(tabHost.newTabSpec(TAB_BLOTTER).setIndicator(getResources().getText(R.string.tab_title_blotter), getResources().getDrawable(R.drawable.ic_tab_graph)).setContent(
                new Intent(this, BlotterActivity.class)));*/
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case UpgradeChecker.DIALOG_UPGRADE:
                return UpgradeChecker.getUpgradeDialog(this);
            default:
                return super.onCreateDialog(id);
        }

    }
}