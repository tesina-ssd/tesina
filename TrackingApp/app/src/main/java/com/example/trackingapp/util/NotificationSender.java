package com.example.trackingapp.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.trackingapp.R;

import static com.example.trackingapp.util.Constants.CHANNEL_ID_STOP;

public class NotificationSender {
    private Context context ;
    private NotificationManagerCompat notificationManager;
    public NotificationSender(Context context){
        this.context = context;
        notificationManager = NotificationManagerCompat.from(this.context);
    }
    public void sendStopServiceNotification(PendingIntent pendingIntent){
        Notification stopNotification = new NotificationCompat.Builder(context, CHANNEL_ID_STOP)
                .setContentTitle(context.getResources().getString(R.string.titoloEscursione))
                .setContentText(context.getResources().getString(R.string.stopEscursione))
                .setSmallIcon(R.drawable.logo_facebook)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.RED)
                .setStyle(new NotificationCompat.BigTextStyle())
                .build();
        notificationManager.notify(2,stopNotification);
    }
}
