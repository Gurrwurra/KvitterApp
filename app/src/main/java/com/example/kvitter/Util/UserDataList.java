package com.example.kvitter.Util;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserDataList {
    List<UserData> listOfData;

    public UserDataList() {
        this.listOfData= new ArrayList<>();
        updateFolder();
    }

    private void updateFolder() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("data").document("o18TaVU4vosbRukSbO8S")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            UserData userData = document.toObject(UserData.class);
                            //   System.out.println("test: " + document.get("500.amount"));
                            List<UserData> test = new ArrayList<>();
                            test.add(document.toObject(UserData.class));
                       //     UserData newData = new UserData(userData);
                         //   test.add(newData);
                        }
                    }
                });
    }

}

