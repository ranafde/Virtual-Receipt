/**
 * Yiran Qin 
 */
package edu.upenn.cis599.charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.util.Log;

/**
 * An abstract helper class for the charts to extend. It contains some methods
 * for building datasets and renderers.
 */
public abstract class MyChartHelper implements MyChartInterface {

	/**
	 * Builds an XY multiple dataset using the provided values.
	 * 
	 * @param titles
	 *            the series titles
	 * @param xAxisValues
	 *            the values for the X axis
	 * @param yAxisValues
	 *            the values for the Y axis
	 * @return the XY multiple dataset
	 */
	protected XYMultipleSeriesDataset buildDataset(String[] titles,
			List<double[]> xAxisValues, List<double[]> yAxisValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xAxisValues, yAxisValues, 0);
		return dataset;
	}

	public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles,
			List<double[]> xAxisValues, List<double[]> yAxisValues, int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			double[] xValues = xAxisValues.get(i);
			double[] yValues = yAxisValues.get(i);
			int seriesLength = xValues.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xValues[k], yValues[k]);
			}
			dataset.addSeries(series);
		}
	}

	/**
	 * Builds an XY multiple series renderer.
	 * 
	 * @param colors
	 *            the series rendering colors
	 * @param pointStyles
	 *            the series point styles
	 * @return the XY multiple series renderers
	 */
	protected XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] pointStyles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, pointStyles);
		return renderer;
	}

	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
			PointStyle[] pointStyles) {
		renderer.setAxisTitleTextSize(40);
		renderer.setChartTitleTextSize(15);
		renderer.setLabelsTextSize(18);
		renderer.setLegendTextSize(40);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(pointStyles[i]);
			renderer.addSeriesRenderer(r);
		}
	}

	/**
	 * Sets a few of the series renderer settings.
	 * 
	 * @param mRenderer
	 *            the renderer to set the properties to
	 * @param title
	 *            the chart title
	 * @param xAxisTitle
	 *            the title for the X axis
	 * @param yAxisTitle
	 *            the title for the Y axis
	 * @param minXValue
	 *            the minimum value on the X axis
	 * @param maxXValue
	 *            the maximum value on the X axis
	 * @param minYValue
	 *            the minimum value on the Y axis
	 * @param maxYValue
	 *            the maximum value on the Y axis
	 * @param axesColor
	 *            the axes color
	 * @param labelsColor
	 *            the labels color
	 */
	protected void setChartSettings(XYMultipleSeriesRenderer mRenderer,
			String title, String xAxisTitle, String yAxisTitle,
			double minXValue, double maxXValue, double minYValue,
			double maxYValue, int axesColor, int labelsColor) {
		mRenderer.setChartTitle(title);
		mRenderer.setXTitle(xAxisTitle);
		mRenderer.setYTitle(yAxisTitle);
		mRenderer.setXAxisMin(minXValue);
		mRenderer.setXAxisMax(maxXValue);
		mRenderer.setYAxisMin(minYValue);
		mRenderer.setYAxisMax(maxYValue);
		// Setting the background color of Line chart;
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setAxesColor(axesColor);
		mRenderer.setLabelsColor(labelsColor);
	}

	protected CategorySeries buildCategoryDataset(String title,
			List<String> titles, List<Double> values) {
		CategorySeries series = new CategorySeries(title);
		int i = 0;
		for (double value : values) {
			series.add(titles.get(i), value);
			i++;
		}
		return series;
	}

	/**
	 * Builds a category renderer to use the provided colors.
	 * 
	 * @param colors
	 *            the colors
	 * @return the category renderer
	 */
	protected DefaultRenderer buildCategoryRenderer(int numColors) {
		List<Integer> colors = getUniqueColors(numColors);
		Log.v("Colors", colors.size() + "");
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setLabelsTextSize(18);
		renderer.setLegendTextSize(20);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		for (int color : colors) {
			SimpleSeriesRenderer simpleSeriesRenderer = new SimpleSeriesRenderer();
			simpleSeriesRenderer.setColor(color);
			simpleSeriesRenderer.setDisplayChartValues(true);
			simpleSeriesRenderer.setChartValuesTextSize(8);
			renderer.addSeriesRenderer(simpleSeriesRenderer);
		}
		return renderer;
	}

	protected List<Integer> getUniqueColors(int amount) {
		int[] colors = new int[] { Color.rgb(129, 164, 238),
				Color.rgb(143, 238, 149), Color.rgb(233, 126, 126),
				Color.rgb(179, 144, 203), Color.rgb(255, 171, 96),
				Color.rgb(126, 220, 244), Color.rgb(193, 235, 153),
				Color.rgb(184, 205, 238), Color.rgb(213, 77, 80), Color.LTGRAY,
				Color.DKGRAY, Color.MAGENTA, Color.CYAN };
		List<Integer> colorList = new ArrayList<Integer>(amount);
		for (int i = 0; i < amount; i++) {
			if (amount > colors.length) {
				colorList.add(colors[i % colors.length]);
			} else {
				colorList.add(colors[i]);
			}
		}
		return colorList;
	}

	/**
	 * Lighten the given color
	 * 
	 * @param color
	 *            original color
	 * @return lightened color
	 */
	protected int lighten(int color) {
		double[] rgb = toRgb(color, 1.3);
		System.out.println(Arrays.toString(rgb));
		double[] rgbClone = rgb.clone();
		double threshold = 255.999;
		Arrays.sort(rgbClone);
		double blue = rgbClone[2];
		if (blue <= threshold)
			return Color.rgb((int) rgb[0], (int) rgb[1], (int) rgb[2]);
		double total = rgb[0] + rgb[1] + rgb[2];
		if (total >= 3 * threshold)
			return Color.rgb(255, 255, 255);
		double x = (3 * threshold - total) / (3 * blue - total);
		double gray = threshold - x * blue;
		return Color.rgb((int) (gray + x * rgb[0]), (int) (gray + x * rgb[1]),
				(int) (gray + x * rgb[2]));
	}

	/**
	 * Convert the given color in int to an double array of its rgb value
	 * multiplied by a factor.
	 * 
	 * @param color
	 * @param factor
	 * @return
	 */
	protected double[] toRgb(int color, double factor) {
		double[] result = new double[3];
		String hexColor = String.format("#%06X", (0xFFFFFF & color));
		System.out.println(hexColor);
		String red = hexColor.substring(1, 3);
		String green = hexColor.substring(3, 5);
		String blue = hexColor.substring(5, 7);
		int r = Integer.parseInt(red, 16);
		int g = Integer.parseInt(green, 16);
		int b = Integer.parseInt(blue, 16);
		result[0] = (r * factor);
		result[1] = (g * factor);
		result[2] = (b * factor);
		return result;
	}

}
