package br.com.webevolution.android.hoursbank;

import java.util.Date;

import br.com.webevolution.android.hoursbank.db.DatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class CheckPointListAdapter extends ResourceCursorAdapter {
	
	private int listId = 0;
	
    public CheckPointListAdapter(Context context, int layoutId, Cursor cursor) {
    	super(context, layoutId, cursor);
    	this.listId = layoutId;
    }

    @Override
    public View newView(Context context, Cursor cur, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return li.inflate(this.listId, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvListText = (TextView)view.findViewById(R.id.txtCheckPoint);
        Date dt = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_CHECKPOINT)));
        tvListText.setText(dt.toLocaleString());
        ((ImageView)view.findViewById(R.id.imgCheckpointInOut)).setImageResource(HoursBank.getImageResId(cursor.getPosition()+1));
    }
}
