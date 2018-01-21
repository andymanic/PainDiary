package com.paindiary;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.paindiary.application.GraphingManager;
import com.paindiary.domain.GraphData;
import com.paindiary.domain.PartOfDay;
import com.paindiary.domain.PartOfDayDistribution;
import com.paindiary.util.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GraphedEntriesActivity extends AppCompatActivity {

    private static final int MAX_DATAPOINT_COUNT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphed_entries);

        configurePainLevelChart();
        configureNumberOfJointsChart();
        configureTimeDistributionChart();

        fetchLastPainEntries();
    }

    private void configurePainLevelChart() {

    }

    private void configureNumberOfJointsChart() {

    }

    private void configureTimeDistributionChart() {
        BarChart chart = findViewById(R.id.timeDistribitionGraph);

        // hide the description in the bottom
        chart.setDescription(null);

        // disable axis lines
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);

        // change X-axis labels to string representations of the PartOfDay enum, and place them at the bottom
        chart.getXAxis().setValueFormatter(new TimeDistributionXAxisLabelFormatter());
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // we don't need values on the axis since they are being displayed on top of the bars
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);

        // we don't need a legend because there is just 1 series inside a column
        chart.getLegend().setEnabled(false);

        // disable selecting
        chart.setHighlightPerTapEnabled(false);
        chart.setHighlightFullBarEnabled(false);
        chart.setHighlightPerDragEnabled(false);
    }

    private void fetchLastPainEntries() {
        fetchPainEntries(new Date());
    }

    private void fetchPainEntries(Date to){
        fetchPainEntries(DateUtils.subtractMonth(to), to);
    }

    private void fetchPainEntries(Date from, Date to) {
        new GraphedEntriesActivity.GetGraphDataPoints().execute(new GraphedEntriesActivity.GetGraphDataPointParameters(from, DateUtils.addMonth(from)));
    }
    private class GetGraphDataPointParameters {
        public Date fromDate;
        public Date untilDate;

        public GetGraphDataPointParameters(Date fromDate, Date untilDate) {
            this.fromDate = fromDate;
            this.untilDate = untilDate;

        }
    }

    private class GetGraphDataPoints extends AsyncTask<GetGraphDataPointParameters, Void, Void> {
        private LineGraphSeries<DataPoint> _averagePainSeries;
        private LineGraphSeries<DataPoint> _averageJointSeries;
        private PointsGraphSeries<DataPoint> _painSeries;
        private PointsGraphSeries<DataPoint> _jointSeries;
        private BarData _timeOfDayDistributionData;

        private Date first;
        private Date last;

        private int maxJoints;

        @Override
        protected Void doInBackground(GetGraphDataPointParameters... parameters) {
            GetGraphDataPointParameters params = parameters[0];
            List<GraphData> dataPoints = GraphingManager.getInstance().get(params.fromDate, params.untilDate);

            _averagePainSeries = new LineGraphSeries<>();
            _averageJointSeries = new LineGraphSeries<>();
            _painSeries = new PointsGraphSeries<>();
            _jointSeries = new PointsGraphSeries<>();

            _averagePainSeries = formatSeries(_averagePainSeries);
            _averageJointSeries = formatSeries(_averageJointSeries);
            _painSeries = formatSeries(_painSeries);
            _jointSeries = formatSeries(_jointSeries);

            //_painSeries.setColor(Color.rgb(193, 54, 54));
            _painSeries.setColor(Color.BLACK);
            _averagePainSeries.setColor(Color.rgb(193, 54, 54));
            //_jointSeries.setColor(Color.rgb(89, 193, 48));
            _jointSeries.setColor(Color.BLACK);
            _averageJointSeries.setColor(Color.rgb(89, 193, 48));

            PartOfDayDistribution mergeDist = new PartOfDayDistribution();

            maxJoints = 0;
            for (GraphData gd : dataPoints
                    ) {
                Date date = gd.getDate();
                mergeDist.merge(gd.getPartOfDayDist());

                DataPoint avgPainLvl = new DataPoint(date, gd.getAveragePainLevel());
                _averagePainSeries.appendData(avgPainLvl, true, MAX_DATAPOINT_COUNT);

                DataPoint avgNoJoints = new DataPoint(date, gd.getAverageJountCount());
                _averageJointSeries.appendData(avgNoJoints, true, MAX_DATAPOINT_COUNT);

                for (Integer p : gd.getLevels() ){
                    DataPoint painLvl = new DataPoint(date, p);
                    _painSeries.appendData(painLvl, true, MAX_DATAPOINT_COUNT);
                }

                for (Integer j : gd.getNumberOfJoints() ) {
                    maxJoints = Math.max(maxJoints, j);
                    DataPoint jointCount = new DataPoint(date, j);
                    _jointSeries.appendData(jointCount, true, MAX_DATAPOINT_COUNT);
                }
            }

            // Generate the time distribution data sets and assign them to the general distribution bar data
            int regularColour = Color.rgb( 120, 154, 159);
            int highColour = Color.rgb( 210, 54, 65 );
            int lowColour = Color.rgb(65, 146, 75 );

            List<PartOfDay> highParts = mergeDist.getPartsOfDayWithHighestValue();
            List<PartOfDay> lowParts = mergeDist.getPartsOfDayWithLowestValue();

            List<IBarDataSet> barDataSets = new ArrayList<>();

            for (PartOfDay pod: PartOfDay.values()) {
                BarDataSet ds = new BarDataSet(
                        Arrays.asList(new BarEntry(pod.getValue(), mergeDist.getValue(pod))), // Only accepts a list even though we have just 1 element... used Arrays.asList with a single element array to work around this
                        pod.toString()
                );

                if(highParts.contains(pod))
                    ds.setColor(highColour);
                else if(lowParts.contains(pod))
                    ds.setColor(lowColour);
                else
                    ds.setColor(regularColour);

                barDataSets.add(ds);
            }

            _timeOfDayDistributionData = new BarData(barDataSets);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            GraphView painGraph = findViewById(R.id.painGraph);
            GraphView jointGraph = findViewById(R.id.jointsGraph);
            BarChart barGraph = findViewById(R.id.timeDistribitionGraph);

            painGraph.removeAllSeries();
            jointGraph.removeAllSeries();
            barGraph.removeAllViews();

            painGraph.addSeries(_averagePainSeries);
            painGraph.addSeries(_painSeries);
            jointGraph.addSeries(_averageJointSeries);
            jointGraph.addSeries(_jointSeries);

            barGraph.setData(_timeOfDayDistributionData);
        }

        private LineGraphSeries<DataPoint> formatSeries(LineGraphSeries<DataPoint> series){
            series.setThickness(8);
            series.setDataPointsRadius(5);
            series.setDrawDataPoints(false);
            return series;
        }
        private PointsGraphSeries<DataPoint> formatSeries(PointsGraphSeries<DataPoint> series){
            series.setSize(5);
            return series;
        }
    }

    private class TimeDistributionXAxisLabelFormatter implements IAxisValueFormatter {

        public TimeDistributionXAxisLabelFormatter() { }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return PartOfDay.fromValue((int)value).toString();
        }
    }
}

