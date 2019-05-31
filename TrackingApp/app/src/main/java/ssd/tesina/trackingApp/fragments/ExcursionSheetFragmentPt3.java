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

import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ExcursionSheetFragmentPt3 extends androidx.fragment.app.Fragment {

    private ExcursionSheetMapBuilder excursionSheet;

    private TextInputEditText txtOtherComponentsNames;
    private TextInputEditText txtOtherComponentsCellNumbers;
    private TextInputEditText txtPhotoPath;
    private ImageView imgPhotoCamera;

    private OnExcursionSheetFragmentPt3InteractionListener mListener;

    public interface OnExcursionSheetFragmentPt3InteractionListener {
        void onExcursionSheetFragmentPt3CancelPressed();
        void onExcursionSheetFragmentPt3NextPressed(Map excursionSheet);
    }

    public ExcursionSheetFragmentPt3() {}

    public static ExcursionSheetFragmentPt3 newInstance(ExcursionSheetMapBuilder excursionSheet) {

        ExcursionSheetFragmentPt3 fragment = new ExcursionSheetFragmentPt3();

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
        View v = inflater.inflate(R.layout.excursion_sheet_pt3, container, false);

        // Acquisizione degli elementi del layout
        Button btnNext = v.findViewById(R.id.ExcursionSheet_BtnNext_Pt3);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    onNextPressed();
            }
        });

        Button btnCancel = v.findViewById(R.id.ExcursionSheet_BtnCancel_Pt3);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelPressed();
            }
        });

        txtOtherComponentsNames = v.findViewById(R.id.ExcursionSheet_OtherComponentsNames_Text);
        txtOtherComponentsNames.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!((TextInputEditText)view).getText().toString().equals(""))  ((TextInputEditText)view).setError(null);
            }
        });

        txtOtherComponentsCellNumbers = v.findViewById(R.id.ExcursionSheet_OtherComponentsCellNumbers_Text);
        txtOtherComponentsCellNumbers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!((TextInputEditText)view).getText().toString().equals(""))  ((TextInputEditText)view).setError(null);
            }
        });

        txtPhotoPath = v.findViewById(R.id.ExcursionSheet_Photo_Text);

        imgPhotoCamera = v.findViewById(R.id.ExcursionSheet_Photo_Camera);
        imgPhotoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "photoChooser"), 1);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            txtPhotoPath.setText(data.getData().toString());
        }
    }


    private void onCancelPressed() {
        if (mListener != null) {
            mListener.onExcursionSheetFragmentPt3CancelPressed();
        }
    }

    private void onNextPressed() {
        if(mListener != null) {
            mListener.onExcursionSheetFragmentPt3NextPressed(
                    excursionSheet.setOtherComponentsNames(txtOtherComponentsNames.getText().toString())
                                  .setOtherComponentsNumbers(txtOtherComponentsCellNumbers.getText().toString())
                                  .setPhotoPath(txtPhotoPath.getText().toString())
                                  .build()
            );
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null) {
            mListener = (OnExcursionSheetFragmentPt3InteractionListener) getParentFragment();
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
}
