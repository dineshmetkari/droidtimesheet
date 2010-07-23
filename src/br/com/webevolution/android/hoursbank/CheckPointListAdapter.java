package br.com.webevolution.android.hoursbank;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import br.com.webevolution.android.hoursbank.db.DatabaseHelper;

public class CheckPointListAdapter extends ResourceCursorAdapter {

	private int layoutType = 0;
	public static final int BLOTTER = 1;
	public static final int DAY = 2;
	public static final int MONTH = 3;
	public static final int OVERVIEW = 4;
	public CheckPointListAdapter(Context context, int layoutType, Cursor cursor) {
		super(context, getLayoutId(), cursor);
		this.layoutType = layoutType;
	}

	@Override
	public View newView(Context context, Cursor cur, ViewGroup parent) {
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return li.inflate(getLayoutId(), parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		TextView tvListText = (TextView) view.findViewById(R.id.txtCheckPoint);
		Date dt = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_CHECKPOINT)));
		switch (layoutType) {
			case BLOTTER:
				tvListText.setText(dt.toLocaleString());
				((ImageView) view.findViewById(R.id.imgCheckpointInOut)).setImageResource(HoursBank.getImageResId(cursor.getPosition() + 1));
				break;
			case DAY:
				tvListText.setText(new SimpleDateFormat("HH:mm:ss").format(dt));
				((ImageView) view.findViewById(R.id.imgCheckpointInOut)).setImageResource(HoursBank.getImageResId(cursor.getPosition() + 1));
				break;
			case MONTH:
				tvListText.setText(new SimpleDateFormat("MMM dd HH:mm:ss").format(dt));
				((ImageView) view.findViewById(R.id.imgCheckpointInOut)).setImageResource(HoursBank.getImageResId(cursor.getPosition() + 1));
				break;
			case OVERVIEW:
				break;
		}
	}

	private static int getLayoutId() {
		return R.layout.checkpoint_row;
	}

}
