package br.com.passeionaweb.android.hoursbank;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import br.com.passeionaweb.android.hoursbank.db.DatabaseHelper;

public class CheckpointCursorAdapter extends ResourceCursorAdapter {

	private int layoutType = 0;
	public static final int BLOTTER = 1;
	public static final int DAY = 2;
	public static final int MONTH = 3;
	public static final int OVERVIEW = 4;
	public static final int LAYOUT_ID = R.layout.checkpoint_row;
	public CheckpointCursorAdapter(Context context, int layoutType, Cursor cursor) {
		super(context, LAYOUT_ID, cursor);
		this.layoutType = layoutType;
	}

	@Override
	public View newView(Context context, Cursor cur, ViewGroup parent) {
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return li.inflate(LAYOUT_ID, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		TextView tvListText = (TextView) view.findViewById(R.id.txtCheckPoint);
		Date dt = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_CHECKPOINT)));
		switch (layoutType) {
			case BLOTTER:
				tvListText.setText(dt.toLocaleString());
				((ImageView) view.findViewById(R.id.imgCheckpointInOut)).setImageResource(CheckpointsView.getImageResId(cursor.getCount() - cursor.getPosition()));
				break;
			case DAY:
				tvListText.setText(new SimpleDateFormat("HH:mm:ss").format(dt));
				((ImageView) view.findViewById(R.id.imgCheckpointInOut)).setImageResource(CheckpointsView.getImageResId(cursor.getPosition() + 1));
				break;
			case MONTH:
				tvListText.setText(new SimpleDateFormat("MMM dd HH:mm:ss").format(dt));
				((ImageView) view.findViewById(R.id.imgCheckpointInOut)).setImageResource(CheckpointsView.getImageResId(cursor.getPosition() + 1));
				break;
			case OVERVIEW:
				break;
		}
	}



}
