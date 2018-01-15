package com.paindiary;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.paindiary.application.PainEntryManager;
import com.paindiary.domain.PainEntry;

import java.util.ArrayList;
import java.util.List;

public class EditPainEntryActivity extends CreateEditPainEntryActivityBase {

    @Override
    protected Context getContext() {
        return EditPainEntryActivity.this;
    }

    @Override
    protected String getSaveButtonText() {
        return "update";
    }

    @Override
    protected boolean supportsDeleteButton() {
        return true;
    }

    @Override
    protected void processEntry(PainEntryTaskParameters parameters) {
        new UpdatePainEntryTask().execute(parameters);
    }

    private class UpdatePainEntryTask extends AsyncTask<PainEntryTaskParameters, Void, List<PainEntry>> {

        @Override
        protected List<PainEntry> doInBackground(PainEntryTaskParameters... parameters) {
            List<PainEntry> results = new ArrayList<>();
            for (PainEntryTaskParameters p : parameters) {
                results.add(PainEntryManager.getInstance().update(new PainEntry(p.id, p.date, p.level, p.comment, p.joints)));
            }

            return results;
        }

        @Override
        protected void onPostExecute(List<PainEntry> entries) {
            super.onPostExecute(entries);
            for (PainEntry e : entries) {
                Toast.makeText(getApplicationContext(),
                        "Entry " + e.getId() +
                                " on " + DateFormat.getDateFormat(getApplicationContext()).format(e.getDate()) +
                                " at " + DateFormat.getTimeFormat(getApplicationContext()).format(e.getDate()) +
                                " updated", Toast.LENGTH_SHORT).show();
            }
        }
    }
}