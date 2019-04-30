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

    /**
     * Binds elements to the right view
     */
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

    /**
     * Populates the dropdownlist with the different folders the user has created.
     *
     * @param context from the activity
     */
    private void populateSpinner(Context context) {
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, folderNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        folder.setAdapter(adapter);
                    }
                });
    }

    /**
     * Sets the different buttons to the correct functions
     */
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
                setValuesUpdate();
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

    /**
     * Sets the values the user has written to update the object. Starts updateReciept in DataEngine-class
     */
    private void setValuesUpdate() {
        UserData updatedReceipt = new UserData();
        String name_rec = name.getText().toString();
        String amount_rec = amount.getText().toString();
        String supplier_rec = supplier.getText().toString();
        String comment_rec = comment.getText().toString();
        String folderName_rec = String.valueOf(folder.getSelectedItem());
        updatedReceipt.setName(name_rec);
        updatedReceipt.setAmount(amount_rec);
        updatedReceipt.setSupplier(supplier_rec);
        updatedReceipt.setComment(comment_rec);
        updatedReceipt.setPhotoRef(receipt.getPhotoRef());
        updatedReceipt.setFolderName(folderName_rec);
        updatedReceipt.setType(1);
        DataEngine engine = new DataEngine();
        engine.updateReciept(receipt.getName(), updatedReceipt);
    }
}
