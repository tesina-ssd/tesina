package com.example.trackingapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Map;

public class FollowInfoUserInfoFragment extends androidx.fragment.app.Fragment {

    public FollowInfoUserInfoFragment() {}

    public static FollowInfoUserInfoFragment newInstance(Map<String, Object> data) {
        FollowInfoUserInfoFragment fragment = new FollowInfoUserInfoFragment();

        Bundle args = new Bundle();
        args.putString("activityType", data.get("activityType").toString());
        args.putString("peopleNumber", data.get("peopleNumber").toString());
        args.putString("startingTimeDate", data.get("startingTimeDate").toString());
        args.putString("startingTimeTime", data.get("startingTimeTime").toString());
        args.putString("finishingTimeDate", data.get("finishingTimeDate").toString());
        args.putString("finishingTimeTime", data.get("finishingTimeTime").toString());
        args.putString("picPath", data.get("picPath").toString());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.follow_user_dialog_info, container, false);
        TextView activityType = v.findViewById(R.id.FollowUserDialog_TableInfo_ActivityType);
        activityType.setText(getArguments().getString("activityType"));

        TextView startingTimeTime = v.findViewById(R.id.FollowUserDialog_TableInfo_StartingTime);
        startingTimeTime.setText(getArguments().getString("startingTimeTime"));

        TextView startingTimeDate = v.findViewById(R.id.FollowUserDialog_TableInfo_StartingDate);
        startingTimeDate.setText(getArguments().getString("startingTimeDate"));

        TextView finishingTimeTime = v.findViewById(R.id.FollowUserDialog_TableInfo_FinishingTime);
        finishingTimeTime.setText(getArguments().getString("finishingTimeTime"));

        TextView finishingTimeDate = v.findViewById(R.id.FollowUserDialog_TableInfo_FinishingDate);
        finishingTimeDate.setText(getArguments().getString("finishingTimeDate"));

        //TODO: limitare tempi di attesa
        Picasso.get()
                .load(getArguments().getString("picPath"))
                .into((ImageView) v.findViewById(R.id.FollowUserDialog_ProfileImage));

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
