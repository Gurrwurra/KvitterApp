package com.example.kvitter.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kvitter.Logic;
import com.example.kvitter.R;

public class LoginActivity extends AppCompatActivity {
    private EditText pwd, usrName;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindViews();
        Logic logic = new Logic();

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String user = usrName.getText().toString();
                String password = pwd.getText().toString();
                boolean validate = logic.validateUser(user, password);

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
