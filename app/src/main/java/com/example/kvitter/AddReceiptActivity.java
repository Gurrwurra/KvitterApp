package com.example.kvitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class AddReceiptActivity extends AppCompatActivity {


    private ImageButton recieptPic;
    private Button fileUpload;
    private Button save;
    private EditText title;
    private EditText amount;
    private EditText supplier;
    private EditText comment;
    private TextView file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);

        bindViews();
        setListiners();
    }

    private void bindViews(){
        recieptPic = findViewById(R.id.receiptImage);
        fileUpload = findViewById(R.id.btn_upload_file);
        save = findViewById(R.id.btn_save);
        title = findViewById(R.id.etxt_name);
        amount = findViewById(R.id.etxt_total_amount);
        supplier = findViewById(R.id.etxt_supplier);
        comment = findViewById(R.id.etxt_note_reciept);
        file = findViewById(R.id.txt_file_path);
    }

    private void setListiners(){
        recieptPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recieptPic.setImageResource(R.drawable.english);
            }
        });

        fileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file.setText("Test");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddReceiptActivity.this, Validate_reciept.class);
                intent.putExtra("name", title.getText().toString());
                intent.putExtra("amount", amount.getText().toString());
                intent.putExtra("supplier", supplier.getText().toString());
                intent.putExtra("comment", comment.getText().toString());

                if(file.getText().toString() != ""){
                    intent.putExtra("file", file.getText().toString());
                }
                startActivity(intent);
            }
        });


    }
}
