package com.example.soen_357_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;
import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private TextView alarmSetForTextView;
    private PendingIntent pendingIntent;
    public static Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    private Calendar cal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmSetForTextView = findViewById(R.id.alarmSetForTextView);

        // getting the saved alarm from the shared preference
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.AlarmTimeFile), Context.MODE_PRIVATE);
        String alarmTime = sharedPreferences.getString(getString(R.string.AlarmTime), null);
        if(alarmTime == null){
            alarmSetForTextView.setText("No Alarm Set");
        }else{
            alarmSetForTextView.setText(alarmTime);
        }

        Intent intent = new Intent(this, AlertReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.changeRingtoneButton){
            changeRingtone();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    // when a time is chosen by the user, this functions is what gets the time that has been set by the user
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        updateAlarmTimeText(cal);
        startAlarm(cal);

    }

    // Updates the TextView when the user chooses an alarm.
    private void updateAlarmTimeText(Calendar c) {
        String alarmText = "Alarm Set For: ";
        alarmText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()); // Formats the chosen time and displays it on screen
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

        // lets the alarm go off at the exact time specified by the user -> repeating every day
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }


    private void cancelAlarm() {

        if(cal == null){
            Toast.makeText(this, "No Alarm Set", Toast.LENGTH_SHORT).show();
            return;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Stops the alarm defined in the AlarmSoundService Class
        stopService(new Intent(MainActivity.this, AlarmSoundService.class));

        Toast.makeText(this, "Alarm Stopped", Toast.LENGTH_SHORT).show();

        // Re-adds the chosen time with 1 day added -> TO repeat the alarm infinitely.
        cal.add(Calendar.DATE, 1);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

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


}