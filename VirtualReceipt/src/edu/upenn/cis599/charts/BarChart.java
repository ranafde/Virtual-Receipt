/**
 * Rahul Ajay Nafde
 */
package edu.upenn.cis599.charts;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import edu.upenn.cis599.eas499.ReceiptDbAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.util.Log;


/**
 * Monthly spending demo chart.
 */
public class BarChart extends MyChartHelper {
	
	public static final String TAG = "BarChart.java";
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	@Override
	public String getName() {
		return "Income vs Budget vs Expenses Statistics";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	@Override
	public String getDesc() {
		return "Displays the statistics for Income, Budget and Expenses incurred every month for the current year.";
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
		int[] x = { 0,1,2,3,4,5,6,7, 8, 9, 10, 11 };
		//int[] income = { 2000,2500,2700,3000,2800,3500,3700,3800, 0,0,0,0};
		//int[] expense = {2200, 2700, 2900, 2800, 2600, 3000, 3300, 3400, 0, 0, 0, 0 };
		//int[] budget = {2210, 2500, 2700, 2300, 2200, 3100, 3000, 3200, 0, 0, 0, 0};

		String[] mMonth = new String[] {
				"Jan", "Feb" , "Mar", "Apr", "May", "Jun",
				"Jul", "Aug" , "Sep", "Oct", "Nov", "Dec"
		};
		// Creating an XYSeries for Income
		XYSeries incomeSeries = new XYSeries("Income");
		// Creating an XYSeries for Expense
		XYSeries expenseSeries = new XYSeries("Expense");
		// Creating an XYSeries for Expense
		XYSeries budgetSeries = new XYSeries("Budget");
		
		/* get income, expense and budget details */
		ArrayList<double[]> monthlyExpenseDetails = getMonthlySpendingList(context);
		ArrayList<double[]> monthlyIncomeDetails = getMonthlyIncomeList(context);
		ArrayList<double[]> monthlyBudgetDetails = getMonthlyBudgetList(context);
		
		double[] monthlyIncome = new double[10];
		double[] monthlyExpenses = new double[10];
		double[] monthlyBudget= new double[10];
		
		monthlyIncome = monthlyIncomeDetails.get(0);
		monthlyExpenses = monthlyExpenseDetails.get(0);
		monthlyBudget = monthlyBudgetDetails.get(0);
		
		// Adding data to Income and Expense Series
//		for(int i=0;i<x.length;i++){
//			incomeSeries.add(i,income[i]);
//			budgetSeries.add(i, budget[i]);
//		}
		
		for(int i=0;i<x.length;i++){
			incomeSeries.add(i,monthlyIncome[i]);
			expenseSeries.add(i,monthlyExpenses[i]);
			budgetSeries.add(i, monthlyBudget[i]);
		}

		// Creating a dataset to hold each series
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		// Adding Income Series to the dataset
		dataset.addSeries(incomeSeries);
		// Adding Expense Series to dataset
		dataset.addSeries(expenseSeries);
		// Adding Expense Series to dataset
		dataset.addSeries(budgetSeries);

		
		// Creating XYSeriesRenderer to customize incomeSeries
		XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
		incomeRenderer = initializeXYSeriesRenderer();
		incomeRenderer.setColor(Color.GREEN); //color of the graph set to cyan

		// Creating XYSeriesRenderer to customize expenseSeries
		XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
		expenseRenderer = initializeXYSeriesRenderer();
		expenseRenderer.setColor(Color.RED);
		
		// Creating XYSeriesRenderer to customize budgetSeries
		XYSeriesRenderer budgetRenderer = new XYSeriesRenderer();
		budgetRenderer = initializeXYSeriesRenderer();
		budgetRenderer.setColor(Color.YELLOW);

		// Creating a XYMultipleSeriesRenderer to customize the whole chart
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		
		XYSeriesRenderer[] input= new XYSeriesRenderer[3];
		input[0] = incomeRenderer;
		input[1] = expenseRenderer;
		input[2] = budgetRenderer;
		
		 
				
		multiRenderer = addMutlipleRenderer(input);

		// Creating an intent to plot bar chart using dataset and multipleRenderer
		Intent intent = ChartFactory.getBarChartIntent(context, dataset, multiRenderer, Type.DEFAULT); 
		return intent;
	}
	
	
	private XYMultipleSeriesRenderer addMutlipleRenderer(XYSeriesRenderer[] multipleXYSeries){
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		String[] mMonth = new String[] {
				"Jan", "Feb" , "Mar", "Apr", "May", "Jun",
				"Jul", "Aug" , "Sep", "Oct", "Nov", "Dec"
		};
		
		/* Set properties of multiple series renderer */
		multiRenderer.setXLabels(0);
		multiRenderer.setChartTitle("Income vs Expense Chart vs Budget");
		multiRenderer.setXTitle("Year 2014");
		multiRenderer.setYTitle("Amount in Dollars");

		/***
		 * Customizing graphs
		 */
		//setting text size of the title
		multiRenderer.setChartTitleTextSize(28);
		//setting text size of the axis title
		multiRenderer.setAxisTitleTextSize(24);
		//setting text size of the graph lable
		multiRenderer.setLabelsTextSize(24);
		//setting zoom buttons visiblity
		multiRenderer.setZoomButtonsVisible(false);
		//setting pan enablity which uses graph to move on both axis
		multiRenderer.setPanEnabled(false, false);
		//setting click false on graph
		multiRenderer.setClickEnabled(false);
		//setting zoom to false on both axis
		multiRenderer.setZoomEnabled(false, false);
		//setting lines to display on y axis
		multiRenderer.setShowGridY(false);
		//setting lines to display on x axis
		multiRenderer.setShowGridX(false);
		//setting legend to fit the screen size
		multiRenderer.setFitLegend(true);
		//setting displaying line on grid
		multiRenderer.setShowGrid(false);
		//setting zoom to false
		multiRenderer.setZoomEnabled(false);
		//setting external zoom functions to false
		multiRenderer.setExternalZoomEnabled(false);
		//setting displaying lines on graph to be formatted(like using graphics)
		multiRenderer.setAntialiasing(true);
		//setting to in scroll to false
		multiRenderer.setInScroll(false);
		//setting to set legend height of the graph
		multiRenderer.setLegendHeight(30);
		//setting x axis label align
		multiRenderer.setXLabelsAlign(Align.CENTER);
		//setting y axis label to align
		multiRenderer.setYLabelsAlign(Align.LEFT);
		//setting text style
		multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
		//setting no of values to display in y axis
		multiRenderer.setYLabels(10);
		// setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.
		// if you use dynamic values then get the max y value and set here
		multiRenderer.setYAxisMax(4000);
		//setting used to move the graph on xaxiz to .5 to the right
		multiRenderer.setXAxisMin(-0.5);
		//setting max values to be display in x axis
		multiRenderer.setXAxisMax(11);
		//setting bar size or space between two bars
		multiRenderer.setBarSpacing(0.5);
		//Setting background color of the graph to transparent
		multiRenderer.setBackgroundColor(Color.TRANSPARENT);
		//Setting margin color of the graph to transparent
		multiRenderer.setMarginsColor(Color.TRANSPARENT);
		multiRenderer.setApplyBackgroundColor(true);

		//setting the margin size for the graph in the order top, left, bottom, right
		multiRenderer.setMargins(new int[]{30, 30, 30, 30});
		
		for(int i=0; i< mMonth.length;i++){
			multiRenderer.addXTextLabel(i, mMonth[i]);
		}

		// Adding incomeRenderer and expenseRenderer to multipleRenderer
		// Note: The order of adding dataseries to dataset and renderers to multipleRenderer
		// should be same
		/* Do this for every XY series in MultipleXYSeries */
		for(int i = 0; i < multipleXYSeries.length; i++){
			multiRenderer.addSeriesRenderer(multipleXYSeries[i]);
		}
		return multiRenderer;
	}

	private XYSeriesRenderer initializeXYSeriesRenderer(){
		// Creating XYSeriesRenderer to customize incomeSeries
		XYSeriesRenderer xySeries = new XYSeriesRenderer();
		xySeries.setColor(Color.GREEN); //color of the graph set to cyan
		xySeries.setFillPoints(true);
		xySeries.setLineWidth(2);
		xySeries.setDisplayChartValues(true);
		xySeries.setDisplayChartValuesDistance(10);
		
		return xySeries;
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
	
	private ArrayList<double[]> getMonthlyIncomeList(Context context) {
		ReceiptDbAdapter mDbHelper = new ReceiptDbAdapter(context);
		mDbHelper.open();
		ArrayList<double[]> result = new ArrayList<double[]>();
		double[] currentYearMonthlySpendingList = mDbHelper
				.retrieveMonthlyIncome(1);
		double[] allTimeMonthlySpendingList = mDbHelper
				.retrieveMonthlyIncome(2);
		result.add(currentYearMonthlySpendingList);
		result.add(allTimeMonthlySpendingList);
		mDbHelper.close();
		return result;
	}

	private ArrayList<double[]> getMonthlyBudgetList(Context context) {
		ReceiptDbAdapter mDbHelper = new ReceiptDbAdapter(context);
		mDbHelper.open();
		ArrayList<double[]> result = new ArrayList<double[]>();
		double[] currentYearMonthlySpendingList = mDbHelper
				.retrieveMonthlyBudget(1);
		double[] allTimeMonthlySpendingList = mDbHelper
				.retrieveMonthlyBudget(2);
		result.add(currentYearMonthlySpendingList);
		result.add(allTimeMonthlySpendingList);
		mDbHelper.close();
		return result;
	}
}
