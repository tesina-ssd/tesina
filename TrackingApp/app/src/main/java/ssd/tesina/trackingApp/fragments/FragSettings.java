package ssd.tesina.trackingApp.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ssd.tesina.trackingApp.R;

public class FragSettings extends Fragment {
    //private static final String TAG = "FragSettings";
    private TextView lblAccountset   ;
    private FragmentManager fm ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        fm= getFragmentManager();
        lblAccountset = (TextView) view.findViewById(R.id.lblAccountSettings);
        lblAccountset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountSettings accFrag = new AccountSettings();

                //fm.beginTransaction().replace(R.id.frameLayout,accFrag,"accountsettings").commit();
            }
        });

        view.findViewById(R.id.lblSmsSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsFragment smsFrag = new SmsFragment();
                //fm.beginTransaction().replace(R.id.frameLayout,smsFrag,"smsSetting").commit();
            }
        });

       return view;
    }
}
