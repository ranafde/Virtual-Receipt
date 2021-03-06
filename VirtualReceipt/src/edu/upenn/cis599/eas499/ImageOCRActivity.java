/**
 * Copyright 2012 Annie Lee. All Rights Reserved.
 */

package edu.upenn.cis599.eas499;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import edu.upenn.cis599.R;


public class ImageOCRActivity extends Activity {

	private static final String TAG = "ImageOCRActivity.java";
	private static final String WHITELIST = "!?@#$%&*()<>_-+=/.,:;'\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	private static int MODE;
	public static final String lang = "eng";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/VirtualReceipt/";
	public static final String _path = DATA_PATH + "ocr.jpg";
	public static final String _viewpath = DATA_PATH + "viewImg.jpg";

	private Bitmap mBitmap;
	private MyView mView;
	private boolean doneClicked = false;
	private TessBaseAPI baseApi;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		doneClicked = false;
		
		// Setting up Tesseract
		Log.d(TAG, "setting up Tesseract");
		if(baseApi == null){
			Log.d(TAG, "initialized Tesseract");
			baseApi = new TessBaseAPI();
			Log.d(TAG, "initialized Tesseract okay!!!");
			baseApi.setDebug(true);
			baseApi.init(DATA_PATH, lang);
		}
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR | ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Log.d(TAG, "Tesseract set!!!");
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
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
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
		
		Bundle extras = getIntent().getExtras();
		MODE = extras.getInt("mode");
		byte[] imageByteArray = null;
		/* MODE = 0, ImageOCR is launched from ReceiptEntryActivity */
		/* MODE = 1, ImageOCR is launched from ReceiptViewActivity */
		String imageFromPath;
		
		if(MODE == 0){
			imageFromPath = _path;
		}
		else {
			imageFromPath = _viewpath;
		}
		
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		
		bitmapOptions.inSampleSize = 1;
		Bitmap photo = BitmapFactory.decodeFile(imageFromPath, bitmapOptions);
		
		// added by charles, scale the bitmap to screen size
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		int height = display.getHeight();
		
		/* position of heigt and width in function call was incorrect */
		photo = Bitmap.createScaledBitmap(photo, width, height, true); 
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
		photo.compress(CompressFormat.PNG, 0, outputStream);
		photo.recycle();
		imageByteArray = outputStream.toByteArray();
		
		if (imageByteArray != null) {
			Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
			mBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
			if (mBitmap.getWidth() > mBitmap.getHeight()) {
				Matrix m = new Matrix();
				mBitmap = Bitmap.createBitmap(mBitmap , 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, true)
								.copy(Bitmap.Config.ARGB_8888, true);
			}
			imageBitmap.recycle();
		}
		else {
			mBitmap = Bitmap.createBitmap(100, 200, Config.RGB_565);
		}

		if (MODE == 0) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Select Total Amount and Description");
			alert.setMessage("Please select the region for the total amount first and then select region for description.");
			alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					selectRegion();
				}
			});
			alert.show();
		}
		else if (MODE == 1) {
			selectRegion();
		}
	}

	
	private void selectRegion() {
		setContentView(R.layout.image_ocr_view);
		mView = (MyView) findViewById(R.id.myview);
		mView.setBitmapData(mBitmap);
	}

	public void onReselectButtonClick(View view) {
		if(mView != null){
			setContentView(R.layout.image_ocr_view);
			mView = (MyView) findViewById(R.id.myview);
			mView.setBitmapData(mBitmap);
			mView.reset();
		}
	}
	
	public void onDoneButtonClick(View view) throws Exception{
		doneClicked = !doneClicked;
		
		if(doneClicked){
			Rect amountRect = mView.getAmountRect();
			Rect descRect = mView.getDescRect();
			if (amountRect != null) {
				Log.v(TAG, "Before baseApi");

				baseApi.setImage(mBitmap);
				Log.v(TAG, "Bit map set");
				baseApi.setVariable("tessedit_char_whitelist", "0123456789.$");
				baseApi.setRectangle(amountRect);
				Log.v(TAG, "Amount rect set");
	
				String recognizedAmount = baseApi.getUTF8Text();
				Log.v(TAG, "read the amount");
				
				if(recognizedAmount == null || recognizedAmount.equals(""))
					Log.e("result", "can't recognize");
				
				baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, WHITELIST);
				baseApi.setRectangle(descRect);
				String recognizedDesc = baseApi.getUTF8Text();
				if(recognizedDesc == null || recognizedAmount.equals(""))
					Log.e("result", "can't recognize");
				
				baseApi.clear();
				baseApi.end();
				
				
				Log.v(TAG, "OCRED TEXT: " + recognizedAmount);
				Log.v(TAG, "OCRED TEXT: " + recognizedDesc);
	
				recognizedAmount = recognizedAmount.trim();
				recognizedDesc = recognizedDesc.trim();
				
	
				switch (MODE) {
					// When called from receipt entry
					case 0:
						//mBitmap.recycle();  /* A bit map can not be recycled twice. This was causing the app to crash */
						Intent resultIntent = new Intent();
						resultIntent.putExtra("ocr-amount", recognizedAmount);
						resultIntent.putExtra("ocr-desc", recognizedDesc);
						setResult(Activity.RESULT_OK, resultIntent);
						finish();
						break;
						// When called from receipt view
					case 1:
						String recognizedText = "Amount:" + recognizedAmount + " Desc:" + recognizedDesc;
						Toast toast = Toast.makeText(this, recognizedText, Toast.LENGTH_LONG);
						toast.show();
						finish();
						break;
				}
			}
		}
	}

}
