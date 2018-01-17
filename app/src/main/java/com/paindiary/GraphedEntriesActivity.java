package com.paindiary;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.paindiary.application.PainEntryManager;
import com.paindiary.domain.PainEntry;
import com.paindiary.util.DateUtils;

import java.util.ArrayList;
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

        fetchPainEntriesForMonth(fromDate);
    }

    private void createGraph(){
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
            //Creating the DataPoint array to fill the graph. We need to do this twice as we want 2 series
        });
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);

        graph.getViewport().setScalable(true);
        graph.addSeries(series);
    }

    private void fetchPainEntriesForMonth(Date from) {
        new GraphedEntriesActivity.GetEntriesTask().execute(new GraphedEntriesActivity.GetPainEntriesTaskParameters(from, DateUtils.addMonth(from), 0, ""));
    }
    private class GetPainEntriesTaskParameters {

        public Date fromDate;
        public Date untilDate;
        public int level;
        public String joint;

        public GetPainEntriesTaskParameters(Date fromDate, Date untilDate, int level, String joint) {
            this.fromDate = fromDate;
            this.untilDate = untilDate;
            this.level = level;
            this.joint = joint;
        }
    }

    private class GetEntriesTask extends AsyncTask<GetPainEntriesTaskParameters, Void, List<PainEntry>> {

        @Override
        protected List<PainEntry> doInBackground(GetPainEntriesTaskParameters... parameters) {
            List<PainEntry> results = new ArrayList<>();
            List<Long> resultIds = new ArrayList<>();
            for (GetPainEntriesTaskParameters p : parameters) {
                List<PainEntry> entries = PainEntryManager.getInstance().getAll(p.fromDate, p.untilDate, p.level);

                for (PainEntry e : entries) {
                    if ((p.joint.isEmpty() || e.hasJoint(p.joint)) && !resultIds.contains(e.getId())) {
                        results.add(e);
                        resultIds.add(e.getId());
                    }
                }
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<PainEntry> entries) {
            super.onPostExecute(entries);
            //TODO
        }
    }
}
