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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private de.hdodenhof.circleimageview.CircleImageView cm;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int REQUEST_CODE=1;
    private OnFragmentInteractionListener mListener;
    private FirebaseFirestore db;
    private String userid;
    private FirebaseAuth auth;
    private EditText txtPhone;
    private EditText txtName;
    private EditText txtConnectedPhone;
    private EditText txtAlarmPhome;
    private Uri imageuri;
    private FirebaseUser user;
    private DocumentReference docRef;
    Userinformation userinfo ;
    private StorageReference mStorageRef;
    private String imageURL="";
    private StorageTask uploadTask;
    //private StorageReference storageReference;
    public AccountSettings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
   //  * @param param1 Parameter 1.
  //   * @param param2 Parameter 2.
     * @return A new instance of fragment AccountSettings.
     */
  /*  // TODO: Rename and change types and number of parameters
    public static AccountSettings newInstance(String param1, String param2) {
        AccountSettings fragment = new AccountSettings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.accountsettings, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        txtPhone = (EditText) v.findViewById(R.id.txtUserPhone) ;
        txtName = (EditText) v.findViewById(R.id.txtUserName) ;
        txtConnectedPhone = (EditText) v.findViewById(R.id.txtUserConnectedPhone) ;
        txtAlarmPhome = (EditText) v.findViewById(R.id.txtalarmPhone) ;



        loadData();
        //container.removeAllViews();
     /*   txtName.setText(userinfo.getNameSurname());
        txtPhone.setText(userinfo.getPhone_num());
        txtConnectedPhone.setText(userinfo.getConnected_num());
        txtAlarmPhome.setText(userinfo.getAlarm_num());
*/
        txtPhone.setEnabled(false);
        txtName.setEnabled(false);
        txtConnectedPhone.setEnabled(false);
        txtAlarmPhome.setEnabled(false);



        cm= (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.profile_image);
        cm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Seleziona un immagine"),REQUEST_CODE);
            }
        });



        // Inflate the layout for this fragment
        return v;
    }

    private void loadData() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Uri photoUrl ;
        String username;
        userinfo = new Userinformation();
        if (user != null) {
            // Name, email address, and profile photo Url
             username = user.getDisplayName();
             txtName.setText(username);
             // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            userid = auth.getUid();

          /*  String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();*/


            photoUrl = user.getPhotoUrl();
            if(photoUrl!= null){
             //   setImage(photoUrl);
            }


            db=FirebaseFirestore.getInstance();

            docRef = db.collection("users").document(userid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //userinfo.setPhone_num(document.get("NumeroDiTelefono").toString());
                            txtPhone.setText(document.get("NumeroDiTelefono").toString());
                            txtConnectedPhone.setText(document.get("NumeroPersonaConnessa").toString());
                            txtAlarmPhome.setText(document.get("NumeroDiAllarme").toString());
                            imageURL = document.get("PathImg").toString();
                           // userinfo.setConnected_num(document.get("NumeroPersonaConnessa").toString());
                            //userinfo.setAlarm_num(document.get("NumeroDiAllarme").toString());
                            if(!imageURL.equals("")){

                                setImageFromdb();
                            }


                        } else {
                            toastMessage("No such document");
                        }
                    } else {
                        toastMessage("get failed with ");
                    }
                }
            });
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){

           imageuri = data.getData();
           if(uploadTask != null && uploadTask.isInProgress()){
               toastMessage("upload in progress");
           }else{
               uploadImage();
               setImageFromStorage();
           }

        }

    }
    private void uploadImage(){
        Random rand = new Random();

        // Obtain a number between [0 - 49].
        int n = rand.nextInt(10000)+1;
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if(imageuri != null){
            final StorageReference fileReference = mStorageRef.child("images/users/" + userid + "/" + n + "."+getFileExtension(imageuri));
            fileReference.putFile(imageuri).continueWithTask(new Continuation <UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Uri doownloadUri = (Uri) task.getResult();
                        String muri = doownloadUri.toString();

                        Map<String ,Object > usermap = new HashMap<>();

                        usermap.put("PathImg",muri);

                        db.collection("users").document(userid).update(usermap);
                        pd.dismiss();
                    }else {
                        toastMessage("Failed uploading");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMessage("Failed");
                }
            });

        }else{
            toastMessage("No image selected");
        }


    }
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        return mtm.getExtensionFromMimeType(cr.getType(uri));
    }
    private void setImageFromdb(){

        if(!imageURL.equals("")){
            Picasso.get()
                    .load(imageURL)
                    .into(cm);
        }
    }

    private void setImageFromStorage(){
        Bitmap btmap = null;
        try {
            btmap = MediaStore.Images.Media.getBitmap(((MainActivity)getActivity()).getContex().getContentResolver(),imageuri);
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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    private void toastMessage(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

}
