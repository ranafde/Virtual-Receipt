/**
 * Copyright 2012 Annie Lee. All Rights Reserved.
 */

package edu.upenn.cis599.eas499;


import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis599.DropboxActivity;
import edu.upenn.cis599.R;
import edu.upenn.cis599.R.array;
import edu.upenn.cis599.R.layout;
import edu.upenn.cis599.charts.StatisticsViewerActivity;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VirtualReceiptActivity extends Activity {
	
	ListView lv;
	ArrayList<String> menuArray;
	private static final String TAG = "VirtualReceiptActivity.java";
	
	AlarmManager am;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        setRepeatingAlarm();
        
        menuArray = new ArrayList<String>();
        menuArray.add(getResources().getString(R.string.add_income));
        menuArray.add(getResources().getString(R.string.add_receipt));
        menuArray.add(getResources().getString(R.string.view_receipt));
        menuArray.add(getResources().getString(R.string.spending_stats));
        menuArray.add(getResources().getString(R.string.dropbox));
        
        setContentView(R.layout.main);
        
        lv = (ListView) findViewById(android.R.id.list);
//        lv = getListView
        View header = (View)getLayoutInflater().inflate(R.layout.logo_header, null);
        lv.addHeaderView(header, R.layout.main, false);
        
        CustomAdapter adapter = new CustomAdapter(this, R.layout.main_item, menuArray);
        lv.setAdapter(adapter);    
        
        lv.setOnItemClickListener(new MyListener(this));
        header.setClickable(false);
        
        Log.d(TAG,"okay so far");
        //

        /**
         * Yiran Qin
         * Original attempt to achieve data sharing feature using parse.com database instance
         */
//        Parse.initialize(this, "MR3lfXcvroMCIWElUptZAv40qCg6Rbwi1xVoq2qS", "rwKKqMjU6kXIUiH47uZZLz1FTwFaHNH92HuvNvZ7");
//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();
    }
    
    public void setRepeatingAlarm() {
    	  Intent intent = new Intent(this, NotifyAlarm.class);
    	  PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
    	    intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	  am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
    	    (10 * 1000), pendingIntent);
    }
    
    class MyListener implements OnItemClickListener {

    	private Activity p = null;
    	
    	public MyListener(Activity a) {
    		p = a;
    	}
    	
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TextView txt = (TextView) parent.getChildAt(position - lv.getFirstVisiblePosition()).findViewById(R.id.menu);
            String label = txt.getText().toString();
			Intent intent;
			Log.d(TAG,"checking labels");
			/* Add Income */
			if (label.equals(getResources().getString(R.string.add_income))) {
    			intent = new Intent(p, ReceiptEntryActivity.class);
    			intent.putExtra("Income","Yes");
    			Log.d(TAG,"add an income virtual receipt activity");
    			Toast.makeText(getApplicationContext(), R.string.add_income_Toast, Toast.LENGTH_LONG).show();
    			startActivity(intent);
    		}
			/* Add Expense */
			else if (label.equals(getResources().getString(R.string.add_receipt))) {
    			intent = new Intent(p, ReceiptEntryActivity.class);
    			intent.putExtra("Income","No");
    			Toast.makeText(getApplicationContext(), R.string.add_receipt_Toast, Toast.LENGTH_LONG).show();
    			startActivity(intent);
    		}
			/* View Entries */
    		else if (label.equals(getResources().getString(R.string.view_receipt))) {
    			intent = new Intent(p, ReceiptsListActivity.class);
    			Toast.makeText(getApplicationContext(), R.string.view_receipt_Toast, Toast.LENGTH_LONG).show();
    			startActivity(intent);
    		}
			/*  View spending statistics */
    		else if (label.equals(getResources().getString(R.string.spending_stats))) {
    			intent = new Intent(p, StatisticsViewerActivity.class);
    			Toast.makeText(getApplicationContext(), R.string.spending_stats_Toast, Toast.LENGTH_LONG).show();
    			startActivity(intent);
    		}
    		/* Sync with dropbox */
    		else if (label.equals(getResources().getString(R.string.dropbox))) {
    			/*Yiran Qin Dropbox activity to handle all front-end data sharing feature*/
    			intent = new Intent(p, DropboxActivity.class);
    			Toast.makeText(getApplicationContext(), R.string.dropbox_Toast, Toast.LENGTH_LONG).show();
    			startActivity(intent);
    		}
		}
    	
    }

}