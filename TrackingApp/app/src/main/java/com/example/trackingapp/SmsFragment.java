package com.example.trackingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class SmsFragment extends Fragment {

    private EditText txtkeyword;
    private Button saveButton;
    private Switch enableSettings;
    private Switch swgoogleMapsSMS;
    private Switch swlocationSms;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT_KEYWORD = "keyword";
    public static final String SWITCH_ENABLESMS = "enableSms";
    public static final String SWITCH_GOOGLEMAPS_SMS = "smsgooglemaps";
    public static final String SWITCH_LOCATION_SMS = "smsLocation";

    private String keyoword;
    private boolean switchEnablesms;
    private boolean boolGoogleMapsSms;
    private boolean boolLocationSms;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sms_settings,container,false);
        txtkeyword = (EditText) v.findViewById(R.id.txtkeyword);
        saveButton = (Button) v.findViewById(R.id.btnsalvaSharedprefe);
        enableSettings = (Switch) v.findViewById(R.id.enableSms);
        swgoogleMapsSMS = (Switch) v.findViewById(R.id.swgoogleMapsSms);
        swlocationSms= (Switch) v.findViewById(R.id.swlocationSms);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        loadData();
        updateViews();

        return v;
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();



        editor.putString(TEXT_KEYWORD, txtkeyword.getText().toString());
        editor.putBoolean(SWITCH_ENABLESMS, enableSettings.isChecked());
        editor.putBoolean(SWITCH_GOOGLEMAPS_SMS, swgoogleMapsSMS.isChecked());
        editor.putBoolean(SWITCH_LOCATION_SMS, swlocationSms.isChecked());
        editor.apply();

        Toast.makeText(getContext(), "Data saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        keyoword = sharedPreferences.getString(TEXT_KEYWORD, "");
        switchEnablesms = sharedPreferences.getBoolean(SWITCH_ENABLESMS, false);
        boolGoogleMapsSms = sharedPreferences.getBoolean(SWITCH_GOOGLEMAPS_SMS, false);
        boolLocationSms = sharedPreferences.getBoolean(SWITCH_LOCATION_SMS, false);
    }

    public void updateViews() {
        txtkeyword.setText(keyoword);
        enableSettings.setChecked(switchEnablesms);
        swlocationSms.setChecked(boolGoogleMapsSms);
        swgoogleMapsSMS.setChecked(boolLocationSms);
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
