package com.example.trackingapp.util;


import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.trackingapp.R;
import static com.example.trackingapp.util.Constants.CHANNEL_ID_SERVICE;
import static com.example.trackingapp.util.Constants.CHANNEL_ID_STOP;

public class CreateNotficationChannel extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel1_name);
            String description = getString(R.string.channel1_description);
            int importance1 = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel1= new NotificationChannel(CHANNEL_ID_SERVICE, name, importance1);
            channel1.setDescription(description);

            CharSequence name1 = getString(R.string.channel2_name);
            String description2 = getString(R.string.channel2_description);
            int importance2 = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel2= new NotificationChannel(CHANNEL_ID_STOP, name1, importance2);
            channel1.setDescription(description2);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}
