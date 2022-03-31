package com.example.soen_357_project;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.soen_357_project.NotificationHelper.CHANNEL_1_ID;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    // when the alarm goes off, this function is called to show the notification
    public void onReceive(Context context, Intent intent) {

        Uri alarmRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        Notification notif = new NotificationCompat.Builder(context, CHANNEL_1_ID) // check if context is correct
                .setSmallIcon(R.drawable.alarm_drawable)
                .setContentText("ALARM!!!")
                .setContentText("Alarm is working !!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(alarmRingtone)
                .build();

        NotificationManagerCompat notifManager = NotificationManagerCompat.from(context);
        notifManager.notify(1, notif);
    }
}
