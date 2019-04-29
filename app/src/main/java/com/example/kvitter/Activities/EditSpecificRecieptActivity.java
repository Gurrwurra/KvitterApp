package com.example.kvitter.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.CurrentReceipt;
import com.example.kvitter.Util.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        save = findViewById(R.id.btn_save_changes);
        delete = findViewById(R.id.btn_delete_re);
        change_pic = findViewById(R.id.btn_change_pic);

        name.setText(receipt.getName());
        amount.setText(receipt.getAmount());
        supplier.setText(receipt.getSupplier());
        comment.setText(receipt.getComment());
        populateSpinner(this);

    }
    private void populateSpinner (Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> folderNames = new ArrayList<>();
        db.collection("data").document(CurrentId.getUserId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> map = document.getData();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                int type = Integer.parseInt(document.get(key + ".type").toString());
                                if (type == 0) {
                                    String folderName = document.get(key + ".folderName").toString();
                                    folderNames.add(folderName);
                                }
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item, folderNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        folder.setAdapter(adapter);
                    }
                });
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
                String folderName_rec = String.valueOf(folder.getSelectedItem());
                updatedReciept.setName(name_rec);
                updatedReciept.setAmount(amount_rec);
                updatedReciept.setSupplier(supplier_rec);
                updatedReciept.setComment(comment_rec);
                updatedReciept.setPhotoRef(receipt.getPhotoRef());
                updatedReciept.setFolderName(folderName_rec);
                updatedReciept.setType(1);
                DataEngine engine = new DataEngine();
                engine.updateReciept(receipt.getName(),updatedReciept);

                Intent intent = new Intent(EditSpecificRecieptActivity.this, MyReceiptActivity.class);
                startActivity(intent);
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
