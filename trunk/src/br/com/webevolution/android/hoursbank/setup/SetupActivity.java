package br.com.webevolution.android.hoursbank.setup;

import br.com.webevolution.android.hoursbank.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SetupActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
	}
}
