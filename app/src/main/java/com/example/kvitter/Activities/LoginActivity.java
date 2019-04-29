package com.example.kvitter.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kvitter.DataEngine;
import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.Logic;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.CurrentUser;
import com.example.kvitter.Util.UserData;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {
    private EditText pwd, usrName;
    private Button login, newUser;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindViews();
        DataEngine engine = new DataEngine();
        //Testmetod för att updatera folderName
        //  engine.updateReciept();
     //   saveInformation();
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Kollar uppgifter...");
        mProgress.setMessage("Var snäll att vänta...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewUserActivity.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                DataEngine engine = new DataEngine();
                String user = usrName.getText().toString();
                String password = pwd.getText().toString();
                User userAttempt = new User();
                userAttempt.setPersonalNumber(user);
                userAttempt.setPassword(password);
                CurrentUser.setUser(userAttempt);
                engine.validateUser(getApplicationContext(), mProgress);
            }
        });
    }


    private void bindViews() {
        newUser = (Button) findViewById(R.id.btn_newUser);
        login = (Button) findViewById(R.id.btn_login);
        usrName = (EditText) findViewById(R.id.txt_usr);
        pwd = (EditText) findViewById(R.id.txt_pwd);
    }

    private void saveInformation() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//String folderName, String name, String amount, String comment, String photoRef, String supplier)
        UserData userData = new UserData("Företag", "bensinkvitto", "850", "företagsresa", "43dec0d4-0d3f-45cc-ac61-199175fce35e-68", "OKQ8", 1);
        UserData userData1 = new UserData("Företag", "affärslunch", "1500", "bjöd kunder på lunch", "43dec0d4-0d3f-45cc-ac61-199175fce35e-68", "Ching Chong", 1);
        UserData userData2 = new UserData("Hobby", "Snickarverkstad", "3000", "Hobbyverksamhet", "43dec0d4-0d3f-45cc-ac61-199175fce35e-68", "Bauhaus", 1);

        db.collection("data").document(
                "o18TaVU4vosbRukSbO8S").update("bensinkvitto", userData);
        db.collection("data").document("o18TaVU4vosbRukSbO8S").update("affärslunch", userData1);
        db.collection("data").document("o18TaVU4vosbRukSbO8S").update("Snickarverkstad", userData2);
    }
}
