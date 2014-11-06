/**
 * Copyright 2012 Annie Lee. All Rights Reserved.
 */

package edu.upenn.cis599.eas499;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;
import com.dropbox.client2.session.Session.AccessType;
import edu.upenn.cis599.FinishListener;
import edu.upenn.cis599.R;
import edu.upenn.cis599.R.id;
import edu.upenn.cis599.SyncToDropbox;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 
 * @author Yiran Qin
 * No big difference regarding to this entry activity, the major changes include added more try/catch to avoid unhandled exception
 * alert pop-up for error to avoid crashing the app and notify the user about the specific error, 
 *
 */
public class ReceiptEntryActivity extends Activity {

	private ReceiptDbAdapter mDbHelper;
	private EditText mDescriptionText;
	private EditText mAmountText;
	private EditText mDateText;
	private Spinner mCategoryText;
	private RadioGroup mPayment;
	private CheckBox mRecurring;
	private Long mRowId;
	private byte[] mImage;

	private String ocrAmount;
	private String ocrDesc;
	private Date mDate;

	private int mYear;
	private int mMonth;
	private int mDay;


	static final int DATE_DIALOG_ID = 0;
	private static final String TAG = "ReceiptEntryActivity.java";
	private static final int CAMERA_REQUEST = 1888;
	private static final int IMAGE_SELECTION = 1889;

	public static final String lang = "eng";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/VirtualReceipt/";

	private static String _path;
	private static File trainData;
	private static File imageFile;

	private Uri mImageCaptureUri;
	public static ArrayList<String> categoryList;
	public static ArrayList<String> categoryListIncome;
	
	private boolean isAddClicked = false;
	
	// added by charles 18/11
	private boolean cloudStorage = false;
	private Button mSave;
	
	// added by charles 11.20
	DropboxAPI<AndroidAuthSession> mApi;
	final static private String APP_KEY = "w9bii3r2hidx7jp";
    final static private String APP_SECRET = "uxrrek6sgqf6uv0";
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    private boolean linking = false;

    private SyncToDropbox upload = null;
	protected long captureTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		String value="No";
		if (extras != null) {
		    value = extras.getString("Income");
		    Log.v(TAG,"Value received from other activity"+value);
		}
		
		/* Open Database connection */
		mDbHelper = new ReceiptDbAdapter(this);
		mDbHelper.open();
		
		
		
		if(value.equals("Yes")){
			Log.d(ACTIVITY_SERVICE, "Entering ReceiptEntryActivity. Income. Loading form");

			if(categoryListIncome == null){
				categoryListIncome = new ArrayList<String>(Arrays.asList("Income","Budget"));
			}
			loadFormIncome();

			Log.d(ACTIVITY_SERVICE, "Done loading form for income");
		}
		else{
			Log.d(ACTIVITY_SERVICE, "Entering ReceiptEntryActivity. Adding receipt");
			isAddClicked = false;
			
			/* initialize categoryList as follows */
			if(categoryList == null){
				categoryList = new ArrayList<String>(Arrays.asList("Education","Grocery","Clothing", "Rent", "Bill", "Resteraunt", "Recreation", "Others"));
			}
			Log.v(TAG, "Entering ReceiptEntryActivity");
	
			// Setting up Tesseract
			if(_path == null && trainData == null)
				initialize();		
			
			// Prompt for adding an image of receipt
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Upload a Receipt");
			alert.setMessage("Would you like to add a receipt image?");
			alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					//Go to Camera and take picture to store in db
					//captureImage();
					try{
						isAddClicked = !isAddClicked;
						if(isAddClicked){		
							captureTime = System.currentTimeMillis();	
							Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							mImageCaptureUri = Uri.fromFile(imageFile);
							cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
							cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
							Log.d(ACTIVITY_SERVICE, "Started...");
							startActivityForResult(cameraIntent, CAMERA_REQUEST);
							Log.d(ACTIVITY_SERVICE, "Done");
						} 
					} catch (RuntimeException e) {
						// Barcode Scanner has seen crashes in the wild of this variety:
						// java.?lang.?RuntimeException: Fail to connect to camera service
						showErrorMessage("Error", "Could not initialize camera. Please try restarting device.");
				    }
				}
			});
			alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					Log.d(TAG, "loading form for adding receipts");
					loadForm();
					//categoryList = null;
					Log.d(TAG, "DONE loading form for adding receipts !!!");
				}
			});
			alert.show();	
			//added by charles 11.20
			AndroidAuthSession session = buildSession();
			mApi = new DropboxAPI<AndroidAuthSession>(session);
			checkAppKeySetup();
		}	
		
	}
	
	
	
	// added by charles 11.20
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
            //showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
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
	
	private void initialize(){
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}
		}
		trainData = new File(DATA_PATH + "tessdata/" + lang + ".traineddata");
		if (!trainData.exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/eng.traineddata");
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/eng.traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}
		_path = new String(DATA_PATH + "ocr.jpg");
		imageFile = new File(_path);
	}

	public void rotatePhoto() {
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = 1;
		Bitmap photo = BitmapFactory.decodeFile(_path, bitmapOptions);
		
		// added by charles
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		int height = display.getHeight();

		photo = Bitmap.createScaledBitmap(photo, width, height, true);
		
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(_path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (exif != null) {
			Log.e(TAG, "This" + exif.getAttribute(ExifInterface.TAG_ORIENTATION));
			if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
				photo = rotate(photo, 90);
			}else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
				photo = rotate(photo, 270);
			}else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
				photo = rotate(photo, 180);
			} else {
				Log.e(TAG, "Rotating second 90");
				photo = rotate(photo, 0);
			}
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
			photo.compress(CompressFormat.PNG, 0, outputStream);
			mImage = outputStream.toByteArray();										
			photo.recycle();
		}
		else{
			Log.e(TAG, "EXIF not found");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){  
		switch (requestCode) {
		case CAMERA_REQUEST:
			if (resultCode == Activity.RESULT_CANCELED) {
				Log.d(ACTIVITY_SERVICE, "Camera activity cancelled");
			}
			else if (resultCode == Activity.RESULT_OK) {
				Log.v(TAG, "Photo accepted. Converting to bitmap.");
				/*TODO: Make this background */
				SomeTask task = new SomeTask();
				Log.v(TAG, "Calling background thread");
				task.execute();
				Log.v(TAG, "Calling launch selection");
				
				/*
				try{					
					rotatePhoto();
					
				}catch(Exception e){
					showErrorMessage("Error", "Decoding Bitmap Error.");				
				}
				
				launchSelection();
				*/
			}
			break;
		case IMAGE_SELECTION:
			Log.v(TAG, " in IMAGE_SELECTION");
			if (resultCode == Activity.RESULT_OK) {
				try{
					Bundle extras = data.getExtras();
					ocrAmount = extras.getString("ocr-amount");
					if (ocrAmount.startsWith("$")) {
						ocrAmount = ocrAmount.substring(1, ocrAmount.length());
					}
					ocrDesc = extras.getString("ocr-desc");
					loadForm();
				}catch(Exception ex){
					showErrorMessage("Error", "OCR result not ready.");
				}
			}
			break;
		}
	}
	
	/**
	 * Added by charles 11/18
	 * @author charles
	 * ClickListener for the save button
	 */
	class StorageOptions implements DialogInterface.OnClickListener {

		private ReceiptEntryActivity a;
		private boolean cloud = false;
		
		public StorageOptions(ReceiptEntryActivity a, boolean cloud) {
			this.a = a;
			this.cloud = cloud;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			a.setCloudStorage(cloud);
			if (cloud) {
				if (!mApi.getSession().isLinked()) {
					mApi.getSession().startAuthentication(ReceiptEntryActivity.this);
					linking = true;
				} else {
					cloudStore();
				}
			} else {
				cloudStore();
			}
		}
		
	}
	
    /** CIS599
     * Yiran Qin
     */
	public static Bitmap rotate(Bitmap bitmap, int degree) {
	    int w = bitmap.getWidth();
	    int h = bitmap.getHeight();

	    Matrix mtx = new Matrix();
	    mtx.postRotate(degree, w / 2, h / 2);
	    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
	    Log.v("map", "a" + w + h);
	    
	    Bitmap result = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), mtx, true);
	    Log.v("map", "b" + result.getWidth() + result.getHeight());
	    return result;
	}

	private void launchSelection() {
		try{
			Intent intent = new Intent(getApplicationContext(), ImageOCRActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.putExtra("image", mImage);
			intent.putExtra("mode", 0);
			Log.d(TAG, "Launching ImageOCRActivity");
			startActivityForResult(intent, IMAGE_SELECTION);
		}catch(Exception e){
			showErrorMessage("Error", "Intent builidng error.");
		}
	}


	
	
	
	private void loadForm() {
		Log.d(TAG,"loading form");
		setContentView(R.layout.receipt_entry);
		mDescriptionText = (EditText) findViewById(R.id.description);
		mDescriptionText.setText(ocrDesc);
		mAmountText = (EditText) findViewById(id.amount);
		mAmountText.setText(ocrAmount);
		mDateText = (EditText) findViewById(id.date);
		mPayment = (RadioGroup) findViewById(id.payment);
		Log.d(TAG,"form display");
		mRecurring = (CheckBox) findViewById(id.check_recurring);
		//added by charles 11/18
		mSave = (Button) findViewById(id.save);
		mSave.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// added by charles 11/18
				if (mImage != null && mImage.length != 0) {
					AlertDialog.Builder alert = new AlertDialog.Builder(ReceiptEntryActivity.this);
					alert.setTitle("Store options");
					alert.setMessage("Would you like to store the photo on dropbox?");
					alert.setPositiveButton("Yes", new StorageOptions(ReceiptEntryActivity.this, true));
					alert.setNegativeButton("No", new StorageOptions(ReceiptEntryActivity.this, false));
					alert.show();
				} else {
					cloudStore();
				}
			}
		});

		Log.d(TAG,"done form display");
		populateSpinner();
 
		Log.d(TAG,"load form- populating spinner");

		//Set Date
		mDateText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		//Get current Date
		final Calendar cal = Calendar.getInstance();
		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH);
		mDay = cal.get(Calendar.DAY_OF_MONTH);
		mDate = cal.getTime();
		mDateText.setText(new SimpleDateFormat("MM/dd/yy").format(mDate).toString());
		Log.d(TAG,"load form- going to use mDbHelper");
		setPaymentMethod(mDbHelper.getMostlyUsedPayment());
		Log.d(TAG,"laod form- used mDbHelper with success");
	}
	
	private void loadFormIncome() {
		Log.d(TAG,"loading income form");
		setContentView(R.layout.income);
		Log.d(TAG,"The layout for income is loaded.");
		//mDescriptionText = (EditText) findViewById(R.id.description);
		//mDescriptionText.setText("");
		mAmountText = (EditText) findViewById(id.amount);
		mDateText = (EditText) findViewById(id.date);
		mPayment = (RadioGroup) findViewById(id.payment);
		Log.d(TAG,"form display");
		mRecurring = (CheckBox) findViewById(id.check_recurring);
		//added by charles 11/18
		mSave = (Button) findViewById(id.save);
		mSave.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// added by charles 11/18
				if (mImage != null && mImage.length != 0) {
					AlertDialog.Builder alert = new AlertDialog.Builder(ReceiptEntryActivity.this);
					alert.setTitle("Store options");
					alert.setMessage("Would you like to store the photo on dropbox?");
					alert.setPositiveButton("Yes", new StorageOptions(ReceiptEntryActivity.this, true));
					alert.setNegativeButton("No", new StorageOptions(ReceiptEntryActivity.this, false));
					alert.show();
				} else {
					cloudStore();
				}
			}
		});

		Log.d(TAG,"done form display");
		populateSpinnerIncome();
		Log.d(TAG,"laod form- populating spinner");
		//Set Date
		mDateText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		//Get current Date
		final Calendar cal = Calendar.getInstance();
		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH);
		mDay = cal.get(Calendar.DAY_OF_MONTH);
		mDate = cal.getTime();
		mDateText.setText(new SimpleDateFormat("MM/dd/yy").format(mDate).toString());
		Log.d(TAG,"laod form- going to use mDbHelper");
		setPaymentMethod(mDbHelper.getMostlyUsedPayment());
		Log.d(TAG,"laod form- used mDbHelper with success");
	}

	private void populateSpinner() {
		ArrayList<String> sortedList = mDbHelper.sortByCategory();
		ArrayList<String> temp = new ArrayList<String>();
		
		String matchingCategory = mDbHelper.findMatchingCategory(mDescriptionText.getText().toString());
		if(matchingCategory != null){
			temp.add(matchingCategory);
			for(String category : sortedList){
				if(!category.equals(matchingCategory))
					temp.add(category);
			}
		}
		else
			temp.addAll(sortedList);
		
		for(String category : categoryList){
			if(categoryList.contains(category) && !temp.contains(category))
				temp.add(category);
		}
		categoryList = temp;
		String[] items = categoryList.toArray(new String[categoryList.size()]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mCategoryText = (Spinner) findViewById(R.id.category);
		mCategoryText.setAdapter(adapter);
	}
	
	
	@SuppressLint("SimpleDateFormat")
	private void populateSpinnerIncome() {
		Log.e(TAG,"Database has been accessed - income");
		
		String[] items = categoryListIncome.toArray(new String[categoryListIncome.size()]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mCategoryText = (Spinner) findViewById(R.id.category);
		mCategoryText.setAdapter(adapter);
	}

	private void updateDate() {
		Log.d(TAG, "Update Date");
		Calendar cal = new GregorianCalendar();
		cal.set(mYear, mMonth, mDay);
		mDate = cal.getTime();
		Log.d(TAG, String.valueOf(mDate.getYear()));
		Log.d(TAG, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(mDate).toString());
		mDateText.setText(new SimpleDateFormat("MM/dd/yy").format(mDate).toString());
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener =
			new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDate();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		}
		return null;
	}

	public void onClearButtonClick(View view) {
		mDescriptionText.setText("");
		mAmountText.setText("");
		mDateText.setText("");
		mCategoryText.setSelection(0);
		mPayment.clearCheck();
		mRecurring.setChecked(true);
	}

	// modified by charles 11/18
	public void onSaveButtonClick() {
		if(! isOthersCategory()){
			saveState(null);
			setResult(RESULT_OK);
			finish();
		}
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		mDbHelper.close();
		super.onDestroy();
	}

	private boolean isOthersCategory(){
		int categoryIndex = mCategoryText.getSelectedItemPosition();
		String category = categoryList.get(categoryIndex);	
		
		if(category.equalsIgnoreCase("Others")){
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Others Category");
			alert.setMessage("Add a new category?");

			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newCategory = input.getText().toString();
				categoryList.add(newCategory);
				saveState(newCategory);
				setResult(RESULT_OK);
				finish();
			}
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
				  saveState(null);
				  setResult(RESULT_OK);
				  finish();
			  }
			});

			alert.show();
			return true;
		}
		
		return false;
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        if (!linking) {}
        else cloudStore();
	}
	
	public void cloudStore() {
		AndroidAuthSession session = mApi.getSession();
        
		if (session.isLinked()) {}
		else if (session.authenticationSuccessful()) {
        	try {
        		// Mandatory call to complete the auth
        		session.finishAuthentication();
        		
        		// Store it locally in our app for later use
        		TokenPair tokens = session.getAccessTokenPair();
        		storeKeys(tokens.key, tokens.secret);
        		
                //setLoggedIn(true);

            } catch (IllegalStateException e) {
            	//showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
        String dateString = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(mDate);
		dateString = dateString.replace(" ", "");
		dateString = dateString.replace(":", "");
		dateString = dateString.replace("-", "");
		dateString = dateString + ".jpg";
    	
		File file = new File(DATA_PATH + dateString);
		
		FileOutputStream fw;
		try {
			fw = new FileOutputStream(file.getAbsoluteFile());
			BufferedOutputStream bw = new BufferedOutputStream(fw);

			bw.write(mImage);
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (cloudStorage) {
			upload = new SyncToDropbox(ReceiptEntryActivity.this, mApi, "/", file);
			upload.execute();
			try {
				upload.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		onSaveButtonClick();
		linking = false;
	}
	
	private void saveState(String newCat) {

		try {
			Log.d(ACTIVITY_SERVICE, "Made it to saveState()");
			String description = mDescriptionText.getText().toString();
			double amount = Double.parseDouble(mAmountText.getText().toString());
			int payment = getPaymentMethod().getValue();
			int categoryIndex = mCategoryText.getSelectedItemPosition();
			String category = (newCat != null) ? newCat : categoryList.get(categoryIndex);	
			boolean recurring = mRecurring.isChecked();
			
			if (mRowId == null) {
				Log.d(ACTIVITY_SERVICE, "mRowId == null");

				// added by charles 11/18 11.20
				long id = 0;
				if (this.cloudStorage) {
					id = mDbHelper.createReceipt(description, amount, mDate, category, payment, recurring, mImage, this.cloudStorage);
					//Toast.makeText(this, "CloudStorageNew", Toast.LENGTH_SHORT).show();
					this.cloudStorage = false;
				} else {
					id = mDbHelper.createReceipt(description, amount, mDate, category, payment, recurring, mImage, this.cloudStorage);
					//Toast.makeText(this, "NoCloudStorageNew", Toast.LENGTH_SHORT).show();
				}
				
				// commented out by charles 11/18
				//long id = mDbHelper.createReceipt(description, amount, mDate, category, payment, mImage);
				if (id > 0) {
					mRowId = id;
				}
				Toast.makeText(this, "Receipt successfully added", Toast.LENGTH_SHORT).show();
			} else {
				Log.d(ACTIVITY_SERVICE, "mRowId != null");
				// added by charles 11/18 11.20
				if (this.cloudStorage) {
					mDbHelper.updateReceipt(mRowId, description, amount, mDate, category, payment, recurring,  mImage, this.cloudStorage);
					this.cloudStorage = false;
					Toast.makeText(this, "CloudStorageOld", Toast.LENGTH_SHORT).show();
				} else {
					mDbHelper.updateReceipt(mRowId, description, amount, mDate, category, payment, recurring, mImage, this.cloudStorage);
					Toast.makeText(this, "NoCloudStorageOld", Toast.LENGTH_SHORT).show();
				}
				
				// commented out by charles 11/18
				//mDbHelper.updateReceipt(mRowId, description, amount, mDate, category, payment, mImage);
				Toast.makeText(this, "Receipt successfully updated", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Log.d(ACTIVITY_SERVICE, "Exception occured");
			if (e.getClass().equals(ParseException.class)) {
				Log.d(ACTIVITY_SERVICE, "Parse Exception");
				showErrorMessage("Error: Invalid Date", "Please enter date in correct format (MM/dd/yy) or use the date picker to select a valid date.");
			}
			else if (e.getClass().equals(NumberFormatException.class)) {
				Log.d(ACTIVITY_SERVICE, "Number Format Exception");
				showErrorMessage("Error: Invalid Amount", "Please enter a valid number for the amount.");
			}
		}
	}

	private PaymentType getPaymentMethod() {
		RadioButton b1 = (RadioButton) findViewById(R.id.radio_cash);
		RadioButton b2 = (RadioButton) findViewById(R.id.radio_credit);
		RadioButton b3 = (RadioButton) findViewById(R.id.radio_debit);
		if (b1.isChecked()) {
			return PaymentType.CASH;
		}
		else if (b2.isChecked()) {
			return PaymentType.CREDIT;
		}
		else if (b3.isChecked()) {
			return PaymentType.DEBIT;
		}
		return PaymentType.CASH;
	}
	
	private void setPaymentMethod(String type) {
		RadioButton b1 = (RadioButton) findViewById(R.id.radio_cash);
		RadioButton b2 = (RadioButton) findViewById(R.id.radio_credit);
		RadioButton b3 = (RadioButton) findViewById(R.id.radio_debit);
		if (type.equalsIgnoreCase(PaymentType.CASH.getText())) {
			b1.setChecked(true);
		}
		else if(type.equalsIgnoreCase(PaymentType.CREDIT.getText())){
			b2.setChecked(true);
		}
		else if(type.equalsIgnoreCase(PaymentType.DEBIT.getText())) {
			b3.setChecked(true);
		}
	}

	public boolean isCloudStorage() {
		return cloudStorage;
	}

	public void setCloudStorage(boolean cloudStorage) {
		this.cloudStorage = cloudStorage;
	}

	
	/** Inner class for implementing progress bar before fetching data **/
	private class SomeTask extends AsyncTask<Void, Void, Integer> 
	{
	    private ProgressDialog Dialog = new ProgressDialog(ReceiptEntryActivity.this);

	    @Override
	    protected void onPreExecute()
	    {
	    	
	        Dialog.setMessage("Saving image...");
	        Dialog.show();
	    }

	    @Override
	    protected Integer doInBackground(Void... params) 
	    {
	        //Task for doing something 
	    	try{					
				rotatePhoto();
				
			}catch(Exception e){
				showErrorMessage("Error", "Decoding Bitmap Error.");				
			}
			
			launchSelection();
	        return 0;
	    }

	    @Override
	    protected void onPostExecute(Integer result)
	    {

	        if(result==0)
	        {
	        	Log.v(TAG,"Success for spinner");  //do some thing
	        }
	        // after completed finished the progressbar
	        Dialog.dismiss();
	    }
	}
}
