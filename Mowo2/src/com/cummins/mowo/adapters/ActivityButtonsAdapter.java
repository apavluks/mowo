package com.cummins.mowo.adapters;

import java.util.ArrayList;

import com.cummins.mowo.R;
import com.cummins.mowo.vos.ActivityType;
import android.app.Activity;

import android.content.ClipData.Item;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityButtonsAdapter extends ArrayAdapter<ActivityType> {

	private final static String TAG = ActivityButtonsAdapter.class.getSimpleName();
	private Context context;
	private int layoutResourceId;
    private ArrayList<ActivityType> data = new ArrayList<ActivityType>();

	public ActivityButtonsAdapter(Context context, int layoutResourceId, ArrayList<ActivityType> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder = null;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new RecordHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}
		ActivityType item = data.get(position);
		holder.txtTitle.setText(item.getTitle());
		((TextView) holder.txtTitle).setCompoundDrawablesWithIntrinsicBounds( 0, item.getIcon(), 0, 0);
		
		Log.d(TAG, "ActivityButtonsAdapter view high " + row.getHeight() );
		return row;
	}

	static class RecordHolder {
		TextView txtTitle;
	}
}
