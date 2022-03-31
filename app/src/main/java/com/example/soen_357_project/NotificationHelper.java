package com.example.soen_357_project;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends Application {

    public static final String CHANNEL_1_ID = "channel_1";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // checks if its android 8  ( oreo )  or more
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Alarm Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Notification received from alarm going off ");
            channel1.enableVibration(true);
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

}
