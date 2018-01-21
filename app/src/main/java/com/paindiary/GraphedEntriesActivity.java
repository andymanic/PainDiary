package com.paindiary;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.paindiary.application.GraphingManager;
import com.paindiary.domain.GraphData;
import com.paindiary.util.DateUtils;
import com.paindiary.util.PartOfDay;
import com.paindiary.util.PartOfDayDistribution;

import java.util.Date;
import java.util.List;

public class GraphedEntriesActivity extends AppCompatActivity {

    private static final int MAXDATAPOINTCOUNT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphed_entries);

        configureGraphs();

        fetchLastPainEntries();


    }


    private void configureGraphs(){
        GraphView painGraph = (GraphView) findViewById(R.id.painGraph);
        GraphView jointGraph = (GraphView) findViewById(R.id.jointsGraph);
        GraphView barGraph = (GraphView) findViewById(R.id.barGraph);


        setupGraphView(painGraph);
        setupGraphView(jointGraph);

    }

    private void setupGraphView(GraphView graph){
        graph.getViewport().setScalable(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(GraphedEntriesActivity.this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);


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
        private BarGraphSeries<DataPoint> _timeOfDayDis;

        private Date first;
        private Date last;

        @Override
        protected Void doInBackground(GetGraphDataPointParameters... parameters) {
            GetGraphDataPointParameters params = parameters[0];
            List<GraphData> dataPoints = GraphingManager.getInstance().get(params.fromDate, params.untilDate);

            _averagePainSeries = new LineGraphSeries<>();
            _averageJointSeries = new LineGraphSeries<>();
            _painSeries = new PointsGraphSeries<>();
            _jointSeries = new PointsGraphSeries<>();
            _timeOfDayDis = new BarGraphSeries<>();

            _averagePainSeries = formatSeries(_averagePainSeries);
            _averageJointSeries = formatSeries(_averageJointSeries);
            _painSeries = formatSeries(_painSeries);
            _jointSeries = formatSeries(_jointSeries);

            _painSeries.setColor(Color.rgb(193, 54, 54));
            _averagePainSeries.setColor(Color.rgb(193, 54, 54));
            _jointSeries.setColor(Color.rgb(89, 193, 48));
            _averageJointSeries.setColor(Color.rgb(89, 193, 48));

            PartOfDayDistribution mergeDist = new PartOfDayDistribution();

            for (GraphData gd : dataPoints
                    ) {
                Date date = gd.getDate();
                mergeDist.merge(gd.getPartOfDayDist());

                DataPoint avgPainLvl = new DataPoint(date, gd.getAveragePainLevel());
                _averagePainSeries.appendData(avgPainLvl, true, MAXDATAPOINTCOUNT);

                DataPoint avgNoJoints = new DataPoint(date, gd.getAverageJountCount());
                _averageJointSeries.appendData(avgNoJoints, true, MAXDATAPOINTCOUNT);

                for (Integer p : gd.getLevels() ){
                    DataPoint painLvl = new DataPoint(date, p);
                    _painSeries.appendData(painLvl, true, MAXDATAPOINTCOUNT);
                }

                for (Integer j : gd.getNumberOfJoints() ){
                    DataPoint jointCount = new DataPoint(date, j);
                    _jointSeries.appendData(jointCount, true, MAXDATAPOINTCOUNT);
                }
            }

            for (PartOfDay pod: PartOfDay.values()
                    ) {
                int y = mergeDist.getValue(pod);
                DataPoint painDist = new DataPoint(pod.getValue(), y);
                _timeOfDayDis.appendData(painDist, true, 6);
            }

            first = dataPoints.get(0).getDate();
            last = dataPoints.get(dataPoints.size() - 1).getDate();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            GraphView painGraph = (GraphView) findViewById(R.id.painGraph);
            GraphView jointGraph = (GraphView) findViewById(R.id.jointsGraph);
            GraphView barGraph = (GraphView) findViewById(R.id.barGraph);

            painGraph.removeAllSeries();
            jointGraph.removeAllSeries();
            barGraph.removeAllSeries();

            painGraph.addSeries(_averagePainSeries);
            //painGraph.addSeries(_painSeries);
            jointGraph.addSeries(_averageJointSeries);
            //jointGraph.addSeries(_jointSeries);
            barGraph.addSeries(_timeOfDayDis);

            setupXDateLabels(painGraph);
            setupXDateLabels(jointGraph);

        }
        private GraphView setupXDateLabels(GraphView graph){
            graph.getViewport().setMinX(first.getTime());
            graph.getViewport().setMaxX(last.getTime());
            graph.getViewport().setXAxisBoundsManual(true);
            return graph;
        }

        private LineGraphSeries<DataPoint> formatSeries(LineGraphSeries<DataPoint> series){
            series.setThickness(8);
            series.setDataPointsRadius(10);
            series.setDrawDataPoints(true);
            return series;
        }
        private PointsGraphSeries<DataPoint> formatSeries(PointsGraphSeries<DataPoint> series){
            series.setSize(10);
            return series;
        }
    }
}

