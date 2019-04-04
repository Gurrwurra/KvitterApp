package com.example.kvitter.Activities;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kvitter.Logic;
import com.example.kvitter.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText pwd, usrName;
    private Button login;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindViews();
     //   Logic logic = new Logic();
     //   String url = "https://kvitterapp-21c55.firebaseio.com/users";
     //   mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(url);
       mDatabase = FirebaseDatabase.getInstance().getReference("users");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String resp = dataSnapshot.getValue(String.class);
                Toast.makeText(getApplicationContext(),"Hello Javatpoint",Toast.LENGTH_SHORT).show();
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

            }
        };
        mDatabase.addValueEventListener(postListener);
        //logic.testData(this);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String user = usrName.getText().toString();
                String password = pwd.getText().toString();
            //    boolean validate = logic.validateUser(user, password);

                //      if (validate == true) {
                Intent i = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(i);
            }
            /*    else {
                    Toast toast = Toast.makeText(getApplicationContext(), "FAIL bre", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });*/
        });
    }
    private void bindViews() {
        login = (Button) findViewById(R.id.btn_login);
        usrName = (EditText) findViewById(R.id.txt_usr);
        pwd = (EditText) findViewById(R.id.txt_pwd);
    }
}
