/**
 * Yiran Qin
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

/**
 * Monthly spending demo chart.
 */
public class LineChart extends MyChartHelper {
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	public String getName() {
		return "Monthly Spending";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
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
	public Intent execute(Context context) {

		ReceiptDbAdapter mDbHelper = new ReceiptDbAdapter(context);
		mDbHelper.open();
		List<double[]> values = new ArrayList<double[]>();
		double[] sumList = mDbHelper.retrieveMonthlyPayment(1);
		double[] allList = mDbHelper.retrieveMonthlyPayment(2);
		values.add(sumList);
		values.add(allList);

		String[] titles = new String[] {
				"Year" + Calendar.getInstance().get(Calendar.YEAR)
						+ " Monthly Spending", "All Time Monthly Spending" };
		List<double[]> x = new ArrayList<double[]>();
		for (int i = 0; i < titles.length; i++) {
			x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
		}

		int[] colors = new int[] {Color.rgb(233, 126, 126), Color.rgb(129, 164, 238) };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE,
				PointStyle.TRIANGLE };

		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
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
		//legends and labels size
	    renderer.setLegendTextSize(15);
	    renderer.setLabelsTextSize(10);
	    renderer.setLegendHeight(120);
//	    renderer.setMargins(new int[] { 20, 30, 15, 20 });
//	    mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
		// renderer.setApplyBackgroundColor(true);
		// renderer.setBackgroundColor(Color.WHITE);
		// renderer.setAxisTitleTextSize(16);
		// renderer.set
		// renderer.setZoomButtonsVisible(true);

		renderer.setPanLimits(new double[] { 1, 12, 0, 30000 });
		renderer.setZoomLimits(new double[] { 1, 12, 0, 30000 });

		String[] month = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
				"Aug", "Sep", "Oct", "Nov", "Dec" };
		for (int i = 0; i < month.length; i++) {

			renderer.addTextLabel(i + 1, month[i]);
		}
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setXLabels(0);

		int rendererCount = renderer.getSeriesRendererCount();
		for (int i = 0; i < rendererCount; i++) {
			XYSeriesRenderer seriesRenderer = (XYSeriesRenderer) renderer
					.getSeriesRendererAt(i);
			seriesRenderer.setLineWidth(6f);
		}

		setChartSettings(renderer, "Monthly Spending", "Month", "", 1, 12, 0,
				(int) (getMaximumMonthlyPayment(allList) * 1.1), Color.BLACK,
				Color.BLACK);
		Intent intent = ChartFactory.getLineChartIntent(context,
				buildDataset(titles, x, values), renderer, "Monthly Spending");

		mDbHelper.close();
		return intent;
	}

	private double getMaximumMonthlyPayment(double[] sumList) {
		double max = 0.0;
		for (double sum : sumList) {
			if (sum > max)
				max = sum;
		}
		return max;
	}
}
