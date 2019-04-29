package com.example.kvitter;

import android.app.ProgressDialog;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataEngine {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DataEngine() {
    }

    /*
    #####################LATHUND FÖR DATABASEN##################################
FÖR ATT HÄMTA KEY TILL MAPP SÅ SKALL VÄRDEN VARA NULL (name = null) MEN folderName = SpecificFolderName
FÖR ATT HÄMTA KEY TILL KVITTO SÅ ÄR DET get(namnPåKvittot)
FÖR ATT HÄMTA ETT VÄRDE FRÅN KVITTO SÅ ÄR DET T EX ****  .get(namnPåKvittot.amount) för att få beloppet på det kvittot   *****

EXEMPELKOD:
    DocumentSnapshot document = task.getResult();

###DETTA HÄMTAR SUPPLIER FÖR SPECIFIKT KVITTO
    document.get(namnPåKvittot + ".supplier").toString();

###DETTA SÄTTER ETT VÄRDE FÖR SPECIFIKT KVITTO
    UserData newData = new UserData();
    UserData userData = new UserData("t1","t1Kvitto","500","testarLäggaTill,"tstFoto,"henrik, 1);
    db.collection("data").document(CurrentId.getUserId()).update("nytt kvitto", userData);

######################################################################################
 */

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
    public void updateReciept(String oldKeyName, UserData data) {
        Map<String, Object> newValues = new HashMap<>();

        //VALIDERAR ATT KEY MATCHAR OCH SKRIVER ÖVER BEFINTLIGT DOKUMENT
        if (oldKeyName.contains(data.getName())) {
            newValues.put(oldKeyName, data);
            db.collection("data").document(CurrentId.getUserId()).update(newValues);
        }
        //OM ANVÄNDAREN BYTER NAMN PÅ KVITTO (KEY) SÅ TAS GAMLA MAPPEN BORT OCH DEN NYA SKAPAS
        else {
            Map<String, Object> removeOldKey = new HashMap<>();
            removeOldKey.put(oldKeyName,FieldValue.delete());
            db.collection("data").document(CurrentId.getUserId()).update(removeOldKey);
            String newKey = data.getName();
            newValues.put(newKey, data);
            db.collection("data").document(CurrentId.getUserId()).update(newValues);
        }
    }

    public void createFolder(Context context, String folderName) {
        UserData newFolder = new UserData();
        newFolder.setFolderName(folderName);
        newFolder.setType(0);
        Map<String, Object> newValues = new HashMap<>();
        newValues.put(folderName,newFolder);
        db.collection("data").document(CurrentId.getUserId()).update(newValues);
    }

    public void createFolderNewUser(String id){
        UserData newFolder = new UserData();
        newFolder.setFolderName("Övriga kvitton");
        newFolder.setType(0);
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("Övriga kvitton",newFolder);
        db.collection("data").document(id).update(newValues);
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
    /*
    VALIDATES PASSWORD AND PERSONAL NUMBER AND RETURNS StartActivity.class IF DATA IS CORRECT
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
}

