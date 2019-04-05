package com.example.kvitter;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseLogic {

    private FirebaseFirestore db;

    private void getMultiDocument(){

      /*  db = FirebaseFirestore.getInstance();

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
                        });*/
    }

    private void getSingleDocument(){

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
}
