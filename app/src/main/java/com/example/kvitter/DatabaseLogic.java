package com.example.kvitter;

import android.content.Context;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.widget.Toast;

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

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DatabaseLogic {
    private FirebaseFirestore db;
    private boolean exist;

    public boolean mailDoesExists(Context context, String value) {
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").whereEqualTo("mail", value);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(Objects.requireNonNull(task.getResult()).size() > 0){
                    Toast toast = Toast.makeText(context, "Konto med samma mejladress finns redan", Toast.LENGTH_LONG);
                    toast.show();
                    exist = true;
                }else{
                    exist = false;
                }
            }
        });
        return exist;
    }
    public boolean persNoExists(Context context, String value) {
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").whereEqualTo("personal_number", value);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(Objects.requireNonNull(task.getResult()).size() > 0){
                    Toast toast = Toast.makeText(context, "Konto med samma personnummer finns redan", Toast.LENGTH_LONG);
                    toast.show();
                    exist = true;
                }else{
                    exist = false;
                }
            }
        });
        return exist;
    }


    public void createUser(Context context, String firstname, String surname, String mail, String phone, String address, String city, String pwd, String personalNumber) {
        boolean mailExists = mailDoesExists(context, mail);
        boolean persNoExists = persNoExists(context, personalNumber);
        if (mailExists == false && persNoExists == false) {
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
        }
        else {
            Toast toast = Toast.makeText(context, "Konto med dessa uppgifter finns redan", Toast.LENGTH_LONG);
            toast.show();
        }
    }
    private void getMultiDocument(){
/*
        db = FirebaseFirestore.getInstance();

        DocumentReference myRef = db.collection("user_data").
                get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        System.out.println(document.getData());
                                    }
                                } else {
                                    System.out.println(task.getException());
                                }
                            }
                        });
    */}

    public void getSingleDocument(){

        db = FirebaseFirestore.getInstance();


        DocumentReference myRef = db.collection("user_data").document("pxVDocvZG1vmoBNpZvBE");

        myRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            //Toast toast = Toast.makeText(getApplicationContext(), document.getId() + " => " + document.getData(), Toast.LENGTH_LONG);
                            //toast.show();

                            System.out.println(task.getResult());


                        } else {
                            //Toast toast = Toast.makeText(getApplicationContext(), "Error getting documents: " + task.getException(), Toast.LENGTH_LONG);
                            //toast.show();
                        }
                    }
                });
    }

    private boolean validatePersonalNumber() {
        boolean validate = false;

        return validate;
    }
}
