package com.example.soen_357_project;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import androidx.annotation.Nullable;


public class AlarmSoundService extends Service{

    private static MediaPlayer mediaPlayer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, MainActivity.ringtone); // assigns the selected ringtone to be played
        mediaPlayer.setLooping(true);//Runs the alarm sound infinitely
        mediaPlayer.start();  //Start media player
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //On destory stop and release the media player
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }

}
