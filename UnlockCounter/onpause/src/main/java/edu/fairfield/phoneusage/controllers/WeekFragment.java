package edu.fairfield.phoneusage.controllers;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.fairfield.phoneusage.R;
import edu.fairfield.phoneusage.models.classes.LocalDailyUsageEntry;
import edu.fairfield.phoneusage.models.data_sources.BaseDataSource;
import edu.fairfield.phoneusage.models.data_sources.LocalDailyUsageEntryDataSource;
import edu.fairfield.phoneusage.utils.CalendarUtil;

/**
 * Created by SujayBusam on 2/27/16.
 * <p>
 * Fragment to display weekly overview of total phone usage and number of unlocks. Breaks down
 * on a weekly and daily basis using a chart.
 */
public class WeekFragment extends Fragment implements UpdatableFragment {
    private static String TAG = "SVB-WeekFragment";
    private static SimpleDateFormat abbreviatedDayFormatter = new SimpleDateFormat("EEE", Locale.US);

    private Toast mToast;

    // Chart specific
    private CombinedChart mChart;
    private Map<String, LocalDailyUsageEntry> mUsageDataMap;
    private String[] mDays = new String[]{
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    };

    // Relevant to week display on top
    private Calendar mStartOfWeek;
    private Calendar mEndOfWeek;
    private TextView mWeekDateText;
    private int mWeeksAwayFromCurrent;

    /**
     * Update UI elements when tab selected.
     */
    @Override
    public void updateUI() {
        if (getActivity() == null) {
            return;
        }
        Log.d(TAG, "updateUI");

        // Get relevant datetime millis
        long sundayStartMS = mStartOfWeek.getTimeInMillis();
        long endDateTimeMS = mEndOfWeek.getTimeInMillis();

        // Set the text for the current week
        updateWeekDateText();

        // Get all LocalDailyUsageEntry objects from the past week and populate the weekly chart.
        LocalDailyUsageEntryDataSource.getInstance(getActivity())
                .fetchLocalDailyUsageEntriesBetweenDateTimes(sundayStartMS, endDateTimeMS,
                        new BaseDataSource.CompletionHandler<List<LocalDailyUsageEntry>>() {
                            @Override
                            public void onTaskCompleted(List<LocalDailyUsageEntry> result) {
                                updateTodayUsageIfNecessary(result);
                                populateWeeklyUsageChart(result);
                            }
                        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // Inflate the layout for this fragment;
        View view = inflater.inflate(R.layout.fragment_week, container, false);

        // Init UI elements
        mWeekDateText = (TextView) view.findViewById(R.id.week_date_text);
        ImageView mPrevWeekViewBtn = (ImageView) view.findViewById(R.id.prev_week_view_btn);
        ImageView mNextWeekViewBtn = (ImageView) view.findViewById(R.id.next_week_view_btn);

        // Add button onClick handlers
        mPrevWeekViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevWeekClicked();
            }
        });
        mNextWeekViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWeekClicked();
            }
        });

        initWeeklyUsageChart(view);
        updateUI();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updateUI();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mUsageDataMap = new HashMap<>();

        // Setup initial start and end days of this week
        mStartOfWeek = CalendarUtil.calendarForLastSundayStart();
        mEndOfWeek = CalendarUtil.calendarForThisSaturdayEnd();
        mWeeksAwayFromCurrent = 0;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        updateUI();
    }

    // ********************************* Button onClick Handlers ******************************** //

    /**
     * Set the start and end dates to the previous week, and update the UI.
     */
    public void prevWeekClicked() {
        mStartOfWeek.add(Calendar.DAY_OF_WEEK, -7);
        mEndOfWeek.add(Calendar.DAY_OF_WEEK, -7);
        mWeeksAwayFromCurrent--;
        updateUI();
    }

    /**
     * Set the start and end dates to the next week, and update the UI.
     */
    public void nextWeekClicked() {
        // Prevent navigation to future
        if (mWeeksAwayFromCurrent == 0) {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getActivity(), "Cannot go to future week!", Toast.LENGTH_SHORT);
            mToast.show();
            return;
        }

        mStartOfWeek.add(Calendar.DAY_OF_WEEK, 7);
        mEndOfWeek.add(Calendar.DAY_OF_WEEK, 7);
        mWeeksAwayFromCurrent++;
        updateUI();
    }

    // **************************** Private Chart Helper Functions ****************************** //

    private void initWeeklyUsageChart(View view) {
        mChart = (CombinedChart) view.findViewById(R.id.weekly_usage_chart);
        mChart.setDescription(new Description());
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        rightAxis.setTextColor(Color.LTGRAY);
        rightAxis.setTextSize(13f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        leftAxis.setTextColor(Color.LTGRAY);
        leftAxis.setTextSize(13f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setTextSize(13f);

        Legend legend = mChart.getLegend();
        legend.setTextColor(Color.LTGRAY);
        legend.setTextSize(13f);
    }

    private void populateWeeklyUsageChart(List<LocalDailyUsageEntry> usageEntries) {
        // Rebuild the hash map that maps day to a LocalDailyUsageEntry
        mUsageDataMap = new HashMap<>();
        for (LocalDailyUsageEntry usageEntry : usageEntries) {
            Calendar calForEntry = CalendarUtil.calendarFromMillis(usageEntry.getDateTimeMS());
            String abbreviatedDay = abbreviatedDayFormatter.format(calForEntry.getTime());
            if (mUsageDataMap.get(abbreviatedDay) == null) {
                // No entry for this day yet, so add it
                mUsageDataMap.put(abbreviatedDay, usageEntry);
            } else {
                // Entry already exist for this day. Update with newest one.
                Log.d(TAG, "Trying to add a second LocalDailyUsage entry for the same day: " + abbreviatedDay);
                if (mUsageDataMap.get(abbreviatedDay).getDateTimeMS() < usageEntry.getDateTimeMS()) {
                    mUsageDataMap.remove(abbreviatedDay);
                    mUsageDataMap.put(abbreviatedDay, usageEntry);
                }
            }
        }

        // Build up the line and bar entries for the chart from the new data mapping.
        List<Entry> lineEntries = new ArrayList<>();
        List<BarEntry> barEntries = new ArrayList<>();
        for (int dayIndex = 0; dayIndex < mDays.length; dayIndex++) {
            LocalDailyUsageEntry dailyEntry = mUsageDataMap.get(mDays[dayIndex]);
            if (dailyEntry != null) {
                float usageHours = dailyEntry.getTotalUsageInHours();
                float goalHours = dailyEntry.getGoalHoursInHours();
                lineEntries.add(new Entry(usageHours, dayIndex));
                barEntries.add(new BarEntry(goalHours, dayIndex));
                Log.d(TAG, "Added daily entry to chart. Usage: " + dailyEntry.getTotalUsageInHours()
                        + " Goal: " + dailyEntry.getGoalHoursInHours());
            }
        }

        // Add the line and bar entries to the chart.
        CombinedData combinedData = new CombinedData();
        combinedData.setData(getLineData(lineEntries));
        combinedData.setData(getBarData(barEntries));
        mChart.setData(combinedData);
        mChart.invalidate();
    }

    private LineData getLineData(List<Entry> entries) {
        LineData lineData = new LineData();

        LineDataSet set = new LineDataSet(entries, "Your Total Usage Hours");
        // set.setColor(Color.RED);
        set.setColor(getResources().getColor(android.R.color.holo_red_light));
        set.setLineWidth(2.5f);
        // set.setCircleColor(Color.RED);
        set.setCircleColor(getResources().getColor(android.R.color.holo_red_light));
        set.setCircleRadius(5f);
        // set.setFillColor(Color.RED);
        set.setFillColor(getResources().getColor(android.R.color.holo_red_light));
        set.setDrawValues(true);
        set.setValueTextSize(14f);
        // set.setValueTextColor(Color.RED);
        set.setValueTextColor(getResources().getColor(android.R.color.holo_red_dark));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        lineData.addDataSet(set);
        return lineData;
    }

    private BarData getBarData(List<BarEntry> entries) {
        BarData barData = new BarData();

        BarDataSet set = new BarDataSet(entries, "Your Goal Usage Hours");
        // set.setColor(Color.rgb(60, 220, 78));
        set.setColor(getResources().getColor(android.R.color.holo_green_dark));
        //set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextColor(getResources().getColor(android.R.color.holo_green_light));
        set.setValueTextSize(14f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        barData.addDataSet(set);
        return barData;
    }

    // ******************************** Other Helper Functions ********************************** //

    private void updateWeekDateText() {
        int startMonth = mStartOfWeek.get(Calendar.MONTH) + 1; // Since it starts at 0
        int startDay = mStartOfWeek.get(Calendar.DAY_OF_MONTH);
        int endMonth = mEndOfWeek.get(Calendar.MONTH) + 1;
        int endDay = mEndOfWeek.get(Calendar.DAY_OF_MONTH);
        mWeekDateText.setText(String.format("Week of %d/%d to %d/%d",
                startMonth, startDay, endMonth, endDay));
    }

    /**
     * Get the goal hours for today and the total usage so far from
     * SharedPrefs and create a new entry for it. This allows us to
     * show current data for today so far on the weekly chart.
     **/
    private void updateTodayUsageIfNecessary(List<LocalDailyUsageEntry> dbQueryResult) {
        if (mWeeksAwayFromCurrent == 0) {
            Context context = getActivity();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String durationKey = context.getString(R.string.key_for_daily_duration);
            String limitKey = context.getString(R.string.key_for_daily_limitation);

            LocalDailyUsageEntry todayUsage = new LocalDailyUsageEntry();
            todayUsage.setDateTimeMS(Calendar.getInstance().getTimeInMillis());
            todayUsage.setTotalUsageMS(prefs.getLong(durationKey, 0));
            todayUsage.setGoalHoursMS(prefs.getLong(limitKey, 0));
            dbQueryResult.add(todayUsage);
        }
    }
}
