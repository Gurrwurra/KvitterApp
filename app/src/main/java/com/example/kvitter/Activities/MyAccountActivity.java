package com.example.kvitter.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.kvitter.R;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        setListiners();
    }

    private void setListiners() {
        findViewById(R.id.btn_editAccount).setOnClickListener(this);
        findViewById(R.id.btn_settings).setOnClickListener(this);
    }
    public void onClick(View v) {

        Button btn = (Button) v;

        switch (btn.getId()) {
            case R.id.btn_editAccount: {
                Intent intent = new Intent(getApplicationContext(), EditAccountActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.btn_settings: {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

                break;
            }
        }
    }
}

