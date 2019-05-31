package ssd.tesina.trackingApp.util;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import ssd.tesina.trackingApp.fragments.NoConnectionDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public class UsefulMethods {
    private FirebaseFirestore db=null;
    public UsefulMethods(){

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
        //we are connected to a network
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }
    public static void deleteKeyFromDB(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        //Cancello la chiave creata
        db.collection(Constants.COLLECTION_ESCURSIONE).document(Constants.CHIAVE_ESCURSIONE)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Deleteting", "Document successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Deleteting", "Error deleting document", e);
                    }
                });
        //setto anche la stringa a null
        Constants.CHIAVE_ESCURSIONE = "";
    }
}
