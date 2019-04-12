package com.example.trackingapp.Util;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.example.trackingapp.fragments.NoConnectionDialog;

import androidx.fragment.app.FragmentManager;

public class UsefullMethods {
    public UsefullMethods(){

    }

    public static void timerDelayRemoveDialog(long time, final Dialog d, final FragmentManager fragmentManager){

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(d.isShowing()){
                    NoConnectionDialog noConnectionDialog =  NoConnectionDialog.newInstance(2);
                    noConnectionDialog.show(fragmentManager,"SlowConn");
                    d.dismiss();
                }

            }
        }, time);
    }
    public static boolean checkConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }
}
