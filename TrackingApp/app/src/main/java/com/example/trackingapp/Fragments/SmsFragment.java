package com.example.trackingapp.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.trackingapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;
import static com.example.trackingapp.Util.Constants.SHARED_PREFS;
import static com.example.trackingapp.Util.Constants.SWITCH_ENABLESMS;
import static com.example.trackingapp.Util.Constants.SWITCH_GOOGLEMAPS_SMS;
import static com.example.trackingapp.Util.Constants.SWITCH_LOCATION_SMS;
import static com.example.trackingapp.Util.Constants.SWITCH_SINGLE_SMS;
import static com.example.trackingapp.Util.Constants.TEXT_KEYWORD;

public class SmsFragment extends Fragment {

    private EditText txtkeyword;
    private Button saveButton;
    private Switch enableSettings;
    private Switch swgoogleMapsSMS;
    private Switch swlocationSms;
    private Switch swSingleSms;


    private String keyoword;
    private boolean switchEnablesms;
    private boolean boolGoogleMapsSms;
    private boolean boolLocationSms;
    private boolean boolSingleSms;
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
        swSingleSms= (Switch) v.findViewById(R.id.swSingleSms);
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
        editor.putBoolean(SWITCH_SINGLE_SMS, swSingleSms.isChecked());
        editor.apply();

        Toast.makeText(getContext(), "Data saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        keyoword = sharedPreferences.getString(TEXT_KEYWORD, "");
        switchEnablesms = sharedPreferences.getBoolean(SWITCH_ENABLESMS, false);
        boolGoogleMapsSms = sharedPreferences.getBoolean(SWITCH_GOOGLEMAPS_SMS, false);
        boolLocationSms = sharedPreferences.getBoolean(SWITCH_LOCATION_SMS, false);
        boolSingleSms = sharedPreferences.getBoolean(SWITCH_SINGLE_SMS, false);
    }

    public void updateViews() {
        txtkeyword.setText(keyoword);
        enableSettings.setChecked(switchEnablesms);
        swlocationSms.setChecked(boolGoogleMapsSms);
        swgoogleMapsSMS.setChecked(boolLocationSms);
        swSingleSms.setChecked(boolSingleSms);
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
