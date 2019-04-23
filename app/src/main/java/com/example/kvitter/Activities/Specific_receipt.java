package com.example.kvitter.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentReceipt;
import com.example.kvitter.Util.UserData;

public class Specific_receipt extends AppCompatActivity {
    private TextView name, amount, supplier, comment,photoRef, folderName;
    private Button edit;
    private Button share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_reciept);
        bindViews();
        addListiners();
        UserData receipt = CurrentReceipt.getReceipt();
        name.setText(receipt.getName());
        amount.setText(receipt.getAmount());
        supplier.setText(receipt.getSupplier());
        comment.setText(receipt.getComment());
        folderName.setText(receipt.getFolderName());
    }

    private void bindViews() {
        edit = findViewById(R.id.btn_edit_specific_reciept);
        share = findViewById(R.id.btn_share);
        name = findViewById(R.id.txt_specific_name);
        amount = findViewById(R.id.txt_specific_amount);
        supplier = findViewById(R.id.txt_specific_supplier);
        comment = findViewById(R.id.txt_specific_comment);
        folderName = findViewById(R.id.txt_specific_folder);
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
