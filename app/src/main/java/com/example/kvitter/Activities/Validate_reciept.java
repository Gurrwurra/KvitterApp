package com.example.kvitter.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.ImageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Validate_reciept extends AppCompatActivity {

    private TextView title;
    private TextView amount;
    private TextView supplier;
    private TextView comment;
    private TextView file;
    private TextView folder;
    private ImageView recieptImage;

    private Button accept;
    private Button deny;


    private File fileOfPhoto;
    Uri uri;
    int validatePhotoOrigin;
    Uri fileUri;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_reciept);

        bindViews();
        try {
            validateValues();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

                String[] receiptInfo = new String[5];

                receiptInfo[0] = title.getText().toString();
                receiptInfo[1] = supplier.getText().toString();
                receiptInfo[2] = amount.getText().toString();
                receiptInfo[3] = comment.getText().toString();
                receiptInfo[4] =folder.getText().toString();
                DatabaseLogic logic = new DatabaseLogic();
                if(validatePhotoOrigin == 0) {
                    logic.newSequenceNumber(Validate_reciept.this, uri, receiptInfo, validatePhotoOrigin, null);
                }else{
                    logic.newSequenceNumber(Validate_reciept.this, fileUri, receiptInfo, validatePhotoOrigin, fileName);
                }
            }
        });
    }

    private void validateValues() throws IOException {
        Bundle Extra = getIntent().getExtras();
        String name = Extra.getString("name");
        String amount_of = Extra.getString("amount");
        String supp = Extra.getString("supplier");
        String comm = Extra.getString("comment");
        String file_of = Extra.getString("file");
        String folderName = Extra.getString("folderName");
        String fileOfPh = Extra.getString("fileOfPhoto");
        validatePhotoOrigin = Extra.getInt("validate");



        if (file_of != null && validatePhotoOrigin == 0) {
            file.setText(file_of);
        }

        title.setText(name);
        amount.setText(amount_of);
        supplier.setText(supp);
        comment.setText(comm);
        folder.setText(folderName);

      if(fileOfPh != null ) {
            fileOfPhoto = new File(fileOfPh);
            uri = Uri.fromFile(fileOfPhoto);
            Bitmap imageBitmap = ImageHelper.getCorrectlyOrientedImage(getApplicationContext(), uri);
            recieptImage.setImageBitmap(imageBitmap);
        }else
        {
            fileUri = Uri.parse(Extra.getString("fileUri"));
            fileName = Extra.getString("fileName");
            file.setText(fileName);
            recieptImage.setVisibility(View.GONE);
        }

    }

    private void bindViews() {
        title = findViewById(R.id.txt_name_validate);
        amount = findViewById(R.id.txt_amount_validate);
        supplier = findViewById(R.id.txt_supplier_validate);
        comment = findViewById(R.id.txt_comment_validate);
        file = findViewById(R.id.txt_file);
        folder = findViewById(R.id.txt_folderNameValidate);
        recieptImage = findViewById(R.id.img_validate_img);

        accept = findViewById(R.id.btn_accept_validate);
        deny = findViewById(R.id.btn_deny_validate);
    }

}
