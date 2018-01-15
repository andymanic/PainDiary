package com.paindiary;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.paindiary.application.PainEntryManager;
import com.paindiary.domain.PainEntry;
import com.paindiary.util.DateUtils;
import com.paindiary.util.UserInterfaceUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewPainEntriesActivity extends AppCompatActivity {

    private ArrayList<PainEntry> _entriesList = new ArrayList<>();
    private PainEntriesDataAdapter _entriesDataAdapter;

    private SimpleDateFormat monthLabelFormat = new SimpleDateFormat("MMM/yy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pain_entries);

        configureMonthLabel();
        configureFiltersButton();
        configureCalendar();
        configureEntriesList();

        fetchPainEntriesForMonth(((CompactCalendarView) findViewById(R.id.calendar)).getFirstDayOfCurrentMonth());
    }

    private void configureMonthLabel() {
        setMonthLabel(new Date());
    }

    private void configureFiltersButton() {

    }

    private void configureCalendar() {
        CompactCalendarView calendar = findViewById(R.id.calendar);
        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                fetchPainEntryDetailsForSelectedDay(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setMonthLabel(firstDayOfNewMonth);
                fetchPainEntriesForMonth(firstDayOfNewMonth);
            }
        });
    }

    private void configureEntriesList() {
        _entriesDataAdapter = new PainEntriesDataAdapter(this, R.layout.pain_entry);
        ListView lstEntries = findViewById(R.id.lstEntries);
        lstEntries.setAdapter(_entriesDataAdapter);
        lstEntries.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ViewPainEntriesActivity.this, EditPainEntryActivity.class);
                Bundle b = new Bundle();
                b.putLong(CreateEditPainEntryActivityBase.PAIN_ENTRY_ID_KEY, ((PainEntriesDataAdapter.ViewHolder) view.getTag()).id);
                intent.putExtras(b);
                startActivityForResult(intent, 1); // start for result will trigger the onResult callback which refreshes the details list to display the updated data

                return true;
            }
        });

        fetchPainEntryDetailsForSelectedDay(new Date());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent refresh = new Intent(this, ViewPainEntriesActivity.class);
            startActivity(refresh);
            this.finish();
        }
    }

    private void fetchPainEntriesForMonth(Date from) {
        new GetCalendarEntriesTask().execute(new GetPainEntriesTaskParameters(from, DateUtils.addMonth(from), 0, ""));
    }

    private void fetchPainEntryDetailsForSelectedDay(Date day) {
        new GetEntriesDetailsTask().execute(new GetPainEntriesTaskParameters(DateUtils.startOfDay(day), DateUtils.endOfDay(day), 0, ""));
    }

    private void setMonthLabel(Date date) {
        ((TextView) findViewById(R.id.lblMonth)).setText(monthLabelFormat.format(date));
    }

    private void setCalendarEntries(List<PainEntry> painEntries) {
        CompactCalendarView calendar = findViewById(R.id.calendar);
        calendar.removeAllEvents();
        List<Event> events = new ArrayList<>();
        for (PainEntry p : painEntries) {
            Event e = new Event(UserInterfaceUtils.mapPainLevelToColor(p.getPainLevel()), p.getDate().getTime(), p);
            events.add(e);
        }
        calendar.addEvents(events);
    }

    private class GetCalendarEntriesTask extends AsyncTask<GetPainEntriesTaskParameters, Void, List<PainEntry>> {

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
            setCalendarEntries(entries);
        }
    }

    private class GetEntriesDetailsTask extends AsyncTask<GetPainEntriesTaskParameters, Void, List<PainEntry>> {

        private Date date = null;

        @Override
        protected List<PainEntry> doInBackground(GetPainEntriesTaskParameters... parameters) {
            List<PainEntry> results = new ArrayList<>();
            List<Long> resultIds = new ArrayList<>();
            for (GetPainEntriesTaskParameters p : parameters) {
                List<PainEntry> entries = PainEntryManager.getInstance().getAll(p.fromDate, p.untilDate, p.level);
                date = p.fromDate;
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
            _entriesList.clear();
            _entriesList.addAll(entries);
            _entriesDataAdapter.notifyDataSetChanged();
            UserInterfaceUtils.setListViewHeightBasedOnItems((ListView) findViewById(R.id.lstEntries));

            TextView lblEntryCount = findViewById(R.id.lblEntryCount);
            lblEntryCount.setText("Showing " + _entriesList.size() + " entr" + (_entriesList.size() != 1 ? "ies" : "y") + " on " + DateFormat.getDateFormat(getApplicationContext()).format(date));
        }
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

    private class PainEntriesDataAdapter extends ArrayAdapter<PainEntry> {

        public PainEntriesDataAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, _entriesList);
        }

        private class ViewHolder {
            long id;
            TextView level;
            TextView time;
            TextView affectedJoints;
            TextView joints;
            TextView comments;
        }

        @Override
        public void notifyDataSetChanged() {

            Collections.sort(_entriesList, new Comparator<PainEntry>() {
                @Override
                public int compare(PainEntry e1, PainEntry e2) {
                    if (e1 == null || e2 == null) return 0;
                    return e1.getDate().compareTo(e2.getDate());
                }
            });
            super.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.pain_entry, null);

                holder = new ViewHolder();
                holder.level = convertView.findViewById(R.id.lblLevel);
                holder.time = convertView.findViewById(R.id.lblTime);
                holder.affectedJoints = convertView.findViewById(R.id.lblAffectedJoints);
                holder.joints = convertView.findViewById(R.id.lblJoints);
                holder.comments = convertView.findViewById(R.id.lblComments);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (_entriesList.size() > position) {
                PainEntry entry = _entriesList.get(position);

                holder.level.setText(Integer.toString(entry.getPainLevel()));
                holder.level.setBackgroundColor(UserInterfaceUtils.mapPainLevelToColor(entry.getPainLevel()));
                holder.level.setTextColor(UserInterfaceUtils.mapPainLevelToContrastingColor(entry.getPainLevel()));

                holder.time.setText(android.text.format.DateFormat.getTimeFormat(getApplicationContext()).format(entry.getDate()));

                StringBuilder sb = new StringBuilder();
                String[] joints = entry.getJoints();
                Arrays.sort(joints);
                for (String j : joints) {
                    if (sb.length() > 0)
                        sb.append("\n");
                    sb.append("> ");
                    sb.append(j);
                }
                holder.joints.setText(sb.toString());
                holder.affectedJoints.setText("Affected joints (" + entry.getJoints().length + ")");

                holder.comments.setText(entry.getComment());
                if (holder.comments.getText().toString().isEmpty())
                    holder.comments.setVisibility(View.GONE);
                else
                    holder.comments.setVisibility(View.VISIBLE);

                holder.id = entry.getId();
            }

            return convertView;
        }
    }
}
