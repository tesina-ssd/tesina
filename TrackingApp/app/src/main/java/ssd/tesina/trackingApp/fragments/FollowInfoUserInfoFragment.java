package ssd.tesina.trackingApp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ssd.tesina.trackingApp.fragments.viewmodels.FollowUserInfoModel;
import ssd.tesina.trackingApp.R;
import ssd.tesina.trackingApp.databinding.FollowUserDialogInfoBinding;
import com.squareup.picasso.Picasso;

import androidx.databinding.DataBindingUtil;

public class FollowInfoUserInfoFragment extends androidx.fragment.app.Fragment {

    public FollowInfoUserInfoFragment() {}

    public static FollowInfoUserInfoFragment newInstance(FollowUserInfoModel viewModel) {
        FollowInfoUserInfoFragment fragment = new FollowInfoUserInfoFragment();

        Bundle args = new Bundle();
        args.putSerializable("viewModel", viewModel);
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
        FollowUserDialogInfoBinding binding = DataBindingUtil.inflate(inflater ,R.layout.follow_user_dialog_info, container, false);
        binding.setViewModel((FollowUserInfoModel) getArguments().getSerializable("viewModel"));
        View v = binding.getRoot();

        //TODO: limitare tempi di attesa
        Picasso.get()
                .load(((FollowUserInfoModel) getArguments().getSerializable("viewModel")).getPicPath())
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
