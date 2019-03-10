package com.example.trackingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public class WriteData {
    private  ProgressDialog pd =null;
    private FirebaseUser user = null;
    private FirebaseFirestore db = null;
    private String userid = "";
    private String mkey = "";
    private StorageReference storageReference= null;
    private Context contex;
    private FragmentManager fragmentManager;

    public WriteData(Context context , FragmentManager fm){
        this.contex=context;
        this.pd = new ProgressDialog(this.contex);
        this.fragmentManager = fm;
        pd.setCancelable(false);
    }


    public WriteData setDb(FirebaseFirestore db) {
        this.db = db;
        return this;
    }

    public void uploadImage(Uri imageuri) {
        Random rand = new Random();
        // Obtain a number between [0 - 49].
        int n = 1;
        pd.setCancelable(false);
        pd.setMessage("Uploading");
        pd.show();
        timerDelayRemoveDialog(15000,pd);
        if (imageuri != null) {
            final StorageReference fileReference = storageReference.child("images/users/" + userid + "/" + n + "." + getFileExtension(imageuri));
            fileReference.putFile(imageuri).continueWithTask(new Continuation< UploadTask.TaskSnapshot, Task< Uri >>() {
                @Override
                public Task < Uri > then(@NonNull Task < UploadTask.TaskSnapshot > task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = (Uri) task.getResult();
                        String imageuri = downloadUri.toString();

                        Map< String, Object > usermap = new HashMap< >();

                        usermap.put("PathImg", imageuri);

                        db.collection("users").document(userid).set(usermap,SetOptions.merge());
                        pd.dismiss();
                    } else {
                        toastMessage("Failed uploading");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMessage("Failed");
                    pd.dismiss();
                }
            });

        } else {
            toastMessage("No image selected");
        }


    }
    public WriteData setExcursion(Map<String, Object> excursionData){
        if(!mkey.equals("") && !userid.equals("") && db!=null){
            pd.show();
            timerDelayRemoveDialog(15000,pd);
            pd.setMessage("Setting Excursion...");
            db.collection("excursion").document(userid)
                    .set(excursionData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            toastMessage("Excursion Activated");
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMessage("Failed Activating Excursion");
                    pd.dismiss();
                }
            });

        }
        return this;
    }
    public void keysCollection(){
        Map<Object,String> key_userid= new HashMap<>();
        if(!mkey.equals("") && !userid.equals("") && db!=null){
            pd.show();
            key_userid.put("useid",userid);
            timerDelayRemoveDialog(15000,pd);
            pd.setMessage("Setting Excursion...");
            db.collection("excursionKeys").document(mkey).set(key_userid)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            toastMessage("Key Set");
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMessage("Failed Setting Key");
                    pd.dismiss();
                }
            });

        }else{
            toastMessage("Please try to set Key");
        }
    }

    public void updateProfile(Map<String, Object> data,String name, final boolean userModifiedWrite){
        if(user!=null && userModifiedWrite){
            pd.show();
            timerDelayRemoveDialog(15000,pd);
            pd.setMessage("Updating data...");
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                toastMessage("User profile updated.");
                            }
                            pd.dismiss();
                        }
                    });
        }
        if(!userid.equals("")){
            pd.show();
            timerDelayRemoveDialog(15000,pd);
            pd.setMessage("Updating data...");
            db.collection("users").document(userid)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(!userModifiedWrite){
                                toastMessage("User profile updated.");
                            }
                            pd.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMessage("Error writing document");
                            pd.dismiss();
                        }
                    });

        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = this.contex.getContentResolver();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        return mtm.getExtensionFromMimeType(cr.getType(uri));
    }

    public WriteData setUser(FirebaseUser user) {
        this.user = user;
        return this;
    }

    public WriteData setUserid(String userid) {
        this.userid = userid;
        return this;
    }
    public WriteData setMkey(String mkey) {
        this.mkey = mkey;

        return this;
    }

    public WriteData setStorageReference(StorageReference storageReference) {
        this.storageReference = storageReference;
        return this;
    }

    private void toastMessage(String message) {
        Toast.makeText(this.contex, message, Toast.LENGTH_SHORT).show();
    }
    public void timerDelayRemoveDialog(long time, final Dialog d){

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(d.isShowing()){
                    NoConnectionDialog noConnectionDialog =  NoConnectionDialog.newInstance(2);
                    noConnectionDialog.show(fragmentManager,"SlowConn");
                    d.dismiss();
                }

            }
        }, time);
    }

}
