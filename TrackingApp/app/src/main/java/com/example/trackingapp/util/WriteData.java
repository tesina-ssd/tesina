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
     * @param db : databse su cui lavorare.
     * @return this-> ritorno l'istanza corrente della classe , in questo caso posso usare 
     *         chiamare un'altro metodo.
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
   /**
     * Metodo per fare upload dei dati dell'escursione sul Database.
     * Questo metodo in realta chiama upload generic passandoli la collezione e i dati da caricare.
     * @param data : Map<String, Object> data , è un hash chiave-valore, sarebbero i dati dell'escursione
     *               da caricare sul database. FirebaseFirestore database accetta solamente Hash tables.
     * @return this-> ritorno l'istanza corrente della classe , in questo caso posso usare 
     *         chiamare un'altro metodo.
     */
    public WriteData setEcursion(Map<String, Object> data){
        if(!mkey.equals("") && !userid.equals("") && db!=null){
            showStandardLoadingDialog();
            uploadGenericData("excursion",data);
        }
        return this;
    }
   /**
     * Metodo per fare upload della posizione e dell'ora corrente.
     * Questo metodo in realta chiama upload generic passandoli la collezione e i dati da caricare.
     * @param data : Map<String, Object> data , è un hash chiave-valore, sarebbero i dati due dati da caricare
     *               sul database (posizione e l'ora ) 
     *               FirebaseFirestore database accetta solamente Hash tables.
     * @return void-> questo perchè carico i dati e basta.
     */
    void setUserLocation(Map<String, Object> data){
        if(db!=null && !userid.equals("")){
            uploadGenericData("excursion",data);
        }else{
            toastMessage("Errore in upload della posizione prova");
        }
    }
   /**
     * Questo metodo serve per caricare i dati sul database , e' uno dei metodi principali.
     * Lavora su più collezioni.
     * @param collection : è un parametro di tipo stringa , in quanto sarebbe il nome della collezione 
                           su cui caricare i dati, ciò permette a questo metodo di essere flessibile 
                           e lavorare su qualsiasi collezione.
     * @return void-> non ritorno nessun dato, si presume che vada a buon fine altrimenti viene visuallizato un toast :(
     */
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
   /**
     * Questo metodo serve per creare un documento con la chiave generata dall'utente e settare come valore 
     * id dell'utente.Viene fatto un controllo che la chiave e userid non siano null e soprattutto il db sia iniziallizzato.
     * E' un metodo separato perche' lavora anche con la chiave dell'utente.
     * @return void-> non ritorno nessun dato, si presume che vada a buon fine altrimenti viene visuallizato un toast :(
     */
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
   /**
     * Serve per aggiornare il profilo dell'utente.
     * @param data : Hash table che contiene dati inerenti all'utente tranne l'username , perchè essendo gestito da 
     *               Firebase devo modficarlo separamente.
     * @param name : sarebbe username modficato dall'modficato.
     * @param userModifiedWrite : Parametro di tipo boleano ,che mi indica se devo modicare l'username of no.
     * @return void-> non ritorno nessun dato, si presume che vada a buon fine altrimenti viene visuallizato un toast :(
     */
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
   /**
     * Serve prendere l'estensione del file dato URI.
     * @param uri : Percorso del file nel storage locale.
     * @return String : Ritorna l'estensione del file.
     */
    private String getFileExtension(Uri uri) {
        ContentResolver cr = this.contex.getContentResolver();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        return mtm.getExtensionFromMimeType(cr.getType(uri));
    }
   /**
     * Un metodo setter per inizializzare l'user 
     * @param user : sarebbe user corrente.
     * @return this-> ritorno l'istanza corrente della classe , in questo caso posso usare 
     *         chiamare un'altro metodo.
     */
    public WriteData setUser(FirebaseUser user) {
        this.user = user;
        return this;
    }
   /**
     * Un metodo setter per inizializzare l'id , questo metodo puo' essere anche tolto in quanto 
     * dall'user posso prendere l'id.
     * @param userId : sarebbe id dell'utente.
     * @return this-> ritorno l'istanza corrente della classe , in questo caso posso usare 
     *         chiamare un'altro metodo.
     */
    public WriteData setUserid(String userid) {
        this.userid = userid;
        return this;
    }
   /**
     * Un metodo setter per inizializzare la chiave dell'escursione. Questo metodo può essere tolto tolto
     * in quanto potrei passare la chiave al posto di settarla
     * @param mkey : sarebbe la chiave.
     * @return this-> ritorno l'istanza corrente della classe , in questo caso posso usare 
     *         chiamare un'altro metodo.
     */
    public WriteData setMkey(String mkey) {
        this.mkey = mkey;
        return this;
    }
   /**
     * Server per iniziallizare storageReference.
     * StorageReferencea rappresenta un riferimento a un oggetto Google Cloud Storage. Gli sviluppatori possono caricare e scaricare 
     * oggetti, ottenere / impostare i metadati dell'oggetto ed eliminare un oggetto in un percorso specificato.
     * @param storageReference : sarebbe il riferimento al cloud da assegnare storageReference locale.
     * @return this-> ritorno l'istanza corrente della classe , in questo caso posso usare 
     *         chiamare un'altro metodo.
     */
 
    public WriteData setStorageReference(StorageReference storageReference) {
        this.storageReference = storageReference;
        return this;
    }
   /**
     * Server Visualizzare un toast.
     * @param message : messaggio da visualizzare dal toast.
     * @return void.
     */
    private void toastMessage(String message) {
        Toast.makeText(this.contex, message, Toast.LENGTH_SHORT).show();
    }
   /**
     * Server Visualizzare il dialog.
     * @return void.
     */
    private void showStandardLoadingDialog() {
        pd.setMessage("Caricamento in corso...");
        pd.show();
        UsefulMethods.timerDelayRemoveDialog(15000,pd,fragmentManager);
    }
   /**
     * Serverchiudere il dialog.
     * @return void.
     */
    private void dismissStandardLoadingDialog() {
        pd.dismiss();
    }


}
