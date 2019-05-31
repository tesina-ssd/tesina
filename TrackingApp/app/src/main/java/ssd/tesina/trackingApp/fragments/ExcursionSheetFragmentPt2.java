package ssd.tesina.trackingApp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import ssd.tesina.trackingApp.R;
import ssd.tesina.trackingApp.util.ExcursionSheetMapBuilder;
import com.google.android.material.textfield.TextInputEditText;

import static android.app.Activity.RESULT_OK;

public class ExcursionSheetFragmentPt2 extends androidx.fragment.app.Fragment {

    private ExcursionSheetMapBuilder excursionSheet;

    private TextInputEditText txtStartingLocation;
    private TextInputEditText txtFinishLocation;
    private TextInputEditText txtTrackPath;
    private ImageView imgTrackFolder;

    private OnExcursionSheetFragmentPt2InteractionListener mListener;

    public interface OnExcursionSheetFragmentPt2InteractionListener {
        void onExcursionSheetFragmentPt2CancelPressed();
        void onExcursionSheetFragmentPt2NextPressed(ExcursionSheetMapBuilder excursionSheet);
    }

    public ExcursionSheetFragmentPt2() {}

    public static ExcursionSheetFragmentPt2 newInstance(ExcursionSheetMapBuilder excursionSheet) {

        ExcursionSheetFragmentPt2 fragment = new ExcursionSheetFragmentPt2();

        Bundle args = new Bundle();
        args.putSerializable("excursionSheet", excursionSheet);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            excursionSheet = (ExcursionSheetMapBuilder) getArguments().getSerializable("excursionSheet");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.excursion_sheet_pt2, container, false);

        // Acquisizione degli elementi del layout
        Button btnNext = v.findViewById(R.id.ExcursionSheet_BtnNext_Pt2);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkFields())
                    onNextPressed();
            }
        });

        Button btnCancel = v.findViewById(R.id.ExcursionSheet_BtnCancel_Pt2);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelPressed();
            }
        });

        txtStartingLocation = v.findViewById(R.id.ExcursionSheet_StartingLocation_Text);
        txtStartingLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!((TextInputEditText)view).getText().toString().equals(""))  ((TextInputEditText)view).setError(null);
            }
        });

        txtFinishLocation = v.findViewById(R.id.ExcursionSheet_FinishLocation_Text);
        txtFinishLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!((TextInputEditText)view).getText().toString().equals(""))  ((TextInputEditText)view).setError(null);
            }
        });

        txtTrackPath = v.findViewById(R.id.ExcursionSheet_Track_Text);

        imgTrackFolder = v.findViewById(R.id.ExcursionSheet_Track_Folder);
        imgTrackFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "trackChooser"), 1);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            txtTrackPath.setText(data.getData().toString());
        }
    }


    private void onCancelPressed() {
        if (mListener != null) {
            mListener.onExcursionSheetFragmentPt2CancelPressed();
        }
    }

    private void onNextPressed() {
        if(mListener != null) {
            mListener.onExcursionSheetFragmentPt2NextPressed(
                    excursionSheet.setStartingLocation(txtStartingLocation.getText().toString())
                                  .setFinishLocation(txtFinishLocation.getText().toString())
                                  .setTrackPath(txtTrackPath.getText().toString())
            );
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null) {
            mListener = (OnExcursionSheetFragmentPt2InteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnExcursionSheetFragmentPt3InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private boolean checkFields() {

        boolean ret = true;

        if(txtStartingLocation.getText().toString().equals("")) {
            txtStartingLocation.setError("Compilare il campo");
            ret = false;
        }

        if(txtFinishLocation.getText().toString().equals("")) {
            txtFinishLocation.setError("Compilare il campo");
            ret = false;
        }

        return ret;

    }
}
