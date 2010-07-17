package br.com.webevolution.android.hoursbank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;



public class MonthActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createCheckpoint();
				
			}
		});
        setContentView(button);
        
    }
    private void createCheckpoint(){
    	Intent intent = new Intent(this, CheckpointService.class);
    	startService(intent);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	TextView text = new TextView(this);
    	text.setText("Result OK");
    	setContentView(text);
    }
}