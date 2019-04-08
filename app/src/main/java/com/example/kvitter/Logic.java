package com.example.kvitter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.kvitter.Activities.LoginActivity;
import com.example.kvitter.Util.Validate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class Logic {

    public boolean validateUser(String personalNumber, String pwd) {

        DatabaseLogic dbLogic = new DatabaseLogic();
        dbLogic.pwdExists(pwd,personalNumber);
        /*    boolean persNumber = persNo.getState();
        boolean persPwd = usrPwd.getState();

        if (persNumber ==true && persPwd == true) {
            System.out.println("Inloggning suceeesss");
            return true;
        }
        else {
            System.out.println("Inloggning faila");
            return false;
        }
        */
        return true;
    }


    public void testData(Context context){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Toast.makeText(context,"Hello Javatpoint",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }



}
