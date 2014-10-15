/**
 * Pie chart for payment rendering
 */
package edu.upenn.cis599.charts;

import java.util.ArrayList;
import java.util.HashMap;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import edu.upenn.cis599.eas499.ReceiptDbAdapter;

import android.content.Context;
import android.content.Intent;

/**
 * Budget demo pie chart.
 */
public class PieChartPayment extends MyChartHelper {
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	@Override
	public String getName() {
		return "Payment Type Analyzer";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	@Override
	public String getDesc() {
		return "The spending on each of the payment type";
	}

	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	/* meaningless variable names */
	@Override
	public Intent execute(Context context) {
		ReceiptDbAdapter mDbHelper = new ReceiptDbAdapter(context);
		mDbHelper.open();

		HashMap<String, Double> paymentSum = mDbHelper.retrieveDataByPayment(0);
		ArrayList<String> paymentList = new ArrayList<String>();
		ArrayList<Double> sumList = new ArrayList<Double>();
		for (String payment : paymentSum.keySet()) {
			paymentList.add(payment);
			sumList.add(paymentSum.get(payment));
		}

		DefaultRenderer renderer = buildCategoryRenderer(paymentSum.size());
		renderer.setZoomButtonsVisible(true);
		renderer.setZoomEnabled(true);
		renderer.setChartTitleTextSize(15);

		mDbHelper.close();
		return ChartFactory.getPieChartIntent(
				context,
				buildCategoryDataset("Payment Type Spending", paymentList,
						sumList), renderer, "Payment Type Spending");
	}

	/**
	 * Get the Spending by Payment Type View within specified duration
	 */
	/* dupicate code, similar to execute() method */
	/* long method, need to be modularized */
	public GraphicalView getPaymentView(Context context, int duration) {
		ReceiptDbAdapter mDbHelper = new ReceiptDbAdapter(context);
		mDbHelper.open();

		HashMap<String, Double> paymentSum = mDbHelper
				.retrieveDataByPayment(duration);
		ArrayList<String> paymentList = new ArrayList<String>();
		ArrayList<Double> sumList = new ArrayList<Double>();
		for (String payment : paymentSum.keySet()) {
			paymentList.add(payment);
			sumList.add(paymentSum.get(payment));
		}

		DefaultRenderer renderer = buildCategoryRenderer(paymentSum.size());
		/* renderer should be set in another method */
		renderer.setChartTitleTextSize(20);
		renderer.setPanEnabled(false);
		renderer.setZoomEnabled(false);
		renderer.setShowLabels(true);

		// renderer.isDisplayValues();
		renderer.setDisplayValues(true);
		renderer.setStartAngle(45);

		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		// Set the color gradient and highlight the first slice.
		/* SimpleSeriesRenderer r should be declared outside the loop */
		for (int i = 0; i < renderer.getSeriesRendererCount(); i++) {
			SimpleSeriesRenderer r = renderer.getSeriesRendererAt(i);
			r.setGradientEnabled(true);
			int endColor = r.getColor();
			int startColor = lighten(endColor);
			r.setGradientStart(0, startColor);
			r.setGradientStop(0, endColor);
			if (i == 0)
				r.setHighlighted(true);
		}

		String chartTitle;
		switch (duration) {
		case 1:
			chartTitle = "Current Year";
			break;
		case 2:
			chartTitle = "All Time";
			break;
		default:
			chartTitle = "Current Month";
			break;
		}
		renderer.setChartTitle(chartTitle);

		mDbHelper.close();
		return ChartFactory.getPieChartView(context,
				buildCategoryDataset(chartTitle, paymentList, sumList),
				renderer);
	}

}
