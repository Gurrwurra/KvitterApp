package com.example.kvitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.widget.Toast;

import com.example.kvitter.Activities.StartActivity;
import com.example.kvitter.Activities.Validate_reciept;
import com.example.kvitter.Adapters.FolderAdapter;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.Data;
import com.example.kvitter.Util.ImageHelper;
import com.example.kvitter.Util.Reciept;
import com.example.kvitter.Util.UserData;
import com.example.kvitter.Util.Validate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DatabaseLogic {
    private FirebaseFirestore db;
    private boolean mailExist;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private boolean currentState;

    public boolean mailDoesExists(Context context, String value) {
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").whereEqualTo("mail", value);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(Objects.requireNonNull(task.getResult()).size() > 0){
                    Toast toast = Toast.makeText(context, "Konto med samma mejladress finns redan", Toast.LENGTH_LONG);
                    toast.show();

                    mailExist = true;
                }else{
                    mailExist = false;
                }
            }
        });
        return mailExist;
    }

    public void populateFolders() {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("user_data").document("HINCqfhWGB9XwGtGBtYl");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    UserData data = (UserData) document.toObject(UserData.class);
                      System.out.println("DATA TRANSFER IS COMPLETE!");

                } else {
                    System.out.println("Cached get failed:" + task.getException()); }
            }
        });
    }
// EN RECYLERVIEW (INNEHÅLLER FOLDER I RECYLER MED RECEIPTS) SOM HÄMTAS IN AV METOD FÖR ATT RETURNERA LISTA FRÅN SPECIFIK FOLDER.
// EN LISTA MED FOLDERS [RECYLARVIEW}, VARJE VIEW(FOLDER) INNEHÅLLER LISTA (HOLDER) MED RECEIPTS, METOD SOM DÅ HÄMTAR SPECIFIKT FOLDERNAME OCH KÖR MOT DATABASEN FÖR ATT SKAPA LISTA MED ALLA KVITTON FÖR DEN FOLDER
    public void persNoExists( String value,Context context, ProgressDialog mProgress) {
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").whereEqualTo("personal_number", value);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot task) {
                if(Objects.requireNonNull(task.size())> 0){

                    System.out.println("Personnumret hittades");
                    CurrentId.setUserId(task.getDocuments().get(0).getId());
                    mProgress.dismiss();
                    Intent i = new Intent(context,StartActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }else{
                    mProgress.dismiss();
                    Toast.makeText(context, "Felaktig inlogningsinformation", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void pwdExists(String pwd, String persNo, Context context, ProgressDialog mProgress) {
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").whereEqualTo("pwd", pwd);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot task) {
                if(Objects.requireNonNull(task.size()) > 0){
                    System.out.println("Lösenordet hittades");
                    persNoExists(persNo, context, mProgress);
                }else{
                    mProgress.dismiss();

                    Toast.makeText(context, "Felaktig inlogningsinformation", Toast.LENGTH_LONG).show();
                }
            }
            }
            );
    }
    public void createUser(Context context, String firstname, String surname, String mail, String phone, String address, String city, String pwd, String personalNumber) {
     //   boolean mailExists = mailDoesExists(context, mail);
    //    boolean persNoExists = persNoExists( personalNumber);
   //     if (mailExists == false && persNoExists == false) {
            db = FirebaseFirestore.getInstance();
            CollectionReference users = db.collection("users");

            Map<String, Object> data = new HashMap<>();
            data.put("firstname", firstname);
            data.put("surname", surname);
            data.put("mail", mail);
            data.put("phone", phone);
            data.put("address", address);
            data.put("city", city);
            data.put("pwd", pwd);
            data.put("personal_number", personalNumber);

            db.collection("users")
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast toast = Toast.makeText(context, "DocumentSnapshot written with ID: " + documentReference.getId(), Toast.LENGTH_LONG);
                            createFolder(documentReference.getId());
                            toast.show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast toast = Toast.makeText(context, "Error adding document" + e, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
       // }
      //  else {
            Toast toast = Toast.makeText(context, "Konto med dessa uppgifter finns redan", Toast.LENGTH_LONG);
            toast.show();
       // }
    }

 //GETS CURRENT SEQ. NO AND RUNS METHOD "updateSequenceNumber" WITH SEQ. NO AS PARAMETER
    public void newSequenceNumber (Context context, Uri filePath, String[] receiptInfo) {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("photo_sequence").document("sequence");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int seqNumber =  Integer.parseInt(document.getData().get("seq_id").toString());
                    savePhoto(context, filePath, seqNumber, receiptInfo);
                    updateSequenceNumber(seqNumber);
                } else {
                    System.out.println("Cached get failed:" + task.getException()); }
            }
        });
    }
//UPDATES SEQ.NO IN DOCS BY 1.
    public void updateSequenceNumber(int seqNo) {
        int newSeq = seqNo +1;
        Map<String, Object> seq = new HashMap<>();
        seq.put("seq_id", newSeq);
        db = FirebaseFirestore.getInstance();
        db.collection("photo_sequence").document("sequence")
                .set(seq)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("DocumentSnapshots successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error writing document"+ e);
                    }
                });
    }

    /**
     * Uploads image to Firebase storage
     * @param context
     * @param filePath
     * @param seq
     * @param receiptsInfo
     */
    private void savePhoto(Context context, Uri filePath, int seq, String[] receiptsInfo){

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        byte[] bytePhoto = null;

        Bitmap bitmap = null;
        try {
            bitmap = ImageHelper.getCorrectlyOrientedImage(context, filePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bytePhoto = stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Laddar upp...");
            progressDialog.show();

            String photoName = "reciept/"+ UUID.randomUUID().toString() + "-" + seq;

            saveInformation(receiptsInfo, photoName);

            StorageReference ref = storageReference.child(photoName);
            ref.putBytes(bytePhoto)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Uppladdat", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Misslyckad uppladdning:  "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uppladdat "+(int)progress+"%");
                        }
                    });
        }
    }


    /**
     * Creates default folder for the new registered user
     */
    public void createFolder(String user_id){
        /*
        FirebaseFirestore db;

        db = FirebaseFirestore.getInstance();
        ArrayList<Map> recieptInfor = new ArrayList<>();
        Map<String, Object> folderInformation = new HashMap<>();
        folderInformation.put("receipts", recieptInfor);
        ArrayList<Map> folderInfo = new ArrayList<>();
        folderInfo.add(folderInformation);

        Map<String, Object> folders = new HashMap<>();
        folders.put("Default" ,folderInfo);

*/
        Map<String, Object> recieptData = new HashMap<>();
        recieptData.put("folder", "folder");
     //   String user_id = CurrentId.getUserId();

        db.collection("user_data").document(user_id)
                .set(recieptData, SetOptions.mergeFields("folder"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    /**
     * Saves reveipt to default folder for a specific user
     * @param receiptInfo
     * @param photoName
     */

    //String folderName, String name, String amount, String comment, String photoRef, String supplier)
    private void saveInformation(String[] receiptInfo, String photoName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
    /*    for (int i=0; i < receiptInfo.length; i++) {
            if (receiptInfo[i]== null) {
                receiptInfo[i] = " ";
            }
        }
        */
        UserData userData = new UserData("Hobby",receiptInfo[0],receiptInfo[2],receiptInfo[3],photoName,receiptInfo[1], 1);
        db.collection("testData").document(CurrentId.getUserId()).update("data", FieldValue.arrayUnion(userData));
        /*
        Map<String,Object> docData = new HashMap<>();
        docData.put("folderName", "Default");
        Map<String, Object> nestedReceipt = new HashMap<>();
        nestedReceipt.put("name", receiptInfo[0]);
        nestedReceipt.put("supplier", receiptInfo[1]);
        nestedReceipt.put("amount", receiptInfo[2]);
        nestedReceipt.put("comment", receiptInfo[3]);
        nestedReceipt.put("photoRef", photoName);

        docData.put("receipt", nestedReceipt);
        DocumentReference reference = db.collection("data").document(CurrentId.getUserId());
        reference.update("data",FieldValue.arrayUnion(docData));


        UserData data = new UserData("Hobby;", receiptInfo[0]+";", ";"+receiptInfo[2]+";",
                receiptInfo[3]+";", photoName+";",receiptInfo[1]+";");
        Map<String, Object> recieptMap = new HashMap<>();
        recieptMap.put("name", receiptInfo[0]);
        recieptMap.put("supplier", receiptInfo[1]);
        recieptMap.put("amount", receiptInfo[2]);
        recieptMap.put("comment", receiptInfo[3]);
        recieptMap.put("photoRef", photoName);
*/
     //   DocumentReference myRef = db.collection("user_data").document(CurrentId.getUserId());

     //   myRef.update("folder.!Hobby.receipts", FieldValue.arrayUnion(data));
    }
}
