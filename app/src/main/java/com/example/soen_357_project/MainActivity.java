package com.example.soen_357_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private TextView alarmSetForTextView;
    private TextView alarmOnTextView;
    private PendingIntent pendingIntent;
    private ImageView ringtoneButton;
    // private Switch alarmOnSwitch;
    private ConstraintLayout bgd;

    public static Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    Button cancelAlarmButton ;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bgd = findViewById(R.id.mainActivityLayout);
        cancelAlarmButton = findViewById(R.id.cancelAlarmButton);
        alarmSetForTextView = findViewById(R.id.alarmSetForTextView);

        // getting the saved alarm from the shared preference
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.AlarmTimeFile), Context.MODE_PRIVATE);
        String alarmTime = sharedPreferences.getString(getString(R.string.AlarmTime), null);
        if(alarmTime == null )
        {
            alarmSetForTextView.setText("No Alarm Set");
            cancelAlarmButton.setVisibility(View.GONE);
        }
        else if(alarmTime.equals("Alarm Stopped"))
        {
            Toast.makeText(getApplicationContext(),"Alarm Stopped!", Toast.LENGTH_LONG);
            alarmSetForTextView.setText("No Alarm Set");
            cancelAlarmButton.setVisibility(View.GONE);

        }
        else
            {
            alarmSetForTextView.setText(alarmTime);
            cancelAlarmButton.setVisibility(View.VISIBLE);

        }

        Intent intent = new Intent(this, AlertReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmOnTextView = findViewById(R.id.alarmOnTextView);
        alarmOnTextView.setText("");

        ImageView ringtoneButton = findViewById(R.id.ringtoneButton);

        ringtoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeRingtone();
            }
        });

        Button addAlarmButton = findViewById(R.id.AddAlarmButton);
        addAlarmButton.setOnClickListener(v -> {
            TimePickerFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker"); // shows the clock ( time picker ) when the button is clicked
        });

        cancelAlarmButton.setOnClickListener(v -> {
                cancelAlarm();

        });

        setBackground();
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
        cancelAlarmButton.setVisibility(View.VISIBLE);

    }

    // Updates the TextView when the user chooses an alarm.
    private void updateAlarmTimeText(Calendar c) {
        // String alarmText = "Alarm Set For: ";
        String alarmText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()); // Formats the chosen time and displays it on screen
        alarmOnTextView.setText("alarm set for:");
        alarmSetForTextView.setText(alarmText);

        // Putting the Saved alarm inside a shared preference
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.AlarmTimeFile), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.AlarmTime), alarmText);
        editor.apply();

    }

    private void startAlarm(Calendar c) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //Choosing a time in the past, makes the alarm set for the next day
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        // lets the alarm go off at the exact time specified by the user
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }


    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);

        // Stops the alarm defined in the AlarmSoundService Class
        stopService(new Intent(MainActivity.this, AlarmSoundService.class));

        // Putting the Saved alarm inside a shared preference
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.AlarmTimeFile), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.AlarmTime), "Alarm Stopped");
        editor.apply();

        alarmOnTextView.setText("");
        cancelAlarmButton.setVisibility(View.GONE);
        alarmSetForTextView.setText("no alarm set");

    }

    private void changeRingtone() {

        RingtonePickerDialog.Builder ringtonePickerBuilder = new RingtonePickerDialog
                .Builder(MainActivity.this, getSupportFragmentManager())

                //Set title of the dialog.
                //If set null, no title will be displayed.
                .setTitle("Select ringtone")

                //Set true to allow allow user to select default ringtone set in phone settings.
                .displayDefaultRingtone(true)

                //Set true to allow user to select silent (i.e. No ringtone.).
                .displaySilentRingtone(true)

                //set the text to display of the positive (ok) button.
                //If not set OK will be the default text.
                .setPositiveButtonText("SET RINGTONE")

                //set text to display as negative button.
                //If set null, negative button will not be displayed.
                .setCancelButtonText("CANCEL")

                //Set flag true if you want to play the sample of the clicked tone.
                .setPlaySampleWhileSelection(true)

                //Set the callback listener.
                .setListener(new RingtonePickerListener() {
                    @Override
                    public void OnRingtoneSelected(@NonNull String ringtoneName, Uri ringtoneUri) {
                        ringtone = ringtoneUri;
                    }
                });

        //Add the desirable ringtone types.
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_RINGTONE);
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM);

        //Display the dialog.
        ringtonePickerBuilder.show();

    }


    private void setBackground() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        System.out.println(timeOfDay);

        if(timeOfDay >= 6 && timeOfDay <= 18){
            // Drawable sunBackground = getDrawable(R.drawable.sun_sky_bgd);
            bgd.setBackgroundResource(R.drawable.sky_sun_bgd);
        }else {
            // Drawable moonBackground = getDrawable(R.drawable.moon_sky_bgd);
            bgd.setBackgroundResource(R.drawable.moon_sky_bgd);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}