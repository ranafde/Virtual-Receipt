/**
 * Pie chart for category rendering
 */
package edu.upenn.cis599.charts;

import java.util.ArrayList;
import java.util.HashMap;

import org.achartengine.ChartFactory;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import edu.upenn.cis599.eas499.ReceiptDbAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Category demo pie chart.
 */
public class PieChartCategory extends MyChartHelper {
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	@Override
	public String getName() {
		return "Category Spending Analyzer";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	@Override
	public String getDesc() {
		return "Spending on each of the categories";
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

		HashMap<String, Double> categorySum = mDbHelper
				.retrieveDataByCategory(0);
		ArrayList<String> categoryList = new ArrayList<String>();
		ArrayList<Double> sumList = new ArrayList<Double>();
		for (String category : categorySum.keySet()) {
			categoryList.add(category);
			sumList.add(categorySum.get(category));
		}

		DefaultRenderer renderer = buildCategoryRenderer(categorySum.size());
		// renderer.setZoomButtonsVisible(true);
		// renderer.setZoomEnabled(true);
		// renderer.setPanEnabled(true);
		// renderer.setChartTitleTextSize(100);
		// renderer.setChartTitle("What the hell is this");

		mDbHelper.close();
		return ChartFactory
				.getPieChartIntent(
						context,
						buildCategoryDataset("Category Spending", categoryList,
								sumList), renderer, "Category Spending");
	}

	/**
	 * Get the Spending by Category View within specified duration
	 */

	/* dupicate code, similar to execute() method */
	/* long method, need to be modularized */
	public View getCategoryView(Context context, int duration) {
		ReceiptDbAdapter mDbHelper = new ReceiptDbAdapter(context);
		mDbHelper.open();

		HashMap<String, Double> categorySum = mDbHelper
				.retrieveDataByCategory(duration);
		ArrayList<String> categoryList = new ArrayList<String>();
		ArrayList<Double> sumList = new ArrayList<Double>();
		for (String category : categorySum.keySet()) {
			categoryList.add(category);
			sumList.add(categorySum.get(category));
		}

		DefaultRenderer renderer = buildCategoryRenderer(categorySum.size());
		/* renderer should be set in another method */
		renderer.setPanEnabled(false);
		renderer.setZoomEnabled(false);
		renderer.setChartTitleTextSize(20);
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
		renderer.setChartTitleTextSize(50);
		renderer.setLegendTextSize(40);
		renderer.setLabelsTextSize(40);

		mDbHelper.close();
		return ChartFactory.getPieChartView(context,
				buildCategoryDataset(chartTitle, categoryList, sumList),
				renderer);
	}

}
