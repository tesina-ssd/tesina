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
/**
 * <p>
 * Questa classe serve per fare le operazioni sul database / storage 
 * di firestore. Al costruttore vengono passati conterx e fragmentmanager
 * per lavorare con la grafica di android.
 * <p>
 * <p>
 * La classe contiene anche dei metodi set per iniziallizare lo stato della classe e
 * altri metodi utili per il caricamento dei dati sul db e storage
 * <p>
 * <ul>
 * <li>WriteData(Context context , FragmentManager fm)
 * <li>WriteData setDb(FirebaseFirestore db)
 * <li>uploadFile(Uri fileUri, final String folder, final boolean showLoadingDialog, final boolean savePathInDatabase)
 * <li>WriteData setEcursion(Map<String, Object> data)
 * <li>setUserLocation(Map<String, Object> data)
 * <li>uploadGenericData(String collection,Map<String,Object> data)
 * <li>keysCollection()
 * <li>updateProfile(Map<String, Object> data,String name, final boolean userModifiedWrite)
 * <li>getFileExtension(Uri uri)
 * <li>WriteData setUser(FirebaseUser user) 
 * <li>WriteData setUserid(String userid)
 * <li>WriteData setMkey(String mkey)
 * <li>WriteData setStorageReference
 * <li>toastMessage(String message)
 * </ul>
 * <p>
 * @author      Singh Harpreet
 * @author      Delmastro Andrea
 * @version     %I%, %G%
 * @since       1.0
 */
public class WriteData {
    private  ProgressDialog pd =null;
    private FirebaseUser user = null;
    private FirebaseFirestore db = null;
    private String userid = "";
    private String mkey = "";
    private StorageReference storageReference= null;
    private Context contex;
    private FragmentManager fragmentManager;
   /**
     * Costruttore della classe WriteData();
     * @param context : Sarebbe il context in cui si trova Activity o il fragment che crea l'instanza di questa classe
     * @param fm : FragmentManager, non sempre viene utilizzato , passarlo se si vogliono utilizzare dei che l'utlizzano.
     *             La funzione di fm sarebbe quella di poter creare un dialog grafico.
     */
    public WriteData(Context context , FragmentManager fm){
        this.contex=context;
        this.pd = new ProgressDialog(this.contex);
        this.fragmentManager = fm;
        this.userid = FirebaseAuth.getInstance().getUid();
        this.storageReference = FirebaseStorage.getInstance().getReference();
        pd.setCancelable(false);
    }

   /**
     * Un metodo setter per inizializzare il FirebaseFirestore Database della classe
     * @param db : Sarebbe 
     * @param fm : FragmentManager, non sempre viene utilizzato , passarlo se si vogliono utilizzare dei che l'utlizzano.
     *             La funzione di fm sarebbe quella di poter creare un dialog grafico.
     */
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
            showStandardLoadingDialog();
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
                        dismissStandardLoadingDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMessage("Failed");
                dismissStandardLoadingDialog();
            }
        });
    }

    public void keysCollection(){
        Map<Object,String> key_userid= new HashMap<>();
        if(!mkey.equals("") && !userid.equals("") && db!=null){
            showStandardLoadingDialog();
            key_userid.put("useid",userid);
            db.collection("excursionKeys").document(mkey).set(key_userid)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            toastMessage("Key Set");
                            dismissStandardLoadingDialog();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMessage("Failed Setting Key");
                    dismissStandardLoadingDialog();
                }
            });

        }else{
            toastMessage("Please try to set Key");
        }
    }

    public void updateProfile(Map<String, Object> data,String name, final boolean userModifiedWrite){
        if(user!=null && userModifiedWrite){
            showStandardLoadingDialog();
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
                            dismissStandardLoadingDialog();
                        }
                    });
        }
        if(!userid.equals("")){
            showStandardLoadingDialog();
            db.collection("users").document(userid)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(!userModifiedWrite){
                                toastMessage("User profile updated.");
                            }
                            dismissStandardLoadingDialog();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMessage("Error writing document");
                            dismissStandardLoadingDialog();
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
        pd.setMessage("Caricamento in corso...");
        pd.show();
        UsefulMethods.timerDelayRemoveDialog(15000,pd,fragmentManager);
    }

    private void dismissStandardLoadingDialog() {
        pd.dismiss();
    }


}
