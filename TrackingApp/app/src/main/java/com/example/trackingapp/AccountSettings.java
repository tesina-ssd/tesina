package com.example.trackingapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.app.Activity.RESULT_OK;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountSettings.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountSettings#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountSettings extends Fragment {

    private de.hdodenhof.circleimageview.CircleImageView cm;
    private int REQUEST_CODE = 1;
    private OnFragmentInteractionListener mListener;
    private FirebaseFirestore db;
    private String userid = "";
    private FirebaseAuth auth= null;
    private EditText txtPhone= null;
    private EditText txtName= null;
    private EditText txtConnectedPhone= null;
    private EditText txtAlarmPhome= null;
    private Button btnSalva= null;
    private Button btnModifica= null;
    private Button btnLogout= null;
    private Uri imageuri= null;
    private FirebaseUser user= null;
    private DocumentReference docRef= null;
    Userinformation userinfo= null;
    private StorageReference mStorageRef= null;
    private WriteData writeData;
    private String imageURL = "";
    private StorageTask uploadTask= null;
    private boolean nameChanged = false;
    private boolean phoneChanged = false;
    private boolean connectedChanged = false;
    private boolean alarmChanged = false;
    private boolean canWrite = false;
    private boolean userModifiedWrite= false;
    private String KEY_IMAGE_PATH = "PathImg";
    private String KEY_USER_PHONE = "NumeroDiTelefono";
    private String KEY_PHONE_CONNECTED_TO_USER = "NumeroPersonaConnessa";
    private String KEY_ALARM_PHONE = "NumeroDiAllarme";
    public AccountSettings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountSettings.
     */
 /*
   public static AccountSettings newInstance() {
       AccountSettings fragment = new AccountSettings();
       Bundle args = new Bundle();
       args.stuff........
       fragment.setArguments(args);
       return fragment;
   }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  /* if (getArguments() != null) {
       stuff
   }*/
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userid = auth.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        writeData = new WriteData(this.getContext());
        inizializeClassWriteData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.accountsettings, container, false);
        txtPhone = (EditText) v.findViewById(R.id.txtUserPhone);
        txtName = (EditText) v.findViewById(R.id.txtUserName);
        txtConnectedPhone = (EditText) v.findViewById(R.id.txtUserConnectedPhone);
        txtAlarmPhome = (EditText) v.findViewById(R.id.txtalarmPhone);
        loadData();
        btnSalva = (Button) v.findViewById(R.id.btnsalvainfo) ;
        btnModifica = (Button) v.findViewById(R.id.btnModicainfo) ;
        btnLogout = (Button) v.findViewById(R.id.btnLogout) ;
        btnSalva.setEnabled(false);
        txtPhone.setEnabled(false);
        txtName.setEnabled(false);
        txtConnectedPhone.setEnabled(false);
        txtAlarmPhome.setEnabled(false);


        cm = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.profile_image);

        cm.setEnabled(false);
        cm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleziona un immagine"), REQUEST_CODE);
            }
        });
        btnModifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cm.setEnabled(true);
                btnSalva.setEnabled(true);
                btnModifica.setEnabled(false);
                txtPhone.setEnabled(true);
                txtName.setEnabled(true);
                txtConnectedPhone.setEnabled(true);
                txtAlarmPhome.setEnabled(true);
                userinfo = new Userinformation();
                userinfo.setNameSurname(txtName.getText().toString());
                userinfo.setPhone_num(txtPhone.getText().toString());
                userinfo.setConnected_num(txtConnectedPhone.getText().toString());
                userinfo.setAlarm_num(txtAlarmPhome.getText().toString());
            }
        });
        btnSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cm.setEnabled(false);
                String name=txtName.getText().toString();
                String phone = txtPhone.getText().toString();
                String cphone = txtConnectedPhone.getText().toString();
                String alarmphone = txtAlarmPhome.getText().toString();
                Map<String, Object> data = new HashMap<>();
                btnSalva.setEnabled(false);
                btnModifica.setEnabled(true);
                txtPhone.setEnabled(false);
                txtName.setEnabled(false);
                txtConnectedPhone.setEnabled(false);
                txtAlarmPhome.setEnabled(false);

                if(nameChanged){
                    if(!name.equals(userinfo.getNameSurname())){
                        userModifiedWrite=true;
                    }
                }
                if(phoneChanged){
                    if(!phone.equals(userinfo.getPhone_num())){
                        canWrite=true;
                        data.put(KEY_USER_PHONE,phone);
                    }
                }
                if(connectedChanged){
                    if(!cphone.equals(userinfo.getConnected_num())){
                        canWrite=true;
                        data.put(KEY_PHONE_CONNECTED_TO_USER,cphone);
                    }
                }
                if(alarmChanged){
                    if(!alarmphone.equals(userinfo.getAlarm_num())){
                        canWrite=true;
                        data.put(KEY_ALARM_PHONE,alarmphone);
                    }
                }
                if(canWrite || userModifiedWrite){
                        writeData.updateProfile(data,name,userModifiedWrite,canWrite);
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                if(getActivity() != null){
                    getActivity().finish();
                    startActivity(new Intent(getContext(),SignInActivity.class));
                }else {
                    startActivity(new Intent(getContext(),SignInActivity.class));
                }


            }
        });

        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nameChanged = true;
            }
        });
        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                phoneChanged = true;
            }
        });
        txtConnectedPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                connectedChanged = true;
            }
        });
        txtAlarmPhome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                alarmChanged = true;
            }
        });
        return v;
    }

    private void loadData() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setCancelable(false);
        pd.show();
        pd.setMessage("Getting information...");
        String username;
        if (user != null) {
            // Name, email address, and profile photo Url
            username = user.getDisplayName();
            txtName.setText(username);
            docRef = db.collection("users").document(userid);
            docRef.get().addOnCompleteListener(new OnCompleteListener < DocumentSnapshot > () {
                @Override
                public void onComplete(@NonNull Task < DocumentSnapshot > task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            txtPhone.setText(document.get(KEY_USER_PHONE).toString());
                            txtConnectedPhone.setText(document.get(KEY_PHONE_CONNECTED_TO_USER).toString());
                            txtAlarmPhome.setText(document.get(KEY_ALARM_PHONE).toString());
                            imageURL = document.get(KEY_IMAGE_PATH).toString();
                            if (!imageURL.equals("")) {
                                setImageFromdb();
                            }

                        } else {
                            toastMessage("No such document");
                        }
                    } else {
                        toastMessage("get failed with ");
                    }
                    pd.dismiss();
                }
            });

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageuri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()) {
                toastMessage("upload in progress");
            } else {
                    writeData.uploadImage(imageuri);

                setImageFromStorage();
            }
        }
    }

    private void inizializeClassWriteData (){
        writeData.setStorageReference(mStorageRef).setUser(user)
                .setUserid(userid).setDb(db);
    }

    private void setImageFromdb() {

        if (!imageURL.equals("")) {
            Picasso.get()
                    .load(imageURL)
                    .into(cm);
        }
    }

    private void setImageFromStorage() {
        Bitmap btmap = null;
        try {
            btmap = MediaStore.Images.Media.getBitmap(((MainActivity) getActivity()).getContex().getContentResolver(), imageuri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cm.setImageBitmap(btmap);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}