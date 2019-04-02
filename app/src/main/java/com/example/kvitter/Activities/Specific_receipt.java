package com.example.kvitter.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.kvitter.R;

public class Specific_receipt extends AppCompatActivity {

    private Button edit;
    private Button share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_reciept);

        bindViews();
        addListiners();
    }

    private void bindViews() {
        edit = findViewById(R.id.btn_edit_specific_reciept);
        share = findViewById(R.id.btn_share);
    }

    private void addListiners() {

        //TODO: dela kvitto

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Specific_receipt.this, EditSpecificRecieptActivity.class);
                startActivity(intent);
            }
        });

    }
}
