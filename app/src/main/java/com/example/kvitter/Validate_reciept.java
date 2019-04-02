package com.example.kvitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kvitter.Activities.AddReceiptActivity;

public class Validate_reciept extends AppCompatActivity {

    private TextView title;
    private TextView amount;
    private TextView supplier;
    private TextView comment;
    private TextView file;

    private Button accept;
    private Button deny;

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
    }

    private void validateValues() {

        Bundle Extra = getIntent().getExtras();
        String name = Extra.getString("name");
        String amount_of = Extra.getString("amount");
        String supp = Extra.getString("supplier");
        String comm = Extra.getString("comment");
        String file_of = Extra.getString("file");

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
