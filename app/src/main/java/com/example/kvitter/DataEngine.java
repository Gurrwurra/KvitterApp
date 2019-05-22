package com.example.kvitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.kvitter.Activities.AddReceiptActivity;
import com.example.kvitter.Activities.LoginActivity;
import com.example.kvitter.Activities.StartActivity;
import com.example.kvitter.Activities.User;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.CurrentReceipt;
import com.example.kvitter.Util.CurrentUser;
import com.example.kvitter.Util.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataEngine {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<Boolean> state = new ArrayList<>();
    private List <String> keys = new ArrayList<>();
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
                                    //TODO toast message
                                    System.out.println("Finns redan konto med dessa uppgifter");
                               }
                            }
                                else {
                                    createUser(context);
                                }

                        } else {
                            //TODO toast message
                            System.out.println("Error getting documents: " + task.getException());
                        }
                    }
                });
    }

    /**
     * Method to update name of specific folder. Removes old key and creates a new object in database with newFolderName as key
     * @param newFolderName name of new folder
     * @param oldFolderName name of folder to change
     * @param data object data for current folder
     */
    public void updateFolder(String newFolderName, String oldFolderName, UserData data) {
        Map<String, Object> newValues = new HashMap<>();
        Map<String, Object> removeOldKey = new HashMap<>();
        removeOldKey.put(oldFolderName,FieldValue.delete());
        db.collection("data").document(CurrentId.getUserId()).update(removeOldKey);
        String newKey = newFolderName;
        data.setFolderName(newFolderName);
        newValues.put(newKey, data);
        db.collection("data").document(CurrentId.getUserId()).update(newValues);
        updateReceiptsInFolder(oldFolderName, newFolderName);
    }

    /**
     * Method to update folderName for all receipts in current folder that changes name
     * Retrievs object from database and updates their folderName to new value (newFolderName)
     * @param oldFolderName
     * @param newFolderName
     */
    private void updateReceiptsInFolder(String oldFolderName, String newFolderName) {
        db.collection("data").document(CurrentId.getUserId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> map = document.getData();

                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                int type = Integer.parseInt(document.get(key+ ".type").toString());
                                String folderName = document.get(key+".folderName").toString();
                                if (folderName.contains(oldFolderName) && type ==1) {
                                    db.collection("data").document(CurrentId.getUserId())
                                            .update(
                                                    key+".folderName", newFolderName);
                                }

                            }

                        }
                    }
                });
    }
/*
SKICKAR MED KEY FÖR SPECIFIKT KVITTO OCH ALL DATA SOM SKALL UPPDATERAS
 */

    /**
     * Method to update data in specific receipt - if keyName is unchanged then method will overwrite data in that object with value from param data
     * else if keyName is changed (name of receipt) then method will remove that object from database and create a new one with new keyValue from data.getName
     * @param oldKeyName
     * @param data
     */
    public void updateReciept(String oldKeyName, UserData data) {
        Map<String, Object> newValues = new HashMap<>();

        if (oldKeyName.contains(data.getName())) {
            newValues.put(oldKeyName, data);
            db.collection("data").document(CurrentId.getUserId()).update(newValues);
        }
        else {
            Map<String, Object> removeOldKey = new HashMap<>();
            removeOldKey.put(oldKeyName,FieldValue.delete());
            db.collection("data").document(CurrentId.getUserId()).update(removeOldKey);
            String newKey = data.getName();
            newValues.put(newKey, data);
            db.collection("data").document(CurrentId.getUserId()).update(newValues);
        }
    }

    /**
     * Method to create new folder with folderName from user input and store it in database
     * @param folderName
     */
    public void createFolder(String folderName) {
        UserData newFolder = new UserData();
        newFolder.setFolderName(folderName);
        newFolder.setType(0);
        Map<String, Object> newValues = new HashMap<>();
        newValues.put(folderName,newFolder);
        db.collection("data").document(CurrentId.getUserId()).update(newValues);
    }

    /**
     * Method to create default map for new user. Creates map "Övriga kvitton" in users data.document
     * @param id = id of document from new user
     */
    public void createFolderNewUser(String id){
        UserData newFolder = new UserData();
        newFolder.setFolderName("Övriga kvitton");
        newFolder.setType(0);
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("Övriga kvitton",newFolder);
        db.collection("data").document(id).update(newValues);
    }

    /**
     * Method to create new user, gets object user from static class CurrentUser.getUser()
     * retrievs id for new document and calls method createFolderNewUser
     * @param context
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
                        createFolderNewUser(documentReference.getId());
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

    /**
     * Method to validate user input with database. Searching database to see if field for personalNumber and password matches
     * user input - to determine if user enter correct data.  If successful method will return Activity "StartActivity.class"
     * else method will show toast and ask user to try again
     * @param context
     * @param mProgress
     */
    public void validateUser(Context context, ProgressDialog mProgress) {
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
                                    mProgress.dismiss();
                                    context.startActivity(intent);
                            }
                            else {
                                mProgress.dismiss();
                                Toast toast = Toast.makeText(context, "Ops..Något blev fel, försök igen", Toast.LENGTH_LONG);
                                toast.show();
                            }

                        } else {
                            System.out.println("Error getting documents: " + task.getException());
                        }
                    }
                });
    }

    public void checkReceiptName(String name, Context context, Intent intent){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("data").document(CurrentId.getUserId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> map = document.getData();

                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                System.out.println(key);
                                if(key.equals(name)){
                                    keys.add(key);
                                    System.out.println("test: " + name  + "    " + key);
                                    Toast.makeText(context, "Det verkar som att du redan har ett kvitto med detta namn", Toast.LENGTH_LONG).show();

                                }
                            }

                        }
                    });
        for (int i=0; i < keys.size(); i++) {
            if (!keys.get(i).equals(name)) {
                keys.add("no");
                context.startActivity(intent);
            }
        }
        }
    }



