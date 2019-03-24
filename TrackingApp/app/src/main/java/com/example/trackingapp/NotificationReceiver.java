package com.example.trackingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getStringExtra("action");
        if(action!=null){
            Intent serviceIntent = new Intent(context, UserinfoUpdateService.class);
            context.stopService(serviceIntent);
            IsServiceWorking.isWorking=false;
            Toast.makeText(context,"STOPPED0",Toast.LENGTH_LONG).show();
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }

    }

}
