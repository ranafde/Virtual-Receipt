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
	@Override
	public Intent execute(Context context) {
		ArrayList<String> paymentTypeList = new ArrayList<String>();
		ArrayList<Double> totalAmountForEachPaymentTypeList = new ArrayList<Double>();
		int numberOfPaymentTypes = buildParametersForPieChartIntent(context, 0,
				paymentTypeList, totalAmountForEachPaymentTypeList);
		DefaultRenderer renderer = buildCategoryRenderer(numberOfPaymentTypes);
		setDefaultRenderer(renderer, 1);
		return ChartFactory.getPieChartIntent(
				context,
				buildCategoryDataset("Payment Type Spending", paymentTypeList,
						totalAmountForEachPaymentTypeList), renderer,
				"Payment Type Spending");
	}

	/**
	 * Get the Spending by Payment Type View within specified duration
	 */
	public GraphicalView getPaymentView(Context context, int duration) {
		ArrayList<String> paymentTypeList = new ArrayList<String>();
		ArrayList<Double> totalAmountForEachPaymentTypeList = new ArrayList<Double>();
		int numberOfPaymentTypes = buildParametersForPieChartIntent(context,
				duration, paymentTypeList, totalAmountForEachPaymentTypeList);
		DefaultRenderer renderer = buildCategoryRenderer(numberOfPaymentTypes);
		setDefaultRenderer(renderer, 2);
		String pieChartTitle;
		switch (duration) {
		case 1:
			pieChartTitle = "Current Year";
			break;
		case 2:
			pieChartTitle = "All Time";
			break;
		default:
			pieChartTitle = "Current Month";
			break;
		}
		renderer.setChartTitle(pieChartTitle);
		return ChartFactory.getPieChartView(
				context,
				buildCategoryDataset(pieChartTitle, paymentTypeList,
						totalAmountForEachPaymentTypeList), renderer);
	}

	private int buildParametersForPieChartIntent(Context context, int duration,
			ArrayList<String> paymentTypeList,
			ArrayList<Double> totalAmountForEachPaymentTypeList) {
		ReceiptDbAdapter mDbHelper = new ReceiptDbAdapter(context);
		mDbHelper.open();
		HashMap<String, Double> paymentData = mDbHelper
				.retrieveDataByPayment(duration);
		for (String payment : paymentData.keySet()) {
			paymentTypeList.add(payment);
			totalAmountForEachPaymentTypeList.add(paymentData.get(payment));
		}
		mDbHelper.close();
		return paymentData.size();
	}

	private void setDefaultRenderer(DefaultRenderer renderer, int option) {
		switch (option) {
		case 1:
			renderer.setZoomButtonsVisible(true);
			renderer.setZoomEnabled(true);
			renderer.setChartTitleTextSize(30);
			renderer.setLabelsTextSize(40);
			renderer.setLegendTextSize(40);
		case 2:
			renderer.setChartTitleTextSize(50);
			renderer.setPanEnabled(false);
			renderer.setZoomEnabled(false);
			renderer.setShowLabels(true);
			renderer.setDisplayValues(true);
			renderer.setStartAngle(45);
			renderer.setLabelsTextSize(40);
			renderer.setLegendTextSize(40);
			// Set the color gradient and highlight the first slice.
			for (int i = 0; i < renderer.getSeriesRendererCount(); i++) {
				SimpleSeriesRenderer simpleSeriesRenderer = renderer
						.getSeriesRendererAt(i);
				simpleSeriesRenderer.setGradientEnabled(true);
				int endColor = simpleSeriesRenderer.getColor();
				int startColor = lighten(endColor);
				simpleSeriesRenderer.setGradientStart(0, startColor);
				simpleSeriesRenderer.setGradientStop(0, endColor);
				if (i == 0)
					simpleSeriesRenderer.setHighlighted(true);
			}
		}
	}
}
