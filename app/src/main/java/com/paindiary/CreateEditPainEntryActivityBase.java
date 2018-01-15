package com.paindiary;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.paindiary.application.JointUsageManager;
import com.paindiary.application.PainEntryManager;
import com.paindiary.domain.JointUsage;
import com.paindiary.domain.PainEntry;
import com.paindiary.util.StringUtils;
import com.paindiary.util.UserInterfaceUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class CreateEditPainEntryActivityBase extends AppCompatActivity {

    private ArrayList<SelectableJointWithUsageInformation> _jointSelectionList = new ArrayList<>();
    private JointsDataAdapter _jointsDataAdapter;

    public static final String PAIN_ENTRY_ID_KEY = "PainEntryId";
    private long _id;

    private boolean _saving = false;

    protected abstract Context getContext();

    protected abstract String getSaveButtonText();

    protected abstract boolean supportsDeleteButton();

    protected abstract void processEntry(PainEntryTaskParameters parameters);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_pain_entry);

        configureDateInput();
        configureTimeInput();
        configureLevelSlider();
        configureJointsList();
        configureAddJointButton();

        configureCancelButton();
        configureSaveButton();
        configureDeleteButton();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            _id = b.getLong(PAIN_ENTRY_ID_KEY);
            fetchExistingEntry(_id);
        }
    }

    private void configureDateInput() {
        EditText txtDate = findViewById(R.id.txtDate);
        txtDate.setText(DateFormat.getDateFormat(getApplicationContext()).format(new Date()));
        txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar newCalendar = Calendar.getInstance();
                    new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            ((EditText) findViewById(R.id.txtDate)).setText(DateFormat.getDateFormat(getApplicationContext()).format(newDate.getTime()));
                        }
                    }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    clearFocus();
                }
            }
        });
    }

    private void configureTimeInput() {
        EditText txtTime = findViewById(R.id.txtTime);
        txtTime.setText(DateFormat.getTimeFormat(getApplicationContext()).format(new Date()));
        txtTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar newCalendar = Calendar.getInstance();
                    new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(0, 0, 0, i, i1);
                            ((EditText) findViewById(R.id.txtTime)).setText(DateFormat.getTimeFormat(getApplicationContext()).format(newDate.getTime()));
                        }
                    }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext())).show();
                    clearFocus();
                }
            }
        });
    }

    private void configureLevelSlider() {
        SeekBar sbLevel = findViewById(R.id.sbLevel);
        sbLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setLevelSliderValue(i);
                setLevelTextValue(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setLevelSliderValue(3);
    }

    private void configureJointsList() {
        _jointsDataAdapter = new JointsDataAdapter(this, R.layout.joints_selection);
        ListView lstJoints = findViewById(R.id.lstJoints);
        lstJoints.setAdapter(_jointsDataAdapter);

        new GetJointUsagesTask().execute();
    }

    private void configureAddJointButton() {
        Button btn = findViewById(R.id.btnAddJoint);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Create new joint");

                final EditText input = new EditText(getContext());
                builder.setView(input);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectableJointWithUsageInformation candidate = new SelectableJointWithUsageInformation(new JointUsage(input.getText().toString().toLowerCase(), 0), true);

                        for (SelectableJointWithUsageInformation info : _jointSelectionList) {
                            if (StringUtils.areSameWords(info.getJointUsage().getDescription(), candidate.getJointUsage().getDescription())) {
                                info.setSelected(true);
                                _jointsDataAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Similar description '" + info.getJointUsage().getDescription() + "' found, existing joint selected instead.", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        _jointSelectionList.add(candidate);
                        _jointsDataAdapter.notifyDataSetChanged();
                        UserInterfaceUtils.setListViewHeightBasedOnItems((ListView) findViewById(R.id.lstJoints));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private void configureCancelButton() {
        Button btn = findViewById(R.id.btnCancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirm")
                        .setMessage("Any changes you have made are not yet saved. If you cancel, the entered data will be lost. Do you want proceed?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Discard changes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        })
                        .setNegativeButton("Go back", null).show();
            }
        });
    }

    private void configureSaveButton() {
        Button btn = findViewById(R.id.btnSave);
        btn.setText(getSaveButtonText());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!_saving) {
                    _saving = true;

                    Date date;
                    String dateString = ((EditText) findViewById(R.id.txtDate)).getText().toString();
                    try {
                        date = DateFormat.getDateFormat(getApplicationContext()).parse(dateString);
                    } catch (ParseException p) {
                        Toast.makeText(getApplicationContext(), "Unable to process date '" + dateString + "'. Please ensure correct values are entered and try again", Toast.LENGTH_LONG).show();

                        _saving = false;
                        return;
                    }

                    Date time;
                    String timeString = ((EditText) findViewById(R.id.txtTime)).getText().toString();
                    try {
                        time = DateFormat.getTimeFormat(getApplicationContext()).parse(timeString);
                    } catch (ParseException p) {
                        Toast.makeText(getApplicationContext(), "Unable to process time '" + timeString + "'. Please ensure correct values are entered and try again", Toast.LENGTH_LONG).show();

                        _saving = false;
                        return;
                    }

                    Date dateTime = new Date(date.getYear(), date.getMonth(), date.getDate(), time.getHours(), time.getMinutes());
                    int level = ((SeekBar) findViewById(R.id.sbLevel)).getProgress();
                    String comment = ((EditText) findViewById(R.id.txtComments)).getText().toString();
                    List<String> selectedJoints = new ArrayList<>();
                    for (SelectableJointWithUsageInformation info : _jointSelectionList) {
                        if (info.isSelected())
                            selectedJoints.add(info.getJointUsage().getDescription());
                    }

                    processEntry(new PainEntryTaskParameters(_id, dateTime, level, comment, selectedJoints.toArray(new String[selectedJoints.size()])));

                    setResult(RESULT_OK); // triggers refresh on overviewpage
                    finish();
                }
            }
        });
    }

    private void configureDeleteButton() {
        Button btn = findViewById(R.id.btnDelete);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirm")
                        .setMessage("Do you really want to delete the current entry?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    new DeletePainEntryTask().execute(_id).get();
                                    setResult(RESULT_OK); // triggers refresh on overviewpage
                                    finish();
                                } catch (InterruptedException e) {
                                    Toast.makeText(getApplicationContext(), "Delete was interrupted", Toast.LENGTH_LONG).show();
                                } catch (ExecutionException e) {
                                    Toast.makeText(getApplicationContext(), "Delete failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("Go back", null).show();
            }
        });

        if (!supportsDeleteButton()) {
            btn.setVisibility(View.GONE);
        }
    }

    private void setLevelTextValue(int value) {
        TextView txtLevel = findViewById(R.id.txtLevel);
        txtLevel.setText(Integer.toString(value));
        txtLevel.setTextColor(UserInterfaceUtils.mapPainLevelToColor(value));
    }

    private void setLevelSliderValue(int value) {
        SeekBar sbLevel = findViewById(R.id.sbLevel);
        sbLevel.setProgress(value);
        sbLevel.getProgressDrawable().setColorFilter(UserInterfaceUtils.mapPainLevelToColor(value), PorterDuff.Mode.SRC_IN);
        sbLevel.getThumb().setColorFilter(UserInterfaceUtils.mapPainLevelToColor(value), PorterDuff.Mode.SRC_IN);
    }

    private void fetchExistingEntry(long id) {
        new GetPainEntryTask().execute(id);
    }

    private void fillFieldsWithEntryData(PainEntry entry) {
        // Date
        EditText txtDate = findViewById(R.id.txtDate);
        txtDate.setText(DateFormat.getDateFormat(getContext()).format(entry.getDate()));

        // Time
        EditText txtTime = findViewById(R.id.txtTime);
        txtTime.setText(DateFormat.getTimeFormat(getContext()).format(entry.getDate()));

        // Level
        setLevelSliderValue(entry.getPainLevel());
        setLevelTextValue(entry.getPainLevel());

        // Joints
        for (SelectableJointWithUsageInformation info : _jointSelectionList) {
            if (entry.hasJoint(info.getJointUsage().getDescription())) {
                info.setSelected(true);
                info.getJointUsage().decrementJointUsage(); // don't include the current selection in the final usage count
            } else {
                info.setSelected(false);
            }
        }
        _jointsDataAdapter.notifyDataSetChanged();

        // Comments
        EditText txtComments = findViewById(R.id.txtComments);
        txtComments.setText(entry.getComment());
    }

    protected class PainEntryTaskParameters {

        public long id;
        public Date date;
        public int level;
        public String comment;
        public String[] joints;

        public PainEntryTaskParameters(long id, Date date, int level, String comment, String... joints) {
            this.id = id;
            this.date = date;
            this.level = level;
            this.comment = comment;
            this.joints = joints;
        }
    }

    private class GetPainEntryTask extends AsyncTask<Long, Void, PainEntry> {

        @Override
        protected PainEntry doInBackground(Long... ids) {
            return PainEntryManager.getInstance().get(ids[0]);
        }

        @Override
        protected void onPostExecute(PainEntry painEntry) {
            fillFieldsWithEntryData(painEntry);
        }
    }

    private class DeletePainEntryTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... ids) {
            PainEntryManager.getInstance().delete(ids[0]);
            return null;
        }
    }

    private class GetJointUsagesTask extends AsyncTask<Void, Void, List<JointUsage>> {

        @Override
        protected List<JointUsage> doInBackground(Void... voids) {
            return JointUsageManager.getInstance().getAllByUsage();
        }

        @Override
        protected void onPostExecute(List<JointUsage> jointUsages) {
            super.onPostExecute(jointUsages);
            for (JointUsage j : jointUsages) {
                _jointSelectionList.add(new SelectableJointWithUsageInformation(j));
            }
            _jointsDataAdapter.notifyDataSetChanged();
            UserInterfaceUtils.setListViewHeightBasedOnItems((ListView) findViewById(R.id.lstJoints));
        }
    }

    private class SelectableJointWithUsageInformation {
        private JointUsage _jointUsage;

        public JointUsage getJointUsage() {
            return _jointUsage;
        }

        private void setJointUsage(JointUsage jointUsage) {
            _jointUsage = jointUsage;
        }

        private boolean _selected;

        public boolean isSelected() {
            return _selected;
        }

        public void setSelected(boolean selected) {
            _selected = selected;
        }

        SelectableJointWithUsageInformation(JointUsage jointUsage) {
            this(jointUsage, false);
        }

        SelectableJointWithUsageInformation(JointUsage jointUsage, boolean selected) {
            setJointUsage(jointUsage);
            setSelected(selected);
        }
    }

    private class JointsDataAdapter extends ArrayAdapter<SelectableJointWithUsageInformation> {

        public JointsDataAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, _jointSelectionList);
        }

        private class ViewHolder {
            CheckBox joint;
            TextView usage;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.joints_selection, null);

                holder = new ViewHolder();
                holder.joint = convertView.findViewById(R.id.joint);
                holder.usage = convertView.findViewById(R.id.usage);

                holder.joint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((SelectableJointWithUsageInformation) view.getTag()).setSelected(((CheckBox) view).isChecked());
                    }
                });

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (_jointSelectionList.size() > position) {
                SelectableJointWithUsageInformation info = _jointSelectionList.get(position);
                holder.joint.setText(info.getJointUsage().getDescription());
                holder.joint.setChecked(info.isSelected());
                holder.joint.setTag(info);
                holder.usage.setText(" (" + info.getJointUsage().getCount() + " previous entr" + (info.getJointUsage().getCount() != 1 ? "ies" : "y") + ")");
                holder.usage.setTag(info);
            }

            return convertView;
        }
    }

    private void clearFocus() {
        findViewById(R.id.dummy).requestFocus();
    }
}