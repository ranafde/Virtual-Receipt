/**
 * Copyright 2012 Annie Lee. All Rights Reserved.
 */

package edu.upenn.cis599.eas499;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import edu.upenn.cis599.R;
//import edu.upenn.cis599.charts.BarGraphIncomeExpenditure;
import edu.upenn.cis599.charts.PieChartCategory;
import edu.upenn.cis599.charts.PieChartPayment;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * 
 * @author Yiran Qin Whole logic of this activity has been changed to hold
 *         graphical view rendered by the AChartEngine API wrap the view into a
 *         linear layout and animate the layout changing based on the choice of
 *         current month, current year and all time
 * 
 */

public class ChartViewerActivity extends Activity {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private static final int CATEGORY = 1;

	private static final int CURRENT_MONTH = 0;
	private static final int CURRENT_YEAR = 1;
	private static final int ALL_TIME = 2;

	private static final String TAG = "ChartViewerActivity.java";

	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	private Animation slideIn;
	private Animation slideOut;
	private Animation slideInL;
	private Animation slideOutL;

	ViewFlipper flipper;

	private PieChartCategory cmChart = null;
	private PieChartCategory cyChart = null;
	private PieChartCategory atChart = null;

	private String storageDirectory = Environment.getExternalStorageDirectory()
			.toString() + "/VirtualReceipt/";

	private int selection;
	LinearLayout currentMonthGraph;
	LinearLayout currentYearGraph;
	LinearLayout allTimeGraph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_view);
		Bundle extras = getIntent().getExtras();
		selection = extras.getInt("selection");
		Log.v(TAG, "selection is:" + selection);

		setCharts(selection);
	}

	private void setCharts(int selection) {
		currentMonthGraph = (LinearLayout) findViewById(R.id.current_month);
		currentYearGraph = (LinearLayout) findViewById(R.id.current_year);
		allTimeGraph = (LinearLayout) findViewById(R.id.all_time);
		if (selection == 1) {
			cmChart = new PieChartCategory();
			View cmChartView = cmChart.getCategoryView(this, CURRENT_MONTH);
			currentMonthGraph.addView(cmChartView,
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));

			cyChart = new PieChartCategory();
			View cyChartView = cyChart.getCategoryView(this, CURRENT_YEAR);
			currentYearGraph.addView(cyChartView,
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));

			atChart = new PieChartCategory();
			View atChartView = atChart.getCategoryView(this, ALL_TIME);
			allTimeGraph.addView(atChartView, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));

		} else if (selection == 3) {
			PieChartPayment cmChart = new PieChartPayment();
			View cmChartView = cmChart.getPaymentView(this, CURRENT_MONTH);
			currentMonthGraph.addView(cmChartView,
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));

			PieChartPayment cyChart = new PieChartPayment();
			View cyChartView = cyChart.getPaymentView(this, CURRENT_YEAR);
			currentYearGraph.addView(cyChartView,
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));

			PieChartPayment atChart = new PieChartPayment();
			View atChartView = atChart.getPaymentView(this, ALL_TIME);
			allTimeGraph.addView(atChartView, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}

		/*
		 * else{ Log.v(TAG,"bar graph"); BarGraphIncomeExpenditure barGraph =
		 * new BarGraphIncomeExpenditure(); View barGraphView =
		 * barGraph.getView(); /*View barGraphView =
		 * ChartFactory.getBarChartIntent(context, buildDataset(titles, months,
		 * monthlyStatistics), renderer, BarChart.Type.DEFAULT);
		 * currentYearGraph.addView(barGraphView, new LinearLayout.LayoutParams
		 * (LinearLayout.LayoutParams.WRAP_CONTENT,
		 * LinearLayout.LayoutParams.WRAP_CONTENT));
		 * 
		 * }
		 */
		/*
		 * Annie's way to create charts ImageView cGraph = (ImageView)
		 * findViewById(R.id.c_graph_img); String mUrl = getCategoryChartUrl();
		 * Drawable image = fetchImage(this, mUrl, "category_graph.jpg");
		 * cGraph.setImageDrawable(image);
		 * 
		 * ImageView pGraph = (ImageView) findViewById(R.id.p_graph_img); mUrl =
		 * getPaymentChartUrl(); image = fetchImage(this, mUrl,
		 * "payment_graph.jpg"); pGraph.setImageDrawable(image);
		 * 
		 * mUrl = getMonthChartUrl(); image = fetchImage(this, mUrl,
		 * "date_graph.jpg");
		 */

		flipper = (ViewFlipper) findViewById(R.id.view_flipper);
		slideIn = AnimationUtils.loadAnimation(this, R.anim.animation_enter);
		slideOut = AnimationUtils.loadAnimation(this, R.anim.animation_leave);
		slideInL = AnimationUtils.loadAnimation(this, R.anim.animation_enter_l);
		slideOutL = AnimationUtils
				.loadAnimation(this, R.anim.animation_leave_l);

		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};
		currentMonthGraph.setOnTouchListener(gestureListener);
		currentYearGraph.setOnTouchListener(gestureListener);
		allTimeGraph.setOnTouchListener(gestureListener);

	}

	public void exportPdf(View view) {
		Log.d(TAG, Environment.getExternalStorageDirectory().toString()
				+ "/VirtualReceipt/");
		String[] imageFiles = { "Category_Spending_Current_Month_Graph",
				"Category_Spending_Current_Year_Graph",
				"Category_Spending_All_Time_Graph",
				"Payment_Type_Current_Month_Graph",
				"Payment_Type_Current_Year_Graph",
				"Payment_Type_All_Time_Graph" };
		if (selection == 1) {
			// setCharts(1);
			currentMonthGraph = (LinearLayout) findViewById(R.id.current_month);
			currentYearGraph = (LinearLayout) findViewById(R.id.current_year);
			allTimeGraph = (LinearLayout) findViewById(R.id.all_time);
			if (convertGraphToImage(allTimeGraph, imageFiles[2]) != null)
				return;
			else if (convertGraphToImage(currentYearGraph, imageFiles[1]) != null)
				return;
			else if (convertGraphToImage(currentMonthGraph, imageFiles[0]) != null)
				return;
		} else if (selection == 3) {
			// setCharts(3);
			currentMonthGraph = (LinearLayout) findViewById(R.id.current_month);
			currentYearGraph = (LinearLayout) findViewById(R.id.current_year);
			allTimeGraph = (LinearLayout) findViewById(R.id.all_time);
			if (convertGraphToImage(allTimeGraph, imageFiles[5]) != null)
				return;
			else if (convertGraphToImage(currentYearGraph, imageFiles[4]) != null)
				return;
			else if (convertGraphToImage(currentMonthGraph, imageFiles[3]) != null)
				return;
		}

	}

	private String convertGraphToImage(LinearLayout graph, String fileName) {
		String imageFile = storageDirectory + fileName + ".png";
		Bitmap bitmap;
		graph.setDrawingCacheEnabled(true);
		try {
			bitmap = Bitmap.createBitmap(graph.getDrawingCache());
			bitmap.compress(Bitmap.CompressFormat.PNG, 100,
					new FileOutputStream(new File(imageFile)));
		} catch (Exception e) { // TODO Auto-generated catch block
			return null;
		}
		graph.setDrawingCacheEnabled(false);
		Toast.makeText(getApplicationContext(), "Saved PDF file on the phone", Toast.LENGTH_LONG).show();
		return "success";

	}

/*	private void addImagesToPdf(String imageFiles) {
		String pdfFile = storageDirectory + "report_" + imageFiles + ".pdf";
		Rectangle layout = new Rectangle(PageSize.A4);
		layout.setBackgroundColor(BaseColor.BLACK);
		Document document = new Document(layout);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
			document.open();
			Log.d("image names", imageFiles);

			Image image = Image.getInstance(storageDirectory + imageFiles
					+ ".png");
			image.scalePercent(60f);
			document.add(image);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			document.close();
			Toast.makeText(getApplicationContext(), "Saved PDF file on the phone", Toast.LENGTH_LONG).show();
		}
		//deleteFile(storageDirectory + imageFiles + ".png");
		File file = new File(storageDirectory + imageFiles + ".png");
		boolean deleted = file.delete();
	}
*/
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
					Log.d(ACTIVITY_SERVICE, "no motion");
					return false;
					// right to left swipe
				} else if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					flipper.setInAnimation(slideInL);
					flipper.setOutAnimation(slideOutL);
					flipper.showNext();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					flipper.setInAnimation(slideIn);
					flipper.setOutAnimation(slideOut);
					flipper.showPrevious();
				}
			} catch (Exception e) {
				Log.d(ACTIVITY_SERVICE, "motion exception occured");
				// nothing
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	}

	/**
	 * Annie's way to create the views
	 */
	/*
	 * private Drawable fetchImage(Context ctx, String address, String
	 * saveFilename) { try { URL url = new URL(address); InputStream is =
	 * (InputStream) url.getContent(); Drawable d =
	 * Drawable.createFromStream(is, "src"); return d; } catch
	 * (MalformedURLException e) { Log.e(DOWNLOAD_SERVICE,
	 * "MalformedURLException occured while loading image"); return null; }
	 * catch (IOException e) { Log.e(DOWNLOAD_SERVICE,
	 * "IOException occured while loading image"); return null; } }
	 * 
	 * private String getCategoryChartUrl() { Cursor c = mDbHelper.rawQuery(
	 * "select name, sum(amount) as sum from receipt r, category c where c._id=r.category group by category"
	 * ); HashMap<String, String> map = new HashMap<String, String>(); if (c !=
	 * null) { if (c.moveToFirst()) { do { String category =
	 * c.getString(c.getColumnIndexOrThrow(ReceiptDbAdapter.KEY_NAME)); String
	 * categorySum = c.getString(c.getColumnIndexOrThrow("sum"));
	 * map.put(category, categorySum); } while (c.moveToNext()); } } //Create
	 * string builder for category sums StringBuilder chartData = new
	 * StringBuilder(); chartData.append("chd=t:"); for (String categorySum :
	 * map.values()) { chartData.append(categorySum + ","); }
	 * chartData.deleteCharAt(chartData.length()-1); //Create string builder for
	 * category labels StringBuilder chartLabels = new StringBuilder();
	 * chartLabels.append("chl="); for (String category : map.keySet()) {
	 * chartLabels.append(category + "|"); }
	 * chartLabels.deleteCharAt(chartLabels.length()-1); //Create string builder
	 * for category legend StringBuilder chartLegend = new StringBuilder();
	 * chartLegend.append("chdl="); for (String categorySum : map.values()) {
	 * chartLegend.append("$" + categorySum + "|"); }
	 * chartLegend.deleteCharAt(chartLegend.length()-1); //Create string builder
	 * for final url StringBuilder url = new StringBuilder();
	 * url.append("http://chart.googleapis.com/chart?"); url.append("cht=p3");
	 * url.append("&chs=600x300"); url.append("&" + chartData.toString());
	 * url.append("&" + chartLabels.toString()); url.append("&" +
	 * chartLegend.toString()); url.append("&chdlp=b&chdls=000000,18");
	 * Log.d(ACTIVITY_SERVICE, url.toString()); return url.toString(); }
	 * 
	 * private String getPaymentChartUrl() { Cursor c = mDbHelper.rawQuery(
	 * "select payment, sum(amount) as sum from receipt group by payment");
	 * HashMap<String, String> map = new HashMap<String, String>(); if (c !=
	 * null) { if (c.moveToFirst()) { do { int paymentIndex =
	 * Integer.valueOf(c.getString
	 * (c.getColumnIndexOrThrow(ReceiptDbAdapter.KEY_PAYMENT))); String
	 * paymentType = PaymentType.get(paymentIndex).getText(); String paymentSum
	 * = c.getString(c.getColumnIndexOrThrow("sum")); map.put(paymentType,
	 * paymentSum); } while (c.moveToNext()); } } //Create string builder for
	 * payment type sums StringBuilder chartData = new StringBuilder();
	 * chartData.append("chd=t:"); for (String paymentSum : map.values()) {
	 * chartData.append(paymentSum + ","); }
	 * chartData.deleteCharAt(chartData.length()-1); //Create string builder for
	 * payment type labels StringBuilder chartLabels = new StringBuilder();
	 * chartLabels.append("chl="); for (String paymentType : map.keySet()) {
	 * chartLabels.append(paymentType + "|"); }
	 * chartLabels.deleteCharAt(chartLabels.length()-1); //Create string builder
	 * for payment type legend StringBuilder chartLegend = new StringBuilder();
	 * chartLegend.append("chdl="); for (String paymentSum : map.values()) {
	 * chartLegend.append("$" + paymentSum + "|"); }
	 * chartLegend.deleteCharAt(chartLegend.length()-1); //Create string builder
	 * for final url StringBuilder url = new StringBuilder();
	 * url.append("http://chart.googleapis.com/chart?"); url.append("cht=p3");
	 * url.append("&chs=600x300"); url.append("&" + chartData.toString());
	 * url.append("&" + chartLabels.toString()); url.append("&" +
	 * chartLegend.toString()); url.append("&chdlp=b&chdls=000000,18");
	 * Log.d(ACTIVITY_SERVICE, url.toString()); return url.toString(); }
	 * 
	 * private String getMonthChartUrl() { Cursor c = mDbHelper.rawQuery(
	 * "select strftime(\'%m\', date) as date, sum(amount) as sum from receipt group by strftime(\'%m\', date)"
	 * ); List<Double> sumList = new ArrayList<Double>(12); for (int i = 0; i <
	 * 12; i++) { sumList.add(0.0); }
	 * 
	 * if (c != null) { if (c.moveToFirst()) { do { String month =
	 * c.getString(c.getColumnIndexOrThrow(ReceiptDbAdapter.KEY_DATE)); String
	 * monthSum = c.getString(c.getColumnIndexOrThrow("sum"));
	 * sumList.set(Integer.valueOf(month), Double.valueOf(monthSum)); } while
	 * (c.moveToNext()); } }
	 * 
	 * //Create string builder for month sums StringBuilder chartData = new
	 * StringBuilder();
	 * 
	 * chartData.append("chd=t:"); for (int i = 0; i < 12; i++) {
	 * chartData.append(sumList.get(i) + ","); }
	 * chartData.deleteCharAt(chartData.length()-1); //Create string builder for
	 * month labels StringBuilder chartLabels = new StringBuilder();
	 * chartLabels.append("chxl=0:|"); for (int i = 1; i <= 12; i++) { if (i <
	 * 10) { chartLabels.append("0" + String.valueOf(i) + "|"); } else {
	 * chartLabels.append(String.valueOf(i) + "|"); } }
	 * chartLabels.deleteCharAt(chartLabels.length()-1); //Create string builder
	 * for final url StringBuilder url = new StringBuilder();
	 * url.append("http://chart.googleapis.com/chart?"); url.append("cht=bvs");
	 * url.append("&chs=600x300"); url.append("&" + chartData.toString());
	 * url.append("&chxt=x,y"); url.append("&" + chartLabels.toString());
	 * url.append("&chdlp=b&chdls=000000,18"); url.append("&chbh=40");
	 * Log.d(ACTIVITY_SERVICE, url.toString()); return url.toString(); }
	 */
}
