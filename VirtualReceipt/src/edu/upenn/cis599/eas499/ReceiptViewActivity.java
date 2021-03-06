/**
 * Copyright 2012 Annie Lee. All Rights Reserved.
 */

package edu.upenn.cis599.eas499;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;
import com.dropbox.client2.session.Session.AccessType;

import edu.upenn.cis599.FinishListener;
import edu.upenn.cis599.R;
import edu.upenn.cis599.R.id;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiptViewActivity extends Activity {

	private ReceiptDbAdapter mDbHelper;
	private TextView mDescriptionText;
	private TextView mAmountText;
	private TextView mDateText;
	private TextView mCategoryText;
	private TextView mPaymentText;
	private Long mRowId;
	private TextView mRecurringText;
	
	// added by charles 11.21
	DropboxAPI<AndroidAuthSession> mApi = null;
    final static private String APP_KEY = "w9bii3r2hidx7jp";
    final static private String APP_SECRET = "uxrrek6sgqf6uv0";
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    private boolean linking = false;
    String dateString = "";
    private static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/VirtualReceipt/";
    	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new ReceiptDbAdapter(this);
		mDbHelper.open();
		
		
		setContentView(R.layout.receipt_view);
		
		mDescriptionText = (TextView) findViewById(R.id.description);
		mAmountText = (TextView) findViewById(id.amount);
		mDateText = (TextView) findViewById(id.date);
		mCategoryText = (TextView) findViewById(id.category);
		mPaymentText = (TextView) findViewById(id.payment);
		mRecurringText = (TextView)findViewById(id.check_recurring);
		mRowId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(ReceiptDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(ReceiptDbAdapter.KEY_ROWID)
									: null;
		}
		
		populateFields();
		
		// added by charles 11.21
		AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        checkAppKeySetup();
	}
	
	// added by charles 11.21
	private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }
	
	private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            finish();
            return;
        }

        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            finish();
        }
    }
	
	private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }

	private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }
	
	private void populateFields() {
		if (mRowId != null) {
			Log.d(ACTIVITY_SERVICE, "Populating labels");
			Cursor receipt = mDbHelper.fetchReceipt(mRowId);
			startManagingCursor(receipt);
			mDescriptionText.setText(receipt.getString(receipt.getColumnIndexOrThrow(ReceiptDbAdapter.KEY_DESCRIPTION)));
			mAmountText.setText(receipt.getString(receipt.getColumnIndexOrThrow(ReceiptDbAdapter.KEY_AMOUNT)));
			//Reformat date
			String date = receipt.getString(receipt.getColumnIndexOrThrow(ReceiptDbAdapter.KEY_DATE));
			mDateText.setText(date);
			//Pull payment type from enum.
			int paymentIntVal = Integer.valueOf(receipt.getString(receipt.getColumnIndexOrThrow(ReceiptDbAdapter.KEY_PAYMENT)));
			mPaymentText.setText(PaymentType.get(paymentIntVal).getText());
			//Pull category text from db.
			mCategoryText.setText(receipt.getString(receipt.getColumnIndexOrThrow(ReceiptDbAdapter.KEY_CATEGORY)));
			mRecurringText.setText(receipt.getString(receipt.getColumnIndexOrThrow(ReceiptDbAdapter.KEY_RECURRING)).equals("1")?"Yes":"No");
			if(mRecurringText.getText().equals("No")){
				Button addReminderButton = (Button) findViewById(R.id.addReminder);
				addReminderButton.setVisibility(View.GONE);
			}
			receipt.close();
			}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = mApi.getSession();
		if (linking) {
			if (session.authenticationSuccessful()) {
	        	try {
	        		// Mandatory call to complete the auth
	        		session.finishAuthentication();
	        		
	        		// Store it locally in our app for later use
	        		TokenPair tokens = session.getAccessTokenPair();
	        		storeKeys(tokens.key, tokens.secret);

	            } catch (IllegalStateException e) {
	            	//showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
	            }
	        }
			getCloudFile();
			linking = false;
		} else {
			
		}
	}
	
	// added by charles 11.21
	public void getCloudFile() {
		FileOutputStream outputStream = null;
        Entry dirent;
		try {
			dirent = mApi.metadata("/", 1000, null, true, null);
			if (!dirent.isDir || dirent.contents == null) {
				return;
			}
			//File file = new File(DATA_PATH + dateString);
			File file = new File(DATA_PATH + "viewImg.jpg");
			outputStream = new FileOutputStream(file);
			mApi.getFile("/" + dateString, null, outputStream, null);
			outputStream.close();

			Intent intent = new Intent(this, ImageOCRActivity.class);
			intent.putExtra("mode", 1);
			startActivity(intent);
		} catch (Exception e) {
			showErrorMessage("Error", "Failed to download from Dropbox");	
        }
	} 
	
	public void onAddReminderClick(View view) {
		Log.d("", "AddReminderClicked");
		Intent intent = new Intent(this, ReminderActivity.class);
		startActivity(intent);
	}
	public void onViewImageClick(View view) {
		Cursor c = mDbHelper.fetchReceiptFullDate(mRowId);

		// added by charles 11.20
		int flagI = Integer.valueOf(c.getString(c.getColumnIndexOrThrow(ReceiptDbAdapter.KEY_FLAG)));
		boolean flag = (flagI == 1);
		
		if (flag) {
			Toast.makeText(this, "Downloading images from cloud", Toast.LENGTH_SHORT).show();
			dateString = c.getString(c.getColumnIndex(ReceiptDbAdapter.KEY_DATE));
			dateString = dateString.replace(" ", "");
			dateString = dateString.replace(":", "");
			dateString = dateString.replace("-", "");
			dateString = dateString + ".jpg";
			if (!mApi.getSession().isLinked()) {
				mApi.getSession().startAuthentication(ReceiptViewActivity.this);
				linking = true;
			} else {
				getCloudFile();
			}
		} else {
			try{
				startManagingCursor(c);
				byte[] bitmapData = c.getBlob(c.getColumnIndexOrThrow(ReceiptDbAdapter.KEY_IMAGE));
				if(bitmapData != null){
					File file = new File(DATA_PATH + "viewImg.jpg");
					FileOutputStream fw;
					try {
						fw = new FileOutputStream(file.getAbsoluteFile());
						BufferedOutputStream bw = new BufferedOutputStream(fw);

						bw.write(bitmapData);
						bw.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(this, ImageOCRActivity.class);
					//intent.putExtra("image", bitmapData);
					intent.putExtra("mode", 1);
					c.close();
					startActivity(intent);
				}else{
					Context context = getApplicationContext();
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, "The image has already been deleted", duration);
					toast.show();
				}		
			}catch(Exception e){
				showErrorMessage("Error", "Failed to retrieve the blob data, it's corrupted.");	
			}	
		}

	}
	
	/**
	 * CIS599 Yiran Qin
	 * @param view
	 */
	
	public void onDeleteClick(View view) {
		
		if((mRecurringText.getText().toString()).equals("Yes")) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Delete Recurring Receipt");
			alert.setMessage("Do you want to delete all future receipts from this entry onwards?");
			alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					if (mRowId != null) {
						if(mDbHelper.deleteRecurringReceipt(mDescriptionText.getText().toString(),mRowId,mDateText.getText().toString())){
							mDbHelper.close();
							finish();
						}else{
							showErrorMessage("Error", "Failed to delete the database entry, it's corrupted.");	
						}
					}
				}
			});
			alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					if (mRowId != null) {
						if(mDbHelper.deleteReceipt(mRowId)){
							mDbHelper.close();
							finish();
						}else{
							showErrorMessage("Error", "Failed to delete the database entry, it's corrupted.");	
						}
					}
				}
			});
			alert.show();
		} else {
		
		// Prompt for adding an image of receipt
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Delete Receipt");
		alert.setMessage("Are you sure to delete this receipt?");
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if (mRowId != null) {
					if(mDbHelper.deleteReceipt(mRowId)){
						mDbHelper.close();
						finish();
					}else{
						showErrorMessage("Error", "Failed to delete the database entry, it's corrupted.");	
					}
				}
			}
		});
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				//do nothing
			}
		});
		alert.show();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();		
		mDbHelper.close();
		finish();
	}
	
	/**
	   * Displays an error message dialog box to the user on the UI thread.
	   * 
	   * @param title The title for the dialog box
	   * @param message The error message to be displayed
	   */
	void showErrorMessage(String title, String message) {
		new AlertDialog.Builder(this)
	    	.setTitle(title)
	    	.setMessage(message)
	    	.setOnCancelListener(new FinishListener(this))
	    	.setPositiveButton( "Done", new FinishListener(this))
	    	.show();
	}
}
