package com.example.kvitter.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.kvitter.R;

public class ChoiceActivity extends AppCompatActivity {

    private Button home;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        bindViews();
        addListiners();
    }

    private void bindViews() {
        home = findViewById(R.id.btn_home);
        add = findViewById(R.id.btn_another_reciept);
    }

    private void addListiners() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceActivity.this, AddReceiptActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });
    }
}
