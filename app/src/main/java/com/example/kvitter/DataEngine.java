package com.example.kvitter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.kvitter.Adapters.FolderAdapter;
import com.example.kvitter.Adapters.MyAdapter;
import com.example.kvitter.Util.Folders;
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
    List<String> folderData = new ArrayList<>();
    List<UserData> testData = new ArrayList<>();
    private RecyclerView folderView;
    private RecyclerView.Adapter folderAdapter;
    private RecyclerView.LayoutManager layoutManager;

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
                        System.out.println("Error adding document" + e);
                    }
                });
    }

    public void createAdapter() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("data").document("HINCqfhWGB9XwGtGBtYl")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String fulLData = document.getData().toString();
                            System.out.println("COMPLETE DATA" + " => " + fulLData);
                            populateFolders(fulLData);

                            for(int i=0; i< folderData.size(); i++) {
                                //POPULATE WITH FOLDERNAME
                                testData.add(new UserData(folderData.get(i), UserData.FOLDER_TYPE));
                                populateReciepts(fulLData,folderData.get(i));
                            }

                            for (int j=0; j < testData.size(); j++) {
                                System.out.println("final test: " + testData.get(j).getFolderName() + "   name : "+ testData.get(j).getName());
                            }
                            /*
                            //TODO PLOCKA UT VARJE FOLDERNAME FRÅN DATABAS OCH LAGRA SOM FOLDER_TYPE (FÖR VARJE VARV I DENNA LOOP SÅ SKALL ALLA KVITTON FÖR DENNA FOLDER HÄMTAS IN OCH LAGRAS SOM RECEIPT_TYPE

                            //TODO 2.0 - PLOCKA UT ALLA FOLDERS FRÅN DB I EN LISTA OCH KOLLA ATT NAME INTE REDAN FINNS), SEDAN LOOPA IGENOM DEN LISTAN OCH HÄMTA ALL DATA SOM MATCHAR DET FOLDERNAME
                            testData.add(new UserData("Default", UserData.FOLDER_TYPE));
                            testData.add(new UserData("Default", "test-plankor", "500", "test", "34248234dpsd", "Ica", UserData.RECIEPT_TYPE));

                            testData.add(new UserData("Hobby", UserData.FOLDER_TYPE));
                            testData.add(new UserData("Hobby", "test2- spikar", "200", "test", "fidsfisf93", "Wills", UserData.RECIEPT_TYPE));

                            testData.add(new UserData("Renovering", UserData.FOLDER_TYPE));
                            testData.add(new UserData("Renovering", "test3 - nålar", "1000", "test", "asdsacssa21", "Beijer", UserData.RECIEPT_TYPE));
                            testData.add(new UserData("Renovering", "test4 - skruvar", "300", "test", "ewrewr3241", "ByggMax", UserData.RECIEPT_TYPE));
*/
                        }
                        //SKRIVA UT TESTDATA (KÖRA METODER OCH ANNAT)

                    }
                });
    }
    private void populateReciepts(String data, String folderName) {
        String[] eachObject = data.split("\\{");
        for (int i = 2; i < eachObject.length; i++) {
            //  System.out.println("each object: " + eachObject[i]);

            String[] eachDataInObject = eachObject[i].split(",");
            String [] correctName = new String[1];
            for (int j = 0; j < eachDataInObject.length; j++) {
                //        System.out.println("each data in object: " + eachDataInObject[j]);
                String [] specificFolderName = eachDataInObject[5].split("=");
                correctName = specificFolderName[1].split("\\}");
                if (correctName[0].contains(folderName)) {
                    String [] recieptName = eachDataInObject[2].split("=");
                    String [] recieptAmount = eachDataInObject[0].split("=");
                    String [] recieptComment = eachDataInObject[3].split("=");
                    String [] recieptPhoto = eachDataInObject[4].split("=");
                    String [] recieptCompany = eachDataInObject[1].split("=");

                    testData.add(new UserData(
                            correctName[0],
                            recieptName[recieptName.length-1],
                            recieptAmount[recieptAmount.length-1],
                            recieptComment[recieptComment.length-1],
                            recieptPhoto[recieptPhoto.length-1],
                            recieptCompany[recieptCompany.length-1],
                            UserData.RECIEPT_TYPE));
                  //  testData.add(new UserData("Renovering", "test3 - nålar", "1000", "test", "asdsacssa21", "Beijer", UserData.RECIEPT_TYPE));
                //    testData.add(new UserData(correctName[0],))
            //        System.out.println("test " + dataElements[dataElements.length-1]);
          //          System.out.println("data to save? " + eachDataInObject[j]);
                }
            }

        }
    }
    private void populateFolders(String data) {
      //  System.out.println("full data: " + data);

        String[] eachObject = data.split("\\{");

        for (int i = 2; i < eachObject.length; i++) {
          //  System.out.println("each object: " + eachObject[i]);

            String[] eachDataInObject = eachObject[i].split(",");

            for (int j = 0; j < eachDataInObject.length; j++) {
        //        System.out.println("each data in object: " + eachDataInObject[j]);
                    String [] folderName = eachDataInObject[5].split("=");
                    String [] correctName = folderName[1].split("\\}");
                    if (!folderData.contains(correctName[0])) {
                        folderData.add(correctName[0]);
         //               System.out.println("test " + correctName[0]);
                }
            }
        }
    }
}