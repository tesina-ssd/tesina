package com.example.trackingapp.util;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.trackingapp.activities.MainActivity;
import com.example.trackingapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.trackingapp.util.Constants.ALARM_PHONE_NUMBER;
import static com.example.trackingapp.util.Constants.BOOL_ALARM_MSG;
import static com.example.trackingapp.util.Constants.BOOL_CONNECTED_MSG;
import static com.example.trackingapp.util.Constants.CHANNEL_ID_SERVICE;
import static com.example.trackingapp.util.Constants.CONNECTED_PHONE_NUMBER;
import static com.example.trackingapp.util.Constants.EMERGENCY_MSG;
import static com.example.trackingapp.util.Constants.INTERNET_ENABLE;
import static com.example.trackingapp.util.Constants.IS_TRACKING_SERVICE_WORKING;
import static com.example.trackingapp.util.Constants.LOCATION_MSG;
import static com.example.trackingapp.util.Constants.PHONE_NUMBER;
import static com.example.trackingapp.util.Constants.SHARED_PREFS;
import static com.example.trackingapp.util.Constants.SMS_ENABLE;
import static com.example.trackingapp.util.Constants.TEXT_KEYWORD;
import static com.example.trackingapp.util.Constants.WHO_CALLING;


/**
 * <p>
 * Questa classe forse è il componente principale dell'applicazione in quanto il funzionamento 
 * dell'escursione è compreso qua dentro.
 * E' un servizio che lavora in background è carica i dati sul database fino a una condizione per uscire
 * che sarebbe la chiusura del servizio da parte dell'utente.
 * I dati vengono caricati tramite un thread che lavora in backgroud.
 * Viene controllata anche l'ora. Se l'ora di rientro è passata all'utente viene inviata una notifica che avvisa 
 * l'utente di disattivare il servizio,altrimenti verranno inviati SMS alla persona connessa e al numero di allarme.
 * In questa classe avviene anche degli SMS tramite un broadcast receiver. Evento viene scatenato quando viene ricevuto un sms 
 * il messaggio viene controllato e se contiene la parola chiave vengono inviati i sms riguardanti l'infomazioni sulla 
 * posizione attuale dell'utente.
 * Tutto ciò funziona solamente se viene attivato il servizio dall'utente e in base alle impostazioni dei sms.
 * <p>
 * @author      Singh Harpreet
 * @version     %I%, %G%
 * @since       1.0
 */
public class UserinfoUpdateService extends Service {
    private FirebaseFirestore db = null;
    private static String userid = "";
    private final static String TAG_USERID = "USERID";
    private Calendar calendar = null;
    private static long time = 0;
    private static long timeConnectedMsg = 0;
    private static long timeAlarmMsg = 0;
    public  static volatile  boolean shouldContinue = true;
    private long systemTime = 0;
    private Date date;
    private WriteData wrData;
    private Handler mHandler = new Handler();
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private boolean msgConnectedSent=false;
    private boolean msgAlarmSent=false;
    private SmsManager smsmanager;
    private NotificationSender sendNotification;
    private PendingIntent pendingIntent;
    private boolean alreadyMsgSent=false;

    @Override
    public void onCreate() {
        super.onCreate();
        shouldContinue=true;
        Constants.IS_TRACKING_SERVICE_WORKING =false;
        Log.d(TAG, "onCreate");
        db = FirebaseFirestore.getInstance();
        wrData = new WriteData(getBaseContext(),null);
        calendar=new GregorianCalendar();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SMS_RECEIVED);
        filter.setPriority(2147483647);
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(br, filter);
        smsmanager = SmsManager.getDefault();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        userid = intent.getStringExtra(TAG_USERID);
        inizializeWriteclass();
        time=  intent.getLongExtra("Time",1L);
        timeConnectedMsg= time + 1800000;
        timeAlarmMsg = time + 3600000;
        Intent intentAction = new Intent(getApplicationContext(),NotificationReceiver.class);
        //This is optional if you have more than one buttons and want to differentiate between two
        intentAction.putExtra("action","Dismiss");
        sendNotification = new NotificationSender(getApplicationContext());
        PendingIntent pIntentlogin = PendingIntent.getBroadcast(getApplicationContext(),1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_SERVICE)
                .setContentTitle(getApplicationContext().getResources().getString(R.string.titoloEscursione))
                .setContentText(getResources().getString(R.string.testoEscursione))
                .setSmallIcon(R.drawable.logo_facebook)
                .setContentIntent(pendingIntent)
                .setColor(Color.BLUE)
                .addAction(R.drawable.logo_google, "Chiudi la escursione",pIntentlogin)
                .build();


        doWorkInBackground();
        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    private
        final BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SMS_RECEIVED)) {
                    String keyword ;
                    SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    keyword = sharedPreferences.getString(TEXT_KEYWORD, "").toLowerCase();

                    if ((keyword.length() == 0) || (!SMS_ENABLE)) {
                        //Log.d(TAG, "No keyword available. Exit");
                        return;
                    }
                    ArrayList<SmsMessage> list = null;
                    try {
                        list = getMessagesWithKeyword(keyword, intent.getExtras());
                    } catch (Exception e) {
                        return;
                    }
                    if (list.size() == 0) {
                        //Log.d(TAG, "No message available. Exit");
                        return;
                    }

                    //This is used to close the notification tray
                    Intent serviceIntent = new Intent(context, SmsSenderService.class);
                    serviceIntent.putExtra(PHONE_NUMBER, list.get(0).getOriginatingAddress());
                    serviceIntent.putExtra(WHO_CALLING,LOCATION_MSG);
                    SmsSenderService.enqueueWork(context, serviceIntent);

                }
            }
        };


    private ArrayList<SmsMessage> getMessagesWithKeyword(String keyword, Bundle bundle) {
        ArrayList<SmsMessage> list = new ArrayList<SmsMessage>();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdus.length; i++) { //TODO: controllare
                SmsMessage sms = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = bundle.getString("format");
                    sms = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    sms = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                if (sms.getMessageBody().toLowerCase().equals(keyword)) {
                    list.add(sms);
                }
            }
        }
        return list;
    }
    private void inizializeWriteclass() {
        wrData.setDb(db).setUserid(userid);
    }

    private void doWorkInBackground() {
        mToastRunnable.run();
    }

    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            IS_TRACKING_SERVICE_WORKING =true;
            Log.d("time-",""+time);
            if(time!=0){
                    date= new Date();
                    systemTime =date.getTime();
                    Log.d("timesystem-",""+systemTime);
                Log.d("timesyst-",""+timeConnectedMsg);
                    if(systemTime<time){
                        if(INTERNET_ENABLE){
                            wrData.setUserLocation(LocationUpdater.getHash());
                        }

                    }else {
                        if(systemTime>timeConnectedMsg){
                            if(!msgConnectedSent){
                                msgConnectedSent=true;
                                sendEmergencyMsgs(CONNECTED_PHONE_NUMBER);
                                //Log.d("hereconn",""+systemTime);
                                sendNotification.sendMessageConnectedNotification(pendingIntent);

                            }
                        }
                        if(systemTime>timeAlarmMsg){
                            if(!msgAlarmSent){
                                msgAlarmSent=true;
                                sendEmergencyMsgs(ALARM_PHONE_NUMBER);
                                //Log.d("herealarm",""+systemTime);
                                sendNotification.sendMessageAlarmNotification(pendingIntent);

                            }
                        }
                        if(INTERNET_ENABLE){
                            wrData.setUserLocation(LocationUpdater.getHash());
                        }
                        sendNotification.sendStopServiceNotification(pendingIntent);
                    }
            }
            mHandler.postDelayed(this, 11000);
        }
    };


    private void sendEmergencyMsgs(String phone){


        Intent serviceIntent = new Intent(this, SmsSenderService.class);
        serviceIntent.putExtra(PHONE_NUMBER,phone);
        serviceIntent.putExtra(WHO_CALLING,EMERGENCY_MSG);
        if(alreadyMsgSent){
            serviceIntent.putExtra(BOOL_CONNECTED_MSG,false);
            alreadyMsgSent=true;
        }else{
            serviceIntent.putExtra(BOOL_CONNECTED_MSG,true);
        }
        serviceIntent.putExtra(BOOL_ALARM_MSG,msgAlarmSent);

        SmsSenderService.enqueueWork(this, serviceIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
        mHandler.removeCallbacks(mToastRunnable);
        Log.d(TAG, "onDestroyKILLEDD");
        IS_TRACKING_SERVICE_WORKING =false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
