/**
 * PieChartBuilder Activity
 * Originally, all the charts are implemented as activity rather than views
 */
package edu.upenn.cis599.charts;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import edu.upenn.cis599.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class PieChartBuilder extends Activity {
	/* assign initial values in a constructor */
	public static final String TYPE = "type";

	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,
			Color.MAGENTA, Color.CYAN };

	private CategorySeries mCategorySeries = new CategorySeries("");

	private DefaultRenderer mDefaultRenderer = new DefaultRenderer();

	private String mDateFormat;

	/* meaningless variable names */
	private Button mAddButton;

	private EditText mEditTextX;

	private GraphicalView mChartView;

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mCategorySeries = (CategorySeries) savedState.getSerializable("current_series");
		mDefaultRenderer = (DefaultRenderer) savedState
				.getSerializable("current_renderer");
		mDateFormat = savedState.getString("date_format");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("current_series", mCategorySeries);
		outState.putSerializable("current_renderer", mDefaultRenderer);
		outState.putString("date_format", mDateFormat);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_chart);
		mEditTextX = (EditText) findViewById(R.id.xValue);
		setDefaultRenderer(mDefaultRenderer);
		mAddButton = (Button) findViewById(R.id.add);
		setButton();
	}

	private void setDefaultRenderer(DefaultRenderer mDefaultRenderer){
		mDefaultRenderer.setApplyBackgroundColor(true);
		mDefaultRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
		mDefaultRenderer.setChartTitleTextSize(30);
		mDefaultRenderer.setLabelsTextSize(38);
		mDefaultRenderer.setLegendTextSize(40);
		mDefaultRenderer.setMargins(new int[] { 20, 30, 15, 0 });
		mDefaultRenderer.setZoomButtonsVisible(true);
		mDefaultRenderer.setStartAngle(90);
	}
	
	private void setButton(){
		mAddButton.setEnabled(true);
		mEditTextX.setEnabled(true);

		mAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				double x = 0;
				try {
					x = Double.parseDouble(mEditTextX.getText().toString());
				} catch (NumberFormatException e) {
					// TODO
					mEditTextX.requestFocus();
					return;
				}
				mCategorySeries.add("Series " + (mCategorySeries.getItemCount() + 1), x);
				SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
				renderer.setColor(COLORS[(mCategorySeries.getItemCount() - 1)
						% COLORS.length]);
				mDefaultRenderer.addSeriesRenderer(renderer);
				mEditTextX.setText("");
				mEditTextX.requestFocus();
				if (mChartView != null) {
					mChartView.repaint();
				}
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getPieChartView(this, mCategorySeries, mDefaultRenderer);
			mDefaultRenderer.setClickEnabled(true);
			mDefaultRenderer.setSelectableBuffer(10);
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}
}
