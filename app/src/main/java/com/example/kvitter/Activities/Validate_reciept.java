package com.example.kvitter.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class Validate_reciept extends AppCompatActivity {

    private TextView title;
    private TextView amount;
    private TextView supplier;
    private TextView comment;
    private TextView file;

    private Button accept;
    private Button deny;


    private Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_reciept);

        bindViews();
        validateValues();

        setListiners();
    }

    private void setListiners() {
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Validate_reciept.this, AddReceiptActivity.class);
                startActivity(intent);
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            DatabaseLogic logic = new DatabaseLogic();
            logic.newSequenceNumber(Validate_reciept.this, filePath);

            }
        });
    }

    private void validateValues() {

        Bundle Extra = getIntent().getExtras();
        String name = Extra.getString("name");
        String amount_of = Extra.getString("amount");
        String supp = Extra.getString("supplier");
        String comm = Extra.getString("comment");
        String file_of = Extra.getString("file");

        filePath = Uri.parse(Extra.getString("uri"));

        if(file_of != null){
            file.setText(file_of);
        }

        title.setText(name);
        amount.setText(amount_of);
        supplier.setText(supp);
        comment.setText(comm);

    }

    private void bindViews() {
        title = findViewById(R.id.txt_name_validate);
        amount = findViewById(R.id.txt_amount_validate);
        supplier = findViewById(R.id.txt_supplier_validate);
        comment = findViewById(R.id.txt_comment_validate);
        file = findViewById(R.id.txt_file);

        accept = findViewById(R.id.btn_accept_validate);
        deny = findViewById(R.id.btn_deny_validate);
    }


}
