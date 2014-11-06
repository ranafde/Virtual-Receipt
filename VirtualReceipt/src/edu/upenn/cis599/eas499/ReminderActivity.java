package edu.upenn.cis599.eas499;

import java.util.Calendar;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import edu.upenn.cis599.R;
import edu.upenn.cis599.R.id;
import edu.upenn.cis599.eas499.ReceiptEntryActivity.StorageOptions;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class ReminderActivity extends Activity {
	private Spinner mFrequencySpinner;
	private EditText mEventName;
	private DatePicker mStartDate;
	private DatePicker mEndDate;
	private TimePicker mStartTime;
	private TimePicker mEndTime;
	private CheckBox mAllDay;
	private Button mSave;
	private Button mCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminder_view);
		populateSpinnerValues();
		mEventName = (EditText) findViewById(R.id.rmndrEventNameField);
		mStartDate = (DatePicker) findViewById(R.id.rmndrStartDateField);
		mEndDate = (DatePicker) findViewById(R.id.rmndrEndDateField);
		mStartTime = (TimePicker) findViewById(R.id.rmndrStartTimeField);
		mEndTime = (TimePicker) findViewById(R.id.rmndrEndTimeField);
		mSave = (Button) findViewById(R.id.rmndrSave);
		mCancel = (Button) findViewById(R.id.rmndrCancel);
		mAllDay = (CheckBox) findViewById(R.id.rmndrAllDayField);
		Log.d("ReminderActivity", "done form display");

	}

	public void onSaveReminderClick(View view) {
		Log.d("ReminderActivity", mEventName.getText().toString());
		Log.d("ReminderActivity", Boolean.toString(mAllDay.isEnabled()));
		Log.d("ReminderActivity",
				mStartDate.getYear() + "-" + mStartDate.getMonth() + "-"
						+ mStartDate.getDayOfMonth());
		Log.d("ReminderActivity",
				mEndDate.getYear() + "-" + mEndDate.getMonth() + "-"
						+ mEndDate.getDayOfMonth());
		Log.d("ReminderActivity", mStartTime.getCurrentHour() + ":"
				+ mStartTime.getCurrentMinute());
		Log.d("ReminderActivity", mFrequencySpinner.getSelectedItem().toString());
		Calendar calendar = Calendar.getInstance();
		calendar.set(mStartDate.getYear(), mStartDate.getMonth(), mStartDate.getDayOfMonth(), 
		             mStartTime.getCurrentHour(), mStartTime.getCurrentMinute(), 0);
		long startTime = calendar.getTimeInMillis();
		Calendar calendar1 = Calendar.getInstance();
		calendar1.set(mEndDate.getYear(), mEndDate.getMonth(), mEndDate.getDayOfMonth(), 
		             mEndTime.getCurrentHour(), mEndTime.getCurrentMinute(), 0);
		long endTime = calendar1.getTimeInMillis();
		String frequency = "FREQ="+mFrequencySpinner.getSelectedItem().toString().toUpperCase();
		Calendar cal = Calendar.getInstance();              
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", startTime);
		intent.putExtra("allDay", mAllDay.isChecked());
		intent.putExtra("rrule", frequency);
		intent.putExtra("endTime", endTime);
		intent.putExtra("title", mEventName.getText().toString());
		startActivity(intent);
		setResult(RESULT_OK);
		finish();
	}

	public void onCancelReminderClick(View view) {
		Log.d("ReminderActivity", "Pressed Cancel");
		setResult(RESULT_OK);
		finish();
	}

	private void populateSpinnerValues() {
		String[] items = new String[] { "Daily", "Weekly", "Monthly" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mFrequencySpinner = (Spinner) findViewById(R.id.rmndrFrequencyField);
		mFrequencySpinner.setAdapter(adapter);
	}

}
