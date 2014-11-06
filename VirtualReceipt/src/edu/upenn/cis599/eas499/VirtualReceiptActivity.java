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
import android.app.ListActivity;
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

public class VirtualReceiptActivity extends Activity {
	private ReceiptDbAdapter mDbHelper;
	ListView lv;
	ArrayList<String> menuArray;
	private static final String TAG = "VirtualReceiptActivity.java";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*mDbHelper = new ReceiptDbAdapter(this);
        mDbHelper.open();
        mDbHelper.updateBlobFields();
        mDbHelper.close();*/
        
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
			if (label.equals("Add Income")) {
    			intent = new Intent(p, ReceiptEntryActivity.class);
    			intent.putExtra("Income","Yes");
    			Log.d(TAG,"add an income virtual receipt activity");
    			startActivity(intent);
    		}
			else if (label.equals("Add a receipt")) {
    			intent = new Intent(p, ReceiptEntryActivity.class);
    			intent.putExtra("Income","No");
    			startActivity(intent);
    		}
    		else if (label.equals("View receipts")) {
    			intent = new Intent(p, ReceiptsListActivity.class);
    			startActivity(intent);
    		}
    		else if (label.equals("View spending statistics")) {
    			intent = new Intent(p, StatisticsViewerActivity.class);
    			startActivity(intent);
    		}else if (label.equals("Sync with dropbox")) {
    			/*Yiran Qin Dropbox activity to handle all front-end data sharing feature*/
    			intent = new Intent(p, DropboxActivity.class);
    			startActivity(intent);
    		}
		}
    	
    }

}