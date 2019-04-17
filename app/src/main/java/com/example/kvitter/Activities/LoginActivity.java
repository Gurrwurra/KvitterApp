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

import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.Logic;
import com.example.kvitter.R;


public class LoginActivity extends AppCompatActivity{
    private EditText pwd, usrName;
    private Button login, newUser;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindViews();

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
                DatabaseLogic logic = new DatabaseLogic();
                String user = usrName.getText().toString();
                String password = pwd.getText().toString();
                logic.populateFolders();
        //        logic.testMethod();
                logic.pwdExists(password,user,getApplicationContext(), mProgress);
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
