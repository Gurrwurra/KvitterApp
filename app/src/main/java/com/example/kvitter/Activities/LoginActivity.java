package com.example.kvitter.Activities;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kvitter.Logic;
import com.example.kvitter.R;
import com.example.kvitter.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private EditText pwd, usrName;
    private Button login, newUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindViews();


        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewUserActivity.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String user = usrName.getText().toString();
                String password = pwd.getText().toString();
                //    boolean validate = logic.validateUser(user, password);

                //      if (validate == true) {
                Intent i = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(i);
            }

        });
    }
    private void bindViews() {
        newUser = (Button) findViewById(R.id.btn_newUser);
        login = (Button) findViewById(R.id.btn_login);
        usrName = (EditText) findViewById(R.id.txt_usr);
        pwd = (EditText) findViewById(R.id.txt_pwd);
    }

}
