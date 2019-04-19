package com.example.kvitter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.kvitter.Util.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;

public class DataEngine {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<UserData> receiptData = new ArrayList<>();

    public void addUser(String firstName, String lastName, int born) {
        Map<String, Object> user = new HashMap<>();
        user.put("first", firstName);
        user.put("middle", "Mathison");
        user.put("last", lastName);
        user.put("born", born);
// Add a new document with a generated ID
        db.collection("user")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println( "Error adding document"+ e);
                    }
                });
    }
    public void readData() {
        db.collection("data").document("HINCqfhWGB9XwGtGBtYl")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String fulLData = document.getData().toString();
                            String [] eachObject = fulLData.split("\\{");
                            System.out.println("COMPLETE DATA"+ " => " + fulLData);
                            for(int i =2; i < eachObject.length; i++) {
                                UserData data = new UserData();
                                String [] eachDataInObject = eachObject[i].split(",");
                                for(int j =0; j < eachDataInObject.length; j++) {
                                    String [] specificValue = eachDataInObject[j].split("=");
                                    System.out.println("TESTDATA: " +specificValue[0]);
                                }
                                //TODO PLOCKA UT VÄRDEN I VARJE OBJEKT (RECEIPTS) OCH LAGRA I USERDATA
                        //        System.out.println("PART OF DATA" + " => " + eachObject[i]);
                                data.setAmount(eachDataInObject[0]);
                                data.setSupplier(eachDataInObject[1]);
                                data.setName(eachDataInObject[2]);
                                data.setComment(eachDataInObject[3]);
                                data.setPhotoRef(eachDataInObject[4]);
                                data.setFolderName(eachDataInObject[5]);
                                receiptData.add(data);
                            }
                        //TODO HÄR SKA LISTAN SKICKAS TILL ADAPTER FÖR ATT SKRIVA UT ALLA KVITTON (I ADADPTERN SKA FOLDERNAME KOLLAS FÖR ATT SKRIVAS UT PÅ RÄTT STÄLLE)
                          printUserData(receiptData);
                        }
                         else {
                            System.out.println("Error getting documents."+ task.getException());
                        }
                    }
                });
         }

    private void printUserData(List<UserData> data) {
        for(int i=0; i<data.size(); i++) {
      //      System.out.println("Kvitto: " + data.get(i).getName() + "\n Summa: " + data.get(i).getAmount() + "\n Supplier: " + data.get(i).getSupplier() + "\n Mapp: " + data.get(i).getFolderName());
        }
    }
}
