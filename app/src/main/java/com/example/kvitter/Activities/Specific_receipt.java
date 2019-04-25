package com.example.kvitter.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentReceipt;
import com.example.kvitter.Util.GlideApp;
import com.example.kvitter.Util.MyAppGlideModule;
import com.example.kvitter.Util.UserData;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Specific_receipt extends AppCompatActivity {
    private TextView name, amount, supplier, comment,photoRef, folderName;
    private ImageView receipt_image;
    private Button edit;
    private Button share;
    UserData receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_reciept);
        bindViews();
        addListiners();
        receipt = CurrentReceipt.getReceipt();
        name.setText(receipt.getName());
        amount.setText(receipt.getAmount());
        supplier.setText(receipt.getSupplier());
        comment.setText(receipt.getComment());
        folderName.setText(receipt.getFolderName());

        StorageReference mStorage = FirebaseStorage.getInstance().getReference(receipt.getPhotoRef());

        GlideApp.with(this /* context */)
                .load(mStorage)
                .into(receipt_image);
    }

    private void bindViews() {
        edit = findViewById(R.id.btn_edit_specific_reciept);
        share = findViewById(R.id.btn_share);
        name = findViewById(R.id.txt_specific_name);
        amount = findViewById(R.id.txt_specific_amount);
        supplier = findViewById(R.id.txt_specific_supplier);
        comment = findViewById(R.id.txt_specific_comment);
        folderName = findViewById(R.id.txt_specific_folder);
        receipt_image = findViewById(R.id.specific_img);
    }

    private void addListiners() {

        //TODO: dela kvitto

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(Specific_receipt.this, EditSpecificRecieptActivity.class);
                startActivity(intent);*/
                DatabaseLogic logic = new DatabaseLogic();
                logic.updateReceipt(receipt);
            }
        });

    }
}
