package edu.upenn.cis599.eas499;

import java.util.Calendar;

import edu.upenn.cis599.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class NotifyAlarm extends BroadcastReceiver {

	 NotificationManager nm;
	 private ReceiptDbAdapter mDbHelper;
	 private static final String TAG = "NotifyAlarm.java";

	 @Override
	 public void onReceive(Context context, Intent intent) {
		 Log.d(TAG,"received alarm signal");
       mDbHelper = new ReceiptDbAdapter(context);
       mDbHelper.open();
       int month = getMonthIndex();
       if (mDbHelper.retrieveMonthlyPayment(1)[month] > mDbHelper.retrieveMonthlyBudget(1)[month] && mDbHelper.retrieveMonthlyBudget(1)[month] > 0) {
    	   showNotification(context,intent);
       }
       mDbHelper.close();
	 }
	 
	 public void showNotification(Context context, Intent intent){
		 
		 NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.icon2).setContentTitle("Virtual Receipt")
                 .setContentText("Alert! Monthly Expense > Budget");
		 Intent resultIntent = new Intent(context, VirtualReceiptActivity.class);

	        // The stack builder object will contain an artificial back stack for the
	        // started Activity.
	        // This ensures that navigating backward from the Activity leads out of
	        // your application to the Home screen.
	        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
	        // Adds the back stack for the Intent (but not the Intent itself)
	        stackBuilder.addParentStack(VirtualReceiptActivity.class);
	        // Adds the Intent that starts the Activity to the top of the stack
	        stackBuilder.addNextIntent(resultIntent);
	        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	        notificationBuilder.setContentIntent(resultPendingIntent);
	        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	        mNotificationManager.notify(1, notificationBuilder.build());
	 }
	 
	    public static int getMonthIndex() {
	    	Calendar cal = Calendar.getInstance();
			return cal.get(Calendar.MONTH);
	    }
	}