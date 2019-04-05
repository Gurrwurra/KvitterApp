package com.example.kvitter.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.R;

public class NewUserActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText firstname, surname, mail, phone, address, city, pwd, validatePwd, personalNumber;
    private Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        findViewById(R.id.btn_saveUser).setOnClickListener(this);
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
    }

    public void onClick(View v) {
        String userFirstName = firstname.getText().toString();
        String userSurname = surname.getText().toString();
        String userAddress = address.getText().toString();
        String userCity = city.getText().toString();
        String userMail = mail.getText().toString();
        String userPhone = phone.getText().toString();
        String userPwd = pwd.getText().toString();
        String userConfirmPwd = validatePwd.getText().toString();
        String userPersonalNumber = personalNumber.getText().toString();
        //TODO validate fields - Check with DB - Save to DB

        Boolean checkFields = validateUserData(userMail,userPhone,userPwd,userConfirmPwd,userPersonalNumber);
        if (checkFields == true) {
            Toast.makeText(this,"Validering success!!!",Toast.LENGTH_SHORT).show();
            DatabaseLogic logic = new DatabaseLogic();
            logic.createUser(this,userFirstName,userSurname,userMail,userPhone,userAddress,userCity,userPwd,userPersonalNumber);
        }
        else {
            Toast.makeText(this,"gick inte igenom...",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateUserData(String mail,String phone,String pwd,String validatePwd,String personalNumber) {
        boolean validate = false;

        boolean usrMail = validateMail(mail);
        boolean usrPhone = validatePhone(phone);
        boolean usrPwd = validatePassword(pwd,validatePwd);
        boolean usrPersonalNo= validatePersonalNumber(personalNumber);
        if (!usrMail == true && usrPhone == true && usrPwd == true && usrPersonalNo == true) {
            validate = true;
        }
        return validate;
    }
    private boolean validateMail (String mail) {
        //TODO validera mot databas så mail inte finns
        boolean validate = false;
        if (mail.contains("@")) {
            validate = true;
        }
        return validate;
    }
    private boolean validatePhone (String phone) {
        boolean validate = false;
        if (phone.length() ==(10)) {
            validate = true;
        }
        return validate;
    }
    private boolean validatePassword (String pwd, String validatePwd) {
        boolean validate = false;
        if (pwd.equals(validatePwd)) {
            validate = true;
        }
        return validate;
    }
    private boolean validatePersonalNumber (String personalNumber) {
        boolean validate = false;
        if (personalNumber.length()==12) {
            validate = true;
        }
        return validate;
    }
}
