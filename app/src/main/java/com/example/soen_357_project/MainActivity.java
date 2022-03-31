package com.example.soen_357_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private TextView alarmSetForTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmSetForTextView = findViewById(R.id.alarmSetForTextView);

        Button addAlarmButton = findViewById(R.id.AddAlarmButton);
        addAlarmButton.setOnClickListener(v -> {
            TimePickerFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker"); // shows the clock ( time picker ) when the button is clicked
        });

        Button cancelAlarmButton = findViewById(R.id.cancelAlarmButton);
        cancelAlarmButton.setOnClickListener(v -> {
                cancelAlarm();
        });
    }

    @Override
    // when a time is chosen by the user, this functions is what gets the time that has been set by the user
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateAlarmTimeText(c);
        startAlarm(c);

    }

    // Updates the TextView when the user chooses an alarm.
    private void updateAlarmTimeText(Calendar c) {
        String alarmText = "Alarm Set For: ";
        alarmText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()); // Formats the chosen time and displays it on screen
        alarmSetForTextView.setText(alarmText);
    }

    private void startAlarm(Calendar c) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        //Choosing a time in the past, makes the alarm set for the next day
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        // lets the alarm go off at the exact time specified by the user
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }


    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        // cancels the alarm that has been defined
        alarmManager.cancel(pendingIntent);
        alarmSetForTextView.setText("Alarm canceled");
    }

}