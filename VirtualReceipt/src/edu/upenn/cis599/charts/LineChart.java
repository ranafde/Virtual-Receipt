/**
 * Shashank Vadlamani
 */
package edu.upenn.cis599.charts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import edu.upenn.cis599.eas499.ReceiptDbAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;

/**
 * Monthly spending demo chart.
 */
public class LineChart extends MyChartHelper {
	
	public static final String TAG = "LineChart.java";
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	@Override
	public String getName() {
		return "Monthly Spending";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	@Override
	public String getDesc() {
		return "The total spending of each month in current year";
	}

	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */

	@Override
	public Intent execute(Context context) {
		List<double[]> monthlySpendingValues = getMonthlySpendingList(context);
		String currentYearMonthlySpendingLegendTitle = "Year "
				+ Calendar.getInstance().get(Calendar.YEAR)
				+ " Monthly Spending";
		String allTimeMonthlySpendingLegendTitle = "All Time Monthly Spending";
		String[] titles = new String[] { currentYearMonthlySpendingLegendTitle,
				allTimeMonthlySpendingLegendTitle };
		List<double[]> months = new ArrayList<double[]>();
		for (int i = 0; i < titles.length; i++) {
			months.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
		}
		int[] colors = new int[] { Color.rgb(233, 126, 126),
				Color.rgb(129, 164, 238) };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE,
				PointStyle.TRIANGLE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		setXYMultipleSeriesRenderer(renderer);
		setChartSettings(renderer, "Monthly Spending", "Month", "", 1, 12, 0,
				(int) (getMaximumMonthlyPayment(context) * 1.1), Color.BLACK,
				Color.BLACK);
		Intent intent = ChartFactory.getLineChartIntent(context,
				buildDataset(titles, months, monthlySpendingValues), renderer,
				"Monthly Spending");
		Log.v(TAG,"in execute of Line chart");
		return intent;
	}

	private ArrayList<double[]> getMonthlySpendingList(Context context) {
		ReceiptDbAdapter mDbHelper = new ReceiptDbAdapter(context);
		mDbHelper.open();
		ArrayList<double[]> result = new ArrayList<double[]>();
		double[] currentYearMonthlySpendingList = mDbHelper
				.retrieveMonthlyPayment(1);
		double[] allTimeMonthlySpendingList = mDbHelper
				.retrieveMonthlyPayment(2);
		result.add(currentYearMonthlySpendingList);
		result.add(allTimeMonthlySpendingList);
		mDbHelper.close();
		return result;
	}

	@SuppressWarnings("deprecation")
	private void setXYMultipleSeriesRenderer(XYMultipleSeriesRenderer renderer) {
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
		}
		renderer.setXLabels(25);
		renderer.setYLabels(25);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.CENTER);
		// legends and labels size
		renderer.setLegendTextSize(15);
		renderer.setLabelsTextSize(10);
		renderer.setLegendHeight(120);
		renderer.setPanLimits(new double[] { 1, 12, 0, 30000 });
		renderer.setZoomLimits(new double[] { 1, 12, 0, 30000 });
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setXLabels(0);
		String[] month = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
				"Aug", "Sep", "Oct", "Nov", "Dec" };
		for (int i = 0; i < month.length; i++) {
			renderer.addTextLabel(i + 1, month[i]);
		}
		int rendererCount = renderer.getSeriesRendererCount();
		/* code to set the width of the line in line chart */
		for (int i = 0; i < rendererCount; i++) {
			XYSeriesRenderer seriesRenderer = (XYSeriesRenderer) renderer
					.getSeriesRendererAt(i);
			seriesRenderer.setLineWidth(6f);
		}
	}

	private double getMaximumMonthlyPayment(Context context) {
		ReceiptDbAdapter mDbHelper = new ReceiptDbAdapter(context);
		mDbHelper.open();
		double[] allTimeMonthlySpendingList = mDbHelper
				.retrieveMonthlyPayment(2);
		double max = 0.0;
		for (double sum : allTimeMonthlySpendingList) {
			if (sum > max)
				max = sum;
		}
		mDbHelper.close();
		return max;
	}
}
