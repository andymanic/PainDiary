package com.paindiary;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.paindiary.application.JointUsageManager;
import com.paindiary.application.PainEntryManager;
import com.paindiary.data.Database;
import com.paindiary.domain.JointUsage;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the database instance
        Database.getInstance().loadPainDiaryDb(getApplicationContext());

        // Set view
        setContentView(R.layout.activity_main);

        // Configure UI elements
        configureCreateButton();
        configureViewButton();
        configureGraphButton();
    }

    private void configureCreateButton() {
        final Button btn = findViewById(R.id.btnCreate);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreatePainEntryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void configureViewButton() {
        final Button btn = findViewById(R.id.btnView);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewPainEntriesActivity.class);
                startActivity(intent);
            }
        });
    }
    private void configureGraphButton() {
        final Button btn = findViewById(R.id.btnGraph);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GraphedEntries.class);
                startActivity(intent);
            }
        });
    }

    public class ReadJointsUsagesTask extends AsyncTask<Void, Void, List<JointUsage>> {

        @Override
        protected List<JointUsage> doInBackground(Void... voids) {
            return JointUsageManager.getInstance().getAll();
        }

        @Override
        protected void onPostExecute(List<JointUsage> jointUsages) {
            super.onPostExecute(jointUsages);

            StringBuilder sb = new StringBuilder();
            for (JointUsage j : jointUsages) {
                if (sb.length() > 0)
                    sb.append("\n");
                sb.append(j.getDescription() + ": " + j.getCount() + "x");
            }

            Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public class ClearTestDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            PainEntryManager.getInstance().deleteAll();
            JointUsageManager.getInstance().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "All data deleted", Toast.LENGTH_SHORT).show();
        }
    }
}
