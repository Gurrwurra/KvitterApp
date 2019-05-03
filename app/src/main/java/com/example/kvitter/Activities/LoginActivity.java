package com.example.kvitter.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.kvitter.DataEngine;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentUser;


public class LoginActivity extends AppCompatActivity {
    private EditText pwd, usrName;
    private Button login, newUser;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        bindViews();
        setProgressBar();

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewUserActivity.class);
                startActivity(i);
            }
        });
        /*
        OnClick - sets new User object and runs method (validateUser) in DataEngine class to validate account
         */
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
    /*
    Sets progress bar for validation of login
     */
    private void setProgressBar() {
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Kollar uppgifter...");
        mProgress.setMessage("Var god vänta...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
    }

    private void bindViews() {
        newUser = (Button) findViewById(R.id.btn_newUser);
        login = (Button) findViewById(R.id.btn_login);
        usrName = (EditText) findViewById(R.id.txt_usr);
        pwd = (EditText) findViewById(R.id.txt_pwd);
    }
}
