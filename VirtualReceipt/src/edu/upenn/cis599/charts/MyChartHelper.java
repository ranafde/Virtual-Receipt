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
	 * @param xValues
	 *            the values for the X axis
	 * @param yValues
	 *            the values for the Y axis
	 * @return the XY multiple dataset
	 */
	protected XYMultipleSeriesDataset buildDataset(String[] titles,
			List<double[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xValues, yValues, 0);
		return dataset;
	}

	/* return XYMultipleSeriesDataset object instead of void */
	public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles,
			List<double[]> xValues, List<double[]> yValues, int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			/* change variable names xV and yV */
			double[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
	}

	/**
	 * Builds an XY multiple series renderer.
	 * 
	 * @param colors
	 *            the series rendering colors
	 * @param styles
	 *            the series point styles
	 * @return the XY multiple series renderers
	 */
	protected XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}

	/* return XYMultipleSeriesRenderer object instead of void */
	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
			PointStyle[] styles) {
		renderer.setAxisTitleTextSize(10);
		renderer.setChartTitleTextSize(15);
		renderer.setLabelsTextSize(10);
		renderer.setLegendTextSize(10);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
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
	 * @param xTitle
	 *            the title for the X axis
	 * @param yTitle
	 *            the title for the Y axis
	 * @param xMin
	 *            the minimum value on the X axis
	 * @param xMax
	 *            the maximum value on the X axis
	 * @param yMin
	 *            the minimum value on the Y axis
	 * @param yMax
	 *            the maximum value on the Y axis
	 * @param axesColor
	 *            the axes color
	 * @param labelsColor
	 *            the labels color
	 */
	/* return XYMultipleSeriesRenderer object instead of void */
	protected void setChartSettings(XYMultipleSeriesRenderer mRenderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		mRenderer.setChartTitle(title);
		mRenderer.setXTitle(xTitle);
		mRenderer.setYTitle(yTitle);
		mRenderer.setXAxisMin(xMin);
		mRenderer.setXAxisMax(xMax);
		mRenderer.setYAxisMin(yMin);
		mRenderer.setYAxisMax(yMax);

		// Setting the background color of Line chart;
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);

		mRenderer.setAxesColor(axesColor);
		mRenderer.setLabelsColor(labelsColor);
	}

	/**
	 * Builds an XY multiple time dataset using the provided values.
	 * 
	 * @param titles
	 *            the series titles
	 * @param xValues
	 *            the values for the X axis
	 * @param yValues
	 *            the values for the Y axis
	 * @return the XY multiple time dataset
	 */

	/*
	 * redundant code, similar to buildDataset() method. make a new method that
	 * is used by these two methods
	 */
	protected XYMultipleSeriesDataset buildDateDataset(String[] titles,
			List<Date[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			TimeSeries series = new TimeSeries(titles[i]);
			Date[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	/**
	 * Builds a category series using the provided values.
	 * 
	 * @param titles
	 *            the series titles
	 * @param values
	 *            the values
	 * @return the category series
	 */
	protected CategorySeries buildCategoryDataset(String title, double[] values) {
		CategorySeries series = new CategorySeries(title);
		int k = 0;
		for (double value : values) {
			series.add("Project " + ++k, value);
		}

		return series;
	}

	protected CategorySeries buildCategoryDataset(String title,
			List<String> titles, List<Double> values) {
		CategorySeries series = new CategorySeries(title);
		int k = 0;
		for (double value : values) {
			series.add(titles.get(k), value);
			k++;
		}

		return series;
	}

	/**
	 * Builds a multiple category series using the provided values.
	 * 
	 * @param titles
	 *            the series titles
	 * @param values
	 *            the values
	 * @return the category series
	 */

	/*
	 * redundant code, similar to buildCategoryDataset. make a new method that
	 * is used by both the methods
	 */
	protected MultipleCategorySeries buildMultipleCategoryDataset(String title,
			List<String[]> titles, List<double[]> values) {
		MultipleCategorySeries series = new MultipleCategorySeries(title);
		int k = 0;
		for (double[] value : values) {
			series.add(titles.get(k), value);
			k++;
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
		renderer.setLabelsTextSize(10);
		renderer.setLegendTextSize(10);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		for (int color : colors) {
			/* declare SimpleSeriesRenderer r outside the loop */
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			r.setDisplayChartValues(true);
			r.setChartValuesTextSize(8);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

	/**
	 * Builds a bar multiple series dataset using the provided values.
	 * 
	 * @param titles
	 *            the series titles
	 * @param values
	 *            the values
	 * @return the XY multiple bar dataset
	 */
	protected XYMultipleSeriesDataset buildBarDataset(String[] titles,
			List<double[]> values) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			CategorySeries series = new CategorySeries(titles[i]);
			double[] v = values.get(i);
			int seriesLength = v.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(v[k]);
			}
			dataset.addSeries(series.toXYSeries());
		}
		return dataset;
	}

	/**
	 * Builds a bar multiple series renderer to use the provided colors.
	 * 
	 * @param colors
	 *            the series renderers colors
	 * @return the bar multiple series renderer
	 */

	/* duplicate code, similar to setRenderer(). */
	protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(8);
		renderer.setChartTitleTextSize(10);
		renderer.setLabelsTextSize(8);
		renderer.setLegendTextSize(8);
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

	protected List<Integer> getUniqueColors(int amount) {
		/* assign rgb values to variable and assign those variables to colors */
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
		/* meaningless variable names */
		double[] rgb = toRgb(color, 1.3);
		System.out.println(Arrays.toString(rgb));
		double[] tmp = rgb.clone();
		double threshold = 255.999;
		Arrays.sort(tmp);
		double m = tmp[2];
		if (m <= threshold)
			return Color.rgb((int) rgb[0], (int) rgb[1], (int) rgb[2]);
		double total = rgb[0] + rgb[1] + rgb[2];
		if (total >= 3 * threshold)
			return Color.rgb(255, 255, 255);
		double x = (3 * threshold - total) / (3 * m - total);
		double gray = threshold - x * m;
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
