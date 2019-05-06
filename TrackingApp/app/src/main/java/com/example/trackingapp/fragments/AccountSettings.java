package com.example.trackingapp.fragments;

import android.app.ProgressDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.trackingapp.activities.MainActivity;
import com.example.trackingapp.R;
import com.example.trackingapp.activities.SignInActivity;
import com.example.trackingapp.util.UsefullMethods;
import com.example.trackingapp.util.Userinformation;
import com.example.trackingapp.util.WriteData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.trackingapp.util.Constants.ALARM_PHONE_NUMBER;
import static com.example.trackingapp.util.Constants.AUTH;
import static com.example.trackingapp.util.Constants.CONNECTED_PHONE_NUMBER;
import static com.example.trackingapp.util.Constants.KEY_ALARM_PHONE;
import static com.example.trackingapp.util.Constants.KEY_IMAGE_PATH;
import static com.example.trackingapp.util.Constants.KEY_PHONE_CONNECTED_TO_USER;
import static com.example.trackingapp.util.Constants.KEY_USER_PHONE;


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
    private EditText txtPhone= null;
    private EditText txtName= null;
    private EditText txtConnectedPhone= null;
    private EditText txtAlarmPhome= null;
    private Button btnSalva= null;
    private Button btnLogout= null;
    private Uri imageuri= null;
    private FirebaseUser user= null;
    private DocumentReference docRef= null;
    Userinformation userinfo= null;
    private StorageReference mStorageRef= null;
    private WriteData writeData;
    private String imageURL = "";
    private StorageTask uploadTask= null;
    private boolean phoneChanged = false;
    private boolean connectedChanged = false;
    private boolean alarmChanged = false;
    private Map<String, Object> data =null;
    private boolean userModifiedWrite= false;
    private int SLOWCONN = 2;
    private int NO_CONN = 1;
    private boolean isImageExist= false;
    private boolean newUser = false;
    private boolean connected;


    public AccountSettings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountSettings.
     */

    public static AccountSettings newInstance(boolean newUser) {
        AccountSettings fragment = new AccountSettings();
        Bundle args = new Bundle();
        args.putBoolean("newUser",newUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.newUser = getArguments().getBoolean("newUser");
        }

        user = AUTH.getCurrentUser();
        userid = AUTH.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        writeData = new WriteData(this.getContext(),getFragmentManager());
        inizializeClassWriteData();
        connected = UsefullMethods.checkConnection(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.account_settings, container, false);

        txtPhone = (EditText) v.findViewById(R.id.txtUserPhone);
        txtName = (EditText) v.findViewById(R.id.txtUserName);
        txtConnectedPhone = (EditText) v.findViewById(R.id.txtUserConnectedPhone);
        txtAlarmPhome = (EditText) v.findViewById(R.id.txtalarmPhone);
        btnSalva = (Button) v.findViewById(R.id.btnsalvainfo) ;
        btnLogout = (Button) v.findViewById(R.id.btnLogout) ;
        userinfo = new Userinformation();
        if(!newUser){
            loadData();
        }else{
            btnSalva.setEnabled(false);
            loadUserName();
            toastMessage("Per favore inserire informazioni\nfondamentali");
        }

        data = new HashMap<>();


        cm = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.profile_image);


        cm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleziona un immagine"), REQUEST_CODE);
            }
        });

        btnSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSalva.setEnabled(false);
                if(checkData() && isImageExist){
                    String name=txtName.getText().toString();
                    writeData.updateProfile(data,name,userModifiedWrite);
                    if(imageuri !=null ){writeData.uploadImage(imageuri);}
                    CONNECTED_PHONE_NUMBER= txtConnectedPhone.getText().toString();
                    ALARM_PHONE_NUMBER=txtAlarmPhome.getText().toString();
                    userinfo.setNameSurname(txtName.getText().toString());
                    userinfo.setPhone_num(txtPhone.getText().toString());
                    userinfo.setConnected_num(txtConnectedPhone.getText().toString());
                    userinfo.setAlarm_num(txtAlarmPhome.getText().toString());
                }else {
                    toastMessage("Completa i campi\ne\nSeleziona una tua foto");
                }



            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AUTH.signOut();
                if(getActivity() != null){
                    getActivity().finish();
                    startActivity(new Intent(getContext(), SignInActivity.class));
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
                String name =txtName.getText().toString();
                if(!name.equals(userinfo.getNameSurname())){
                    userModifiedWrite=true;
                    data.put("Username",name);
                }else{
                    userModifiedWrite=false;
                }
                if(userModifiedWrite || phoneChanged || alarmChanged || connectedChanged){
                    btnSalva.setEnabled(true);
                }else{
                    btnSalva.setEnabled(false);
                }
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
                String phone = txtPhone.getText().toString();
                if(!phone.equals(userinfo.getPhone_num())){
                    phoneChanged=true;
                    data.put(KEY_USER_PHONE,phone);
                }else{
                    phoneChanged=false;
                }
                if(userModifiedWrite || phoneChanged || alarmChanged || connectedChanged){
                    btnSalva.setEnabled(true);
                }else{
                    btnSalva.setEnabled(false);
                }

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
                String cphone = txtConnectedPhone.getText().toString();
                if(!cphone.equals(userinfo.getConnected_num())){
                    connectedChanged=true;
                    data.put(KEY_PHONE_CONNECTED_TO_USER,cphone);
                    btnSalva.setEnabled(true);
                }else{
                    connectedChanged=false;
                    btnSalva.setEnabled(false);
                }
                if(userModifiedWrite || phoneChanged || alarmChanged || connectedChanged){
                    btnSalva.setEnabled(true);
                }else{
                    btnSalva.setEnabled(false);
                }

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
                String alarmphone = txtAlarmPhome.getText().toString();
                if(!alarmphone.equals(userinfo.getAlarm_num())){
                    alarmChanged=true;
                    data.put(KEY_ALARM_PHONE,alarmphone);
                    btnSalva.setEnabled(true);
                }else{
                    alarmChanged=false;
                    btnSalva.setEnabled(false);
                }
                if(userModifiedWrite || phoneChanged || alarmChanged || connectedChanged){
                    btnSalva.setEnabled(true);
                }else{
                    btnSalva.setEnabled(false);
                }

            }
        });
        return v;
    }

    private void loadUserName() {
        if(connected) {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setCancelable(false);
            pd.show();
            UsefullMethods.timerDelayRemoveDialog(15000, pd,getFragmentManager());
            pd.setMessage("Getting information...");
            String username;
            if (user != null) {
                // Name, email address, and profile photo Url
                username = user.getDisplayName();
                txtName.setText(username);
                pd.dismiss();
            }
        }else {
            shownoConnection(NO_CONN);
        }
    }

    private boolean checkData() {
        boolean ret = true;
        if(txtName.getText().toString().equals("")) {
            txtName.setError("Compilare il campo");
            ret = false;
        }

        if(txtPhone.getText().toString().equals("")) {
            txtPhone.setError("Compilare il campo");
            ret = false;
        }

        if(txtConnectedPhone.getText().toString().equals("")) {
            txtConnectedPhone.setError("Compilare il campo");
            ret = false;
        }

        if(txtAlarmPhome.getText().toString().equals("")) {
            txtAlarmPhome.setError("Compilare il campo");
            ret = false;
        }

        return ret;
    }



    private void shownoConnection(int conn) {

        if(conn==2){
            NoConnectionDialog noConnectionDialog =  NoConnectionDialog.newInstance(SLOWCONN);
            noConnectionDialog.show(getFragmentManager(),"SlowConn");
        }
        if(conn==1){
            NoConnectionDialog noConnectionDialog =  NoConnectionDialog.newInstance(NO_CONN);
            noConnectionDialog.show(getFragmentManager(),"NoConn");
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
                setImageFromStorage();
                isImageExist = true;
                btnSalva.setEnabled(true);
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
    private void loadData() {
        if(connected){
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setCancelable(false);
            pd.show();
            UsefullMethods.timerDelayRemoveDialog(15000,pd,getFragmentManager());
            pd.setMessage("Getting information...");
            String username;
            if (user != null) {
                // Name, email address, and profile photo Url
                username = user.getDisplayName();
                txtName.setText(username);
                docRef = db.collection("users").document(userid);
                docRef.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
                    @Override
                    public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                txtPhone.setText(document.get(KEY_USER_PHONE).toString());
                                txtConnectedPhone.setText(document.get(KEY_PHONE_CONNECTED_TO_USER).toString());
                                txtAlarmPhome.setText(document.get(KEY_ALARM_PHONE).toString());
                                imageURL = document.get(KEY_IMAGE_PATH).toString();
                                setImageFromdb();
                                isImageExist = true;
                                userinfo.setNameSurname(txtName.getText().toString());
                                userinfo.setPhone_num(txtPhone.getText().toString());
                                userinfo.setConnected_num(txtConnectedPhone.getText().toString());
                                userinfo.setAlarm_num(txtAlarmPhome.getText().toString());
                                phoneChanged = false;
                                userModifiedWrite= false ;
                                alarmChanged= false;
                                connectedChanged= false;
                            } else {
                                toastMessage("Documento non esiste");
                                btnSalva.setEnabled(false);


                            }
                            btnSalva.setEnabled(false);
                            pd.dismiss();
                        } else {
                            toastMessage("get failed with ");
                            btnSalva.setEnabled(false);
                            pd.dismiss();
                        }

                    }
                });

            }
        }else {
            shownoConnection(NO_CONN);
        }

    }
/*********************************/


}