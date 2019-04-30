package com.example.kvitter.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.kvitter.DataEngine;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentUser;

public class NewUserActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText firstname, surname, mail, phone, address, city, pwd, validatePwd, personalNumber;
    private Button save, regret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        save.setOnClickListener(this);
        regret.setOnClickListener(this);
        bindViews();
    }

    private void bindViews() {
        firstname = findViewById(R.id.add_firstname);
        surname = findViewById(R.id.add_surname);
        mail = findViewById(R.id.add_mail);
        phone = findViewById(R.id.add_phone);
        address = findViewById(R.id.add_address);
        city = findViewById(R.id.add_city);
        pwd = findViewById(R.id.add_pwd);
        validatePwd = findViewById(R.id.add_pwd2);
        personalNumber = findViewById(R.id.add_persNumber);
        save = findViewById(R.id.btn_saveUser);
        regret = findViewById(R.id.btn_regret_user);
    }
    /*
    onClickListener -
    case btn_saveUser : Method will retrieve all data from user input - create new User object as static.class (CurrentUser) and validate it in method checkIfUserExists
    case btn_regret_user : Method will redirect to Activity "LoginActivity.class"
     */
    public void onClick(View v) {
        Button btn = (Button) v;
        switch (btn.getId()) {
            case R.id.btn_saveUser: {
                String userFirstName = firstname.getText().toString();
                String userSurname = surname.getText().toString();
                String userAddress = address.getText().toString();
                String userCity = city.getText().toString();
                String userMail = mail.getText().toString();
                String userPhone = phone.getText().toString();
                String userPwd = pwd.getText().toString();
                String userConfirmPwd = validatePwd.getText().toString();
                String userPersonalNumber = personalNumber.getText().toString();

                if (userPwd.contains(userConfirmPwd)) {
                    User user = new User(userFirstName, userSurname, userAddress, userCity, userPhone, userPersonalNumber, userMail, userPwd);
                    CurrentUser.setUser(user);
                    DataEngine engine = new DataEngine();
                    engine.checkIfUserExists(this);
                } else {
                    Toast toast = Toast.makeText(this, "Lösenorden stämmer inte överrens", Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
            }
            case R.id.btn_regret_user: {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            }

        }
    }
}
