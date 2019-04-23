package com.example.kvitter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.example.kvitter.Activities.LoginActivity;
import com.example.kvitter.Activities.StartActivity;
import com.example.kvitter.Activities.User;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.CurrentUser;
import com.example.kvitter.Util.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DataEngine {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DataEngine() {
    }
/*
VALIDATES IF CURRENT DATA ALREADY EXISTS IN DATABASE, IF NOT, METHOD "createUser();" IS CALLED TO CREATE NEW USER
 */
    public void checkIfUserExists(Context context) {
        String personalNumber = CurrentUser.getUser().getPersonalNumber();
        db.collection("users")
                .whereEqualTo("personal_number", personalNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    System.out.println("Finns redan konto med dessa uppgifter");
                                    //    System.out.println(document.getId() + " => " + document.getData());
                                }
                            }
                                else {
                                    createUser(context);
                                }

                        } else {
                            System.out.println("Error getting documents: " + task.getException());
                        }
                    }
                });
    }
    public void updateFolder() {
    }

    public void createFolder(Context context, String folderName) {
        UserData data = new UserData(folderName,UserData.FOLDER_TYPE);
        db = FirebaseFirestore.getInstance();
        db.collection("data").document(CurrentId.getUserId()).update("data", FieldValue.arrayUnion(data));
        Toast toast = Toast.makeText(context, "NEW FOLDER ADDED! ", Toast.LENGTH_LONG);
        toast.show();
    }
    /*
CREATES NEW USER IN COLLECTION "users" WITH USER OJECT FROM STATIC CLASS
 */
    public void createUser(Context context) {
        User user = CurrentUser.getUser();
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast toast = Toast.makeText(context, "DocumentSnapshot written with ID: " + documentReference.getId(), Toast.LENGTH_LONG);
                        toast.show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
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
    public void updateReciept(UserData data, String newFolderName) {

    }
    /*
    VALIDATES PASSWORD AND PERSONAL NUMBER AND RETURNS StartActivity.class IF DATA IS CORRECT
     */
    public void validateUser(Context context) {
        User user = CurrentUser.getUser();
        String personalNumber = user.getPersonalNumber();
        String password = user.getPassword();
        db.collection("users")
                .whereEqualTo("personalNumber", personalNumber)
                .whereEqualTo("password",password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                CurrentId.setUserId(task.getResult().getDocuments().get(0).getId());
                                 Intent intent = new Intent(context, StartActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                            }
                            else {
                                Toast toast = Toast.makeText(context, "Ops..Något blev fel, försök igen", Toast.LENGTH_LONG);
                                toast.show();
                            }

                        } else {
                            System.out.println("Error getting documents: " + task.getException());
                        }
                    }
                });
    }
}

