package ssd.tesina.trackingApp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ssd.tesina.trackingApp.R;
import ssd.tesina.trackingApp.fragments.viewmodels.FollowUserInfoModel;
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

                    Calendar calendar = Calendar.getInstance();

                    final FollowUserInfoModel viewModel = new FollowUserInfoModel();
                    viewModel.setName(document.getString("name"));
                    viewModel.setActivityType(document.getString("activityType"));
                    calendar.setTime(document.getTimestamp("startingTimeDate").toDate());
                    viewModel.setStartingTimeTime( calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
                    viewModel.setStartingTimeDate(calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
                    calendar.setTime(document.getTimestamp("finishingTimeDate").toDate());
                    viewModel.setFinishTimeDate(calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
                    viewModel.setFinishTimeTime(calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
                    viewModel.setPeopleNumber(document.getLong("peopleNumber").toString());

                    DocumentReference usersDoc = db.collection("users").document(connectionCode);
                    usersDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            //TODO: gestire errore non esiste documento

                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {
                                viewModel.setPicPath(document.getString("PathImg"));
                                viewModel.setName(document.getString("Username"));

                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.FollowInfoDialog_CardView, FollowInfoUserInfoFragment.newInstance(viewModel), "followUserInfoFragment").commit();
                            }
                        }
                    });
                }
            }
        });

        return v;
    }
}
