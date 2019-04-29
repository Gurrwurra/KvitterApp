package com.example.kvitter.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kvitter.DataEngine;
import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentReceipt;
import com.example.kvitter.Util.UserData;

public class EditSpecificRecieptActivity extends AppCompatActivity {

    private EditText name;
    private EditText amount;
    private EditText supplier;
    private Spinner folder;
    private EditText comment;

    private Button delete;
    private Button save;
    private Button change_pic;

    UserData receipt = CurrentReceipt.getReceipt();
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

        name.setText(receipt.getName());
        amount.setText(receipt.getAmount());
        supplier.setText(receipt.getSupplier());
        comment.setText(receipt.getComment());

        String[] x = new String[3];

        x[0] = "test";
        x[1] = "Båt";
        x[2] = "Hus";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, x);

        folder.setAdapter(adapter);


        save = findViewById(R.id.btn_save_changes);
        delete = findViewById(R.id.btn_delete_re);
        change_pic = findViewById(R.id.btn_change_pic);
    }

    private void addListiners() {

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSpecificRecieptActivity.this, AcceptDeleteActivity.class);
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserData updatedReciept = new UserData();
                String name_rec = name.getText().toString();
                String amount_rec = amount.getText().toString();
                String supplier_rec = supplier.getText().toString();
                String comment_rec = comment.getText().toString();

                updatedReciept.setName(name_rec);
                updatedReciept.setAmount(amount_rec);
                updatedReciept.setSupplier(supplier_rec);
                updatedReciept.setComment(comment_rec);
                updatedReciept.setPhotoRef(receipt.getPhotoRef());
                updatedReciept.setFolderName(receipt.getFolderName());

                DataEngine engine = new DataEngine();
                engine.updateReciept(receipt.getName(),updatedReciept);
            //    DatabaseLogic logic = new DatabaseLogic();
             //   logic.updateReceipt(receipt, name_rec, amount_rec, supplier_rec, comment_rec, null);
            }
        });

        change_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSpecificRecieptActivity.this, accept_changed_pic.class);
                startActivity(intent);
            }
        });

    }

    //TODO: ändra bild

}
