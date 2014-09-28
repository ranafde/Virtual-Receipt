package edu.upenn.cis599.eas499;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis599.R;
import edu.upenn.cis599.R.id;
import edu.upenn.cis599.R.layout;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class CustomAdapter extends ArrayAdapter<String> {
	Activity activity;
	ArrayList<String> list;
	
	static class ViewHolder {
	    TextView text;
	    ImageView image;
	  }
	
	public CustomAdapter(Activity activity, int layout, ArrayList<String> list){
		super(activity, layout, list);
		this.activity = activity;
		this.list = list;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View rowView = convertView;
		ViewHolder view;
		
		if (rowView == null){
			LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.main_item, null);
			
			view = new ViewHolder();
			view.text = (TextView) rowView.findViewById(R.id.menu);

			rowView.setTag(view);
		}
		
		else {
			view = (ViewHolder) rowView.getTag();
		}
		
//		String item = list.get(position);
		String item = getItem(position);
		view.text.setText(item);
		
		return rowView;
	}
}