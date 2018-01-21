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
        configureSettingButton();
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
                Intent intent = new Intent(getApplicationContext(), GraphedEntriesActivity.class);
                startActivity(intent);
            }
        });
    }
    private void configureSettingButton() {
        final Button btn = findViewById(R.id.btnSettings);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });
    }
}
