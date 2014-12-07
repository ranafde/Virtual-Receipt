/**
 * Copyright 2012 Annie Lee. All Rights Reserved.
 */

package edu.upenn.cis599.eas499;

import edu.upenn.cis599.R;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ReceiptsListActivity extends ListActivity {
	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_VIEW=1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private ReceiptDbAdapter mDbHelper;
	private Cursor mReceiptCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new ReceiptDbAdapter(this);
		mDbHelper.open();
		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {
		mReceiptCursor = mDbHelper.fetchAllReceipts();
		startManagingCursor(mReceiptCursor);
		String[] from = new String[] {ReceiptDbAdapter.KEY_DESCRIPTION, ReceiptDbAdapter.KEY_DATE, ReceiptDbAdapter.KEY_AMOUNT};
		int[] to = new int[] { R.id.receipt_text, R.id.receipt_date, R.id.receipt_amount};
		
		setContentView(R.layout.receipt_list);
		//Edited by Xiaolu
		ListView lv = getListView();
		View header = (View)getLayoutInflater().inflate(R.layout.receipt_header, null);
		lv.addHeaderView(header, R.layout.receipts_row, false);
		
		SimpleCursorAdapter receipts = new SimpleCursorAdapter(this, R.layout.receipts_row, mReceiptCursor, from, to);
		lv.setAdapter(receipts);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_insert_receipt);
		return result;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case INSERT_ID:
			createReceipt();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete_receipt);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
		case DELETE_ID:
			mDbHelper.deleteReceipt(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	private void createReceipt() {
		Intent i = new Intent(this, ReceiptEntryActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ReceiptViewActivity.class);
		i.putExtra(ReceiptDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_VIEW);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}
	
	public void exportToPDF(View v) {
		mDbHelper = new ReceiptDbAdapter(this);
		mDbHelper.open();
        mDbHelper.exportReceiptsToPDF();
        mDbHelper.close();
        Toast.makeText(getApplicationContext(), "Saved PDF file on the phone", Toast.LENGTH_LONG).show();
	}
	
	public void onInfoButtonClick(View view){
		Toast.makeText(getApplicationContext(), R.string.view_receipt_Toast, Toast.LENGTH_LONG).show();
	}
}
