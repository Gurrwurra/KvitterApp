package com.example.kvitter.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentUser;

public class StartActivity extends NavigationActivity implements View.OnClickListener {
    private TextView welcome;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setListiners();
        setWelcomeText();
        runNavigation(R.id.activity_start);
    }

    private void setWelcomeText() {
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
