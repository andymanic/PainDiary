package com.paindiary;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.paindiary.application.GraphingManager;
import com.paindiary.domain.GraphData;
import com.paindiary.domain.PartOfDay;
import com.paindiary.domain.PartOfDayDistribution;
import com.paindiary.util.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GraphedEntriesActivity extends AppCompatActivity {

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
        CombinedChart chart = findViewById(R.id.painGraph);

        // hide the description in the bottom
        chart.setDescription(null);

        // disable axis lines
        //chart.getXAxis().setDrawGridLines(false);
        //chart.getAxisLeft().setDrawGridLines(false);
        //chart.getAxisRight().setDrawGridLines(false);

        // change X-axis labels to string representations of the PartOfDay enum, and place them at the bottom
        chart.getXAxis().setValueFormatter(new DateXAxisLabelFormatter());
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // we don't need values on the axis since they are being displayed on top of the bars
        chart.getAxisLeft().setDrawLabels(true);
        chart.getAxisRight().setDrawLabels(false);

        // we don't need a legend because there is just 1 series inside a column
        chart.getLegend().setEnabled(true);

        // disable selecting
        chart.setHighlightPerTapEnabled(false);
        chart.setHighlightPerDragEnabled(false);

        //Colour settings
        chart.getXAxis().setGridColor(Color.LTGRAY);
        chart.getAxisLeft().setGridColor(Color.LTGRAY);
        chart.getAxisRight().setGridColor(Color.LTGRAY);

        chart.getAxisLeft().setAxisMaximum(10);
        chart.getAxisRight().setAxisMaximum(10);
        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisRight().setAxisMinimum(0);
        chart.getAxisLeft().setGranularity(1);
        chart.getAxisRight().setGranularity(1);

    }

    private void configureNumberOfJointsChart() {
        CombinedChart chart = findViewById(R.id.jointsGraph);

        // hide the description in the bottom
        chart.setDescription(null);

        // disable axis lines
        //chart.getXAxis().setDrawGridLines(false);
        //chart.getAxisLeft().setDrawGridLines(false);
        //chart.getAxisRight().setDrawGridLines(false);

        // change X-axis labels to string representations of the PartOfDay enum, and place them at the bottom
        chart.getXAxis().setValueFormatter(new DateXAxisLabelFormatter());
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // we don't need values on the axis since they are being displayed on top of the bars
        chart.getAxisLeft().setDrawLabels(true);
        chart.getAxisRight().setDrawLabels(false);

        // we don't need a legend because there is just 1 series inside a column
        chart.getLegend().setEnabled(true);

        // disable selecting
        chart.setHighlightPerTapEnabled(false);
        chart.setHighlightPerDragEnabled(false);

        //Colour settings
        chart.getXAxis().setGridColor(Color.LTGRAY);
        chart.getAxisLeft().setGridColor(Color.LTGRAY);
        chart.getAxisRight().setGridColor(Color.LTGRAY);

        chart.getAxisLeft().setGranularity(1);
        chart.getAxisRight().setGranularity(1);

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
        chart.setTouchEnabled(false);
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
        private CombinedData _painData;
        private CombinedData _jointData;
        private BarData _timeOfDayDistributionData;

        @Override
        protected Void doInBackground(GetGraphDataPointParameters... parameters) {
            GetGraphDataPointParameters params = parameters[0];
            List<GraphData> dataPoints = GraphingManager.getInstance().get(params.fromDate, params.untilDate);

            LineDataSet _averagePainDataSet = new LineDataSet(new ArrayList<Entry>(), "Avg Pain");
            LineDataSet _averageJointDataSet = new LineDataSet(new ArrayList<Entry>(), "Avg Joint Count");
            ScatterDataSet _painDataSet = new ScatterDataSet(new ArrayList<Entry>(), "Pain Info");
            ScatterDataSet _jointDataSet = new ScatterDataSet(new ArrayList<Entry>(), "Joint Info");

            int avgColour = Color.rgb( 120, 154, 159);


            _averageJointDataSet.setLineWidth(4);
            _averageJointDataSet.setDrawCircles(false);
            _averagePainDataSet.setDrawCircles(false);

            _averageJointDataSet.setDrawValues(false);
            _averagePainDataSet.setLineWidth(4);

            _averagePainDataSet.setDrawValues(false);
            _averageJointDataSet.setColor(avgColour);
            _averagePainDataSet.setColor(avgColour);

            _painDataSet.setColor(Color.BLACK);
            _jointDataSet.setColor(Color.BLACK);
            _painDataSet.setDrawValues(false);
            _jointDataSet.setDrawValues(false);
            _painDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
            _jointDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);


            PartOfDayDistribution mergeDist = new PartOfDayDistribution();


            for (GraphData gd : dataPoints
                    ) {
                long dateValue = gd.getDate().getTime();
                mergeDist.merge(gd.getPartOfDayDist());

                Entry avgPainLvl = new Entry(dateValue, (float)gd.getAveragePainLevel());
                _averagePainDataSet.addEntry(avgPainLvl);

                Entry avgNoJoints = new Entry(dateValue, (float)gd.getAverageJountCount());
                _averageJointDataSet.addEntry(avgNoJoints);

                for (Integer p : gd.getLevels() ){
                    Entry painLvl = new Entry(dateValue, p);
                    _painDataSet.addEntry(painLvl);
                }

                for (Integer j : gd.getNumberOfJoints() ) {
                    Entry jointCount = new Entry(dateValue, j);
                    _jointDataSet.addEntry(jointCount);
                }
            }

            _painData = new CombinedData();
            _painData.setData(new LineData(Arrays.asList((ILineDataSet) _averagePainDataSet)));
            _painData.setData(new ScatterData(Arrays.asList((IScatterDataSet) _painDataSet)));

            _jointData = new CombinedData();
            _jointData.setData(new LineData(Arrays.asList((ILineDataSet) _averageJointDataSet)));
            _jointData.setData(new ScatterData(Arrays.asList((IScatterDataSet) _jointDataSet)));



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
            CombinedChart painGraph = findViewById(R.id.painGraph);
            CombinedChart jointGraph = findViewById(R.id.jointsGraph);
            BarChart barGraph = findViewById(R.id.timeDistribitionGraph);

            painGraph.removeAllViews();
            jointGraph.removeAllViews();
            barGraph.removeAllViews();

            painGraph.setData(_painData);
            jointGraph.setData(_jointData);
            barGraph.setData(_timeOfDayDistributionData);
        }
    }

    private class TimeDistributionXAxisLabelFormatter implements IAxisValueFormatter {

        public TimeDistributionXAxisLabelFormatter() { }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return PartOfDay.fromValue((int)value).toString();
        }
    }

    private class DateXAxisLabelFormatter implements IAxisValueFormatter {

        public DateXAxisLabelFormatter() { }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Date date = new Date((long)value);
            return new SimpleDateFormat("dd/MM").format(date);
        }
    }
}

