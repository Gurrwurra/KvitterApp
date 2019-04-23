package com.example.kvitter.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kvitter.Activities.AddReceiptActivity;
import com.example.kvitter.Activities.LoginActivity;
import com.example.kvitter.Activities.MyAccountActivity;
import com.example.kvitter.Activities.MyReceiptActivity;
import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.CurrentUser;
import com.example.kvitter.Util.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView welcome;
    private List<UserData> test;
    private UserData user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setListiners();
        String personalNumber = CurrentUser.getUser().getPersonalNumber();
        String birthDate = personalNumber.substring(0,8);
        String lastNumbers = personalNumber.substring(8,personalNumber.length());
        welcome.setText("Mina sidor för: "+ birthDate + "-" +lastNumbers);
    }
    private void setListiners() {
        findViewById(R.id.btn_account).setOnClickListener(this);
        findViewById(R.id.btn_addReceipt).setOnClickListener(this);
        findViewById(R.id.btn_myReceipt).setOnClickListener(this);
        findViewById(R.id.btn_logOut).setOnClickListener(this);
        welcome = findViewById(R.id.txt_welcome);
    }
    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        switch (btn.getId()) {
            case R.id.btn_account: {
                Intent intent = new Intent(getApplicationContext(), MyAccountActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_addReceipt: {
                Intent intent = new Intent(getApplicationContext(), AddReceiptActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_myReceipt: {
                Intent intent = new Intent(getApplicationContext(), MyReceiptActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_logOut: {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
