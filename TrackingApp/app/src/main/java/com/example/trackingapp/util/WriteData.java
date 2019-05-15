package com.example.trackingapp.util;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        this.userid = FirebaseAuth.getInstance().getUid();
        this.storageReference = FirebaseStorage.getInstance().getReference();
        pd.setCancelable(false);
    }


    public WriteData setDb(FirebaseFirestore db) {
        this.db = db;
        return this;
    }

    /**
     * Metodo che memorizza all'interno dello storage di Firebase un file generico.
     * @param fileUri : l'Uri del file da memorizzare
     * @param folder : il percorso della cartella dove memorizzare il file
     * @param showLoadingDialog : possibilità di visualizzare un dialog bloccante durante il caricamento
     * @param savePathInDatabase : possibilità di memorizzare l'url del file nello storage all'interno del database
     *                             nella sezione corrispondente (es: per un path di storage users/ProfilePhoto/userID)
     *                             l'url viene memorizzato in users/userID {ProfilePhoto} seguendo la logica dei nomi
     *                             assegnati al database e allo storage
     */
    public void uploadFile(Uri fileUri, final String folder, final boolean showLoadingDialog, final boolean savePathInDatabase) {

        if(showLoadingDialog) showStandardLoadingDialog();

        if (fileUri != null) {
            Log.d("STORAGE_REF", folder + "/" + userid + "." + getFileExtension(fileUri));

            final StorageReference fileReference = storageReference.child(folder + "/" + userid + "." + getFileExtension(fileUri));
            fileReference.putFile(fileUri).continueWithTask(new Continuation< UploadTask.TaskSnapshot, Task< Uri >>() {
                @Override
                public Task < Uri > then(@NonNull Task < UploadTask.TaskSnapshot > task) throws Exception {
                    if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(savePathInDatabase && task.isSuccessful() && task.getResult() != null) {
                        Map< String, Object > dataToUpload = new HashMap< >();
                        dataToUpload.put(folder.substring(folder.lastIndexOf("/") + 1), task.getResult().toString());
                        db.collection(folder.substring(0, folder.indexOf("/"))).document(userid).set(dataToUpload, SetOptions.merge());

                        if(showLoadingDialog) dismissStandardLoadingDialog();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMessage("Errore nel caricamento dei dati");
                    if(showLoadingDialog) dismissStandardLoadingDialog();
                }
            });
        }
    }

    public WriteData setEcursion(Map<String, Object> data){
        if(!mkey.equals("") && !userid.equals("") && db!=null){
            pd.show();
            UsefulMethods.timerDelayRemoveDialog(15000,pd,fragmentManager);
            pd.setMessage("Setting Excursion...");
            uploadGenericData("excursion",data);
        }
        return this;
    }
    void setUserLocation(Map<String, Object> data){
        if(db!=null && !userid.equals("")){
            uploadGenericData("excursion",data);
        }else{
            toastMessage("setkey,db");
        }

    }
    private void uploadGenericData(String collection,Map<String,Object> data){
        db.collection(collection).document(userid)
                .set(data,SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMessage("Failed");
                pd.dismiss();
            }
        });
    }

    public void keysCollection(){
        Map<Object,String> key_userid= new HashMap<>();
        if(!mkey.equals("") && !userid.equals("") && db!=null){
            pd.show();
            key_userid.put("useid",userid);
            UsefulMethods.timerDelayRemoveDialog(15000,pd,fragmentManager);
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
            UsefulMethods.timerDelayRemoveDialog(15000,pd,fragmentManager);
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
            UsefulMethods.timerDelayRemoveDialog(15000,pd,fragmentManager);
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

    private void showStandardLoadingDialog() {
        pd.setCancelable(false);
        pd.setMessage("Caricamento in corso...");
        pd.show();
        UsefulMethods.timerDelayRemoveDialog(15000,pd,fragmentManager);
    }

    private void dismissStandardLoadingDialog() {
        pd.dismiss();
    }


}
