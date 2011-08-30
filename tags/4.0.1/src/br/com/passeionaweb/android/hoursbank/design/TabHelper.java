package br.com.passeionaweb.android.hoursbank.design;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import br.com.passeionaweb.android.hoursbank.R;

public class TabHelper {
    private static View prepareTabView(Context context, int textId, int drawable) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        ImageView tabImage = (ImageView) view.findViewById(R.id.tabImage);
        tabImage.setImageDrawable(context.getResources().getDrawable(drawable));
        TextView tabText = (TextView)view.findViewById(R.id.tabText);
        tabText.setText(textId);
        return view;
    }

    public static void addTab(TabHost host, int title, String tag, int drawable, Intent intent) {
        TabHost.TabSpec spec = host.newTabSpec(tag);
        spec.setContent(intent);
        View view = prepareTabView(host.getContext(), title, drawable);
        spec.setIndicator(view);
        host.addTab(spec);
    }
}
