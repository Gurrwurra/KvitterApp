package com.example.kvitter.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kvitter.R;

public class EditSpecificRecieptActivity extends AppCompatActivity {

    private EditText name;
    private EditText amount;
    private EditText supplier;
    private Spinner folder;
    private EditText comment;

    private Button delete;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_specific_recipet);

        bindViews();
        addListiners();
    }

    private void bindViews() {
        name = findViewById(R.id.txt_specific_name_edit);
        amount = findViewById(R.id.txt_specific_amount_edit);
        supplier = findViewById(R.id.txt_specific_supplier_edit);
        folder = findViewById(R.id.spi_folder_edit);
        comment = findViewById(R.id.txt_specific_comment_edit);

        String[] x = new String[10];

        x[0] = "test";
        x[1] = "Båt";
        x[2] = "Hus";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, x);

        folder.setAdapter(adapter);


        save = findViewById(R.id.btn_save_changes);
        delete = findViewById(R.id.btn_delete_re);
    }

    private void addListiners() {

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSpecificRecieptActivity.this, AcceptDeleteActivity.class);
                startActivity(intent);
            }
        });

        //TODO: radera och spara ändring



    }


    //TODO: ändra bild

}
