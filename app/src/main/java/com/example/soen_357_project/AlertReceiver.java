package com.example.soen_357_project;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static com.example.soen_357_project.NotificationHelper.CHANNEL_1_ID;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    // when the alarm goes off, this function is called to show the notification
    public void onReceive(Context context, Intent intent) {

        System.out.println("entered on receive");
        context.startService(new Intent(context, AlarmSoundService.class));
        Intent intent_main_activity = new Intent(context, GameActivity.class);
        // set up a pending intent
        PendingIntent pending_intent_main_activity = PendingIntent.getActivity(context, 0, intent_main_activity, FLAG_IMMUTABLE);
        Notification notif = new NotificationCompat.Builder(context, CHANNEL_1_ID) // check if context is correct
                .setSmallIcon(R.drawable.alarm_drawable)
                .setContentTitle("ALARM!!!")
                .setContentText("Time to wake up !!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pending_intent_main_activity)
                .build();

        NotificationManagerCompat notifManager = NotificationManagerCompat.from(context);
        notifManager.notify(1, notif);
        Intent i = new Intent(context, GameActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);


    }

}
