package com.paindiary;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.paindiary.application.GraphingManager;
import com.paindiary.domain.GraphData;
import com.paindiary.util.DateUtils;

import java.util.Date;
import java.util.List;

public class GraphedEntriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphed_entries);

        configureDataPoints();
        createGraph();

    }

    private void configureDataPoints(){
        Date fromDate = null;
        fromDate.getMonth();

        fetchPainEntriesForMonth(fromDate);
    }

    private void createGraph(){
        GraphView graph = (GraphView) findViewById(R.id.graph);


        graph.getViewport().setScalable(true);
        //graph.addSeries(jointSeries);
        //graph.addSeries(painSeries);
    }

    private void fetchPainEntriesForMonth(Date from) {
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

    private class GetGraphDataPoints extends AsyncTask<GetGraphDataPointParameters, Void, List<GraphData>> {

        @Override
        protected List<GraphData> doInBackground(GetGraphDataPointParameters... parameters) {
            GetGraphDataPointParameters p = parameters[0];
            return GraphingManager.getInstance().get(p.fromDate, p.untilDate);
        }

        @Override
        protected void onPostExecute(List<GraphData> dataPoints) {
            super.onPostExecute(dataPoints);
/*
            LineGraphSeries<DataPoint> averagePainSeries = new LineGraphSeries<>();
            LineGraphSeries<DataPoint> averageJointSeries = new LineGraphSeries<>();
            PointsGraphSeries<DataPoint> painSeries = new PointsGraphSeries<>();
            PointsGraphSeries<DataPoint> jointSeries = new PointsGraphSeries<>();
            BarGraphSeries<DataPoint> timeOfDayDis = new BarGraphSeries<>();

            averagePainSeries = formatSeries(averagePainSeries);
            averageJointSeries = formatSeries(averageJointSeries);

            painSeries.setColor(Color.rgb(193, 54, 54));
            jointSeries.setColor(Color.rgb(89, 193, 48));

            for (GraphData p : dataPoints
                 ) {
                Date date = p.getDate();
                int painLevel = p.getLevels();
                int jointCount = p.getNumberOfJoints();

                DataPoint dataPoint = new DataPoint(date, (double) painLevel);

                painSeries.appendData(new DataPoint(date, (double)painLevel), true, 5);


            }
        }
        private LineGraphSeries<DataPoint> formatSeries(LineGraphSeries<DataPoint> series){
            series.setDrawDataPoints(false);
            series.setThickness(8);

            return series;
        }
        private PointsGraphSeries<DataPoint> formatSeries(PointsGraphSeries<DataPoint> series){
            series.setSize(10);


            return series;
        }*/
        }
    }
}
