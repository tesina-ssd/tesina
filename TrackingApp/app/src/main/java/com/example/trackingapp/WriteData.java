package com.example.trackingapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;

public class WriteData {
    private  ProgressDialog pd =null;
    private FirebaseUser user = null;
    private FirebaseFirestore db;
    private String userid = null;
    private StorageReference storageReference= null;
    private Context contex;
    public WriteData(Context context){
        this.contex=context;
        this.pd = new ProgressDialog(this.contex);
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

                        db.collection("users").document(userid).update(usermap);
                        pd.dismiss();
                    } else {
                        toastMessage("Failed uploading");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMessage("Failed");
                }
            });

        } else {
            toastMessage("No image selected");
        }


    }
    public void updateProfile(Map<String, Object> data,String name, final boolean userModifiedWrite, boolean canWrite){
        pd.setCancelable(false);
        pd.show();
        pd.setMessage("Updating data...");
        if(user!=null && userModifiedWrite){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            }
                        }
                    });
        }
        if(!userid.equals("") && canWrite){
            db.collection("users").document(userid).update(data)
                    //.set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(!userModifiedWrite){
                                toastMessage("User profile updated.");
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMessage("Error writing document");
                        }
                    });
            pd.dismiss();
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


    public WriteData setStorageReference(StorageReference storageReference) {
        this.storageReference = storageReference;
        return this;
    }

    private void toastMessage(String message) {
        Toast.makeText(this.contex, message, Toast.LENGTH_SHORT).show();
    }
}
