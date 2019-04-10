package com.example.kvitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.kvitter.Activities.StartActivity;
import com.example.kvitter.Activities.Validate_reciept;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.Reciept;
import com.example.kvitter.Util.Validate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
    public void test() {
        db = FirebaseFirestore.getInstance();


    }
    public void persNoExists( String value,Context context) {
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").whereEqualTo("personal_number", value);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot task) {
                if(Objects.requireNonNull(task.size())> 0){
                    System.out.println("Personnumret hittades");
                    CurrentId.setUserId(task.getDocuments().get(0).getId());
                    Intent i = new Intent(context,StartActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }else{
                    System.out.println("Felaktigt personnummer");
                }
            }
        });
    }
    public void pwdExists(String pwd, String persNo, Context context) {
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").whereEqualTo("pwd", pwd);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot task) {
                if(Objects.requireNonNull(task.size()) > 0){
                    System.out.println("Lösenordet hittades");
                    persNoExists(persNo, context);
                }else{
                    System.out.println("Felaktigt lösenord");
                }
            }
            }
            );
    }

    public void state(boolean state) {
        currentState = state;
        validateUser();
    }
    public boolean validateUser() {

        if (currentState == true) {
            System.out.println("Inloggning suceeesss");
            return true;
        } else {
            System.out.println("Inloggning faila");
            return false;
        }
    }
    public void createUser(Context context, String firstname, String surname, String mail, String phone, String address, String city, String pwd, String personalNumber) {
        boolean mailExists = mailDoesExists(context, mail);
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
    public void newSequenceNumber (Context context, Uri filePath) {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("photo_sequence").document("sequence");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int seqNumber =  Integer.parseInt(document.getData().get("seq_id").toString());
                    saveReciept(context, filePath, seqNumber);
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

    public void getSingleDocument(){
        db = FirebaseFirestore.getInstance();
        DocumentReference myRef = db.collection("user_data").document("pxVDocvZG1vmoBNpZvBE");
        myRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            System.out.println(task.getResult());
                        } else {
                        }
                    }
                });
    }

    private void saveReciept(Context context, Uri filePath, int seq){

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Laddar upp...");
            progressDialog.show();

            StorageReference ref = storageReference.child("reciept/"+ UUID.randomUUID().toString() + "-" + seq);
            ref.putFile(filePath)
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
}
