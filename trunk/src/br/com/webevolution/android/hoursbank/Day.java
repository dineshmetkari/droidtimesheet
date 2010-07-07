package br.com.webevolution.android.hoursbank;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;



public class Day extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView text = new TextView(this);
        text.setText("some text DAY");
        setContentView(text);
        
        
    }
}