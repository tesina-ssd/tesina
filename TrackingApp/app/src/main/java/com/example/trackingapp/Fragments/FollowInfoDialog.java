package com.example.trackingapp.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trackingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FollowInfoDialog extends DialogFragment {

    private FragmentManager fragmentManager = null;
    private String connectionCode = null;
    private FirebaseFirestore db = null;

    static public FollowInfoDialog newInstance(String connectionCode) {
        FollowInfoDialog dialog = new FollowInfoDialog();

        Bundle args = new Bundle();
        args.putString("connectionCode", connectionCode);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Costruzione del layout
        fragmentManager = getChildFragmentManager();

        connectionCode = getArguments().getString("connectionCode");

        View v = inflater.inflate(R.layout.follow_info_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Fragment avantiFragment = FollowInfoLoadingFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.FollowInfoDialog_CardView,  avantiFragment, "avantiTest").commit();

        // Viene ritornata la View corrente

        final Map<String, Object> data = new HashMap<>();

        DocumentReference excursionDoc = db.collection("excursion").document(connectionCode);
        excursionDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //TODO: gestire errore non esiste documento
                DocumentSnapshot document = task.getResult();
                if(document.exists()) {
                    //TODO: sta roba è scritta 'dimmerda
                    Calendar calendar = Calendar.getInstance();
                    data.put("activityType", document.getString("activityType"));
                    data.put("peopleNumber", document.get("peopleNumber").toString());
                    calendar.setTime(document.getTimestamp("startingTimeDate").toDate());
                    data.put("startingTimeDate", calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
                    data.put("startingTimeTime", calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
                    calendar.setTime(document.getTimestamp("finishingTimeDate").toDate());
                    data.put("finishingTimeDate", calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
                    data.put("finishingTimeTime", calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));

                    DocumentReference usersDoc = db.collection("users").document(connectionCode);
                    usersDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            //TODO: gestire errore non esiste documento
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {
                                data.put("picPath", document.getString("PathImg"));

                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                /*Prima data*/
                                transaction.replace(R.id.FollowInfoDialog_CardView, FollowInfoUserInfoFragment.newInstance(null), "followUserInfoFragment").commit();
                            }
                        }
                    });
                }
            }
        });

        return v;
    }


/*    @Override
    public void onExcursionSheetFragmentCancelPressed() {
        mListener.onConnectionDialogCancelClicked();
    }

    @Override
    public void onExcursionSheetFragmentNextPressed(Map<String,Object> excursionSheet) {
        this.excursionSheet = excursionSheet;
        connection_Key = randomCode(10);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
        transaction.replace(R.id.cardViewConnectionDialog, ConnectionCodeFragment.newInstance(connection_Key), "connCode").commit();
    }*/

}
