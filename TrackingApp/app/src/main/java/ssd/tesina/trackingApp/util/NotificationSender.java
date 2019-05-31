package ssd.tesina.trackingApp.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ssd.tesina.trackingApp.R;

public class NotificationSender {
    private Context context ;
    private NotificationManagerCompat notificationManager;
    public NotificationSender(Context context){
        this.context = context;
        notificationManager = NotificationManagerCompat.from(this.context);
    }
    public void sendStopServiceNotification(PendingIntent pendingIntent){
        Notification stopNotification = new NotificationCompat.Builder(context, Constants.CHANNEL_ID_STOP)
                .setContentTitle(context.getResources().getString(R.string.escursioneNonDisattivata))
                .setContentText(context.getResources().getString(R.string.stopEscursione))
                .setSmallIcon(R.drawable.logo_facebook)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.RED)
                .setStyle(new NotificationCompat.BigTextStyle())
                .build();
        notificationManager.notify(2,stopNotification);
    }
    public void sendMessageConnectedNotification(PendingIntent pendingIntent){
        Notification messageToConnectedNotification = new NotificationCompat.Builder(context, Constants.CHANNEL_ID_STOP)
                .setContentTitle(context.getResources().getString(R.string.smsSentTitle))
                .setContentText(context.getResources().getString(R.string.smsConnectedSent))
                .setSmallIcon(R.drawable.logo_facebook)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.RED)
                .setStyle(new NotificationCompat.BigTextStyle())
                .build();
        notificationManager.notify(3,messageToConnectedNotification);
    }
    public void sendMessageAlarmNotification(PendingIntent pendingIntent){
        Notification messageToAlarmstopNotification = new NotificationCompat.Builder(context, Constants.CHANNEL_ID_STOP)
                .setContentTitle(context.getResources().getString(R.string.smsSentTitle))
                .setContentText(context.getResources().getString(R.string.smsAlarmSent))
                .setSmallIcon(R.drawable.logo_facebook)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.RED)
                .setStyle(new NotificationCompat.BigTextStyle())
                .build();
        notificationManager.notify(4,messageToAlarmstopNotification);
    }
}
