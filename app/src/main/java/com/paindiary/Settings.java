package com.paindiary;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.paindiary.util.AlarmReceiver;

import java.util.Calendar;
import java.util.Date;

public class Settings extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        configureTimeInput();
    }
    public void sendNotification(View view) {
        int delay;
        int id = 001;

        EditText txtTime = findViewById(R.id.txtTime);
        delay = Integer.parseInt(txtTime.getText().toString());
        scheduleNotification(this, delay, id);

        /*NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification")
                .setAutoCancel(true);

        Intent notificationIntent = new Intent(this, CreatePainEntryActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
*/
    }

    public void scheduleNotification(Context context, long delay, int notificationId) {//delay is after how much time(in millis) from current time you want to schedule the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Add an entry for today!")
                .setContentText("Don't forget to add a pain entry for today!")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent intent = new Intent(context, CreatePainEntryActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
    private void configureTimeInput() {
        EditText txtTime = findViewById(R.id.txtTime);
        txtTime.setText(DateFormat.getTimeFormat(getApplicationContext()).format(new Date()));
        txtTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar newCalendar = Calendar.getInstance();
                    new TimePickerDialog(getApplication(), new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(0, 0, 0, i, i1);
                            ((EditText) findViewById(R.id.txtTime)).setText(DateFormat.getTimeFormat(getApplicationContext()).format(newDate.getTime()));
                        }
                    }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplication())).show();
                    clearFocus();
                }
            }
        });
    }

    private void clearFocus() {
        findViewById(R.id.dummy).requestFocus();
    }
}
