package com.example.kvitter.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvitter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Validate_reciept extends AppCompatActivity {

    private TextView title;
    private TextView amount;
    private TextView supplier;
    private TextView comment;
    private TextView file;

    private ImageView recieptImage;

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

               saveInformation();
               /* DatabaseLogic logic = new DatabaseLogic();
                logic.newSequenceNumber(Validate_reciept.this, filePath); */

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
        String photoPath = Extra.getString("photoPath");

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);

        filePath = Uri.parse(Extra.getString("uri"));

        if (file_of != null) {
            file.setText(file_of);
        }

        title.setText(name);
        amount.setText(amount_of);
        supplier.setText(supp);
        comment.setText(comm);

        recieptImage.setImageBitmap(bitmap);

    }

    private void bindViews() {
        title = findViewById(R.id.txt_name_validate);
        amount = findViewById(R.id.txt_amount_validate);
        supplier = findViewById(R.id.txt_supplier_validate);
        comment = findViewById(R.id.txt_comment_validate);
        file = findViewById(R.id.txt_file);

        recieptImage = findViewById(R.id.img_validate_img);

        accept = findViewById(R.id.btn_accept_validate);
        deny = findViewById(R.id.btn_deny_validate);
    }


    private void saveInformation() {


        FirebaseFirestore db;

        db = FirebaseFirestore.getInstance();

        Map<String, Object> recieptMap = new HashMap<>();
        recieptMap.put("name", "Vi hahaha ett kvitto");
        recieptMap.put("supplier", "lalala");
        recieptMap.put("amount", 3);
        recieptMap.put("comment", "bajs");

        ArrayList<Map> recieptInfor = new ArrayList<>();

        recieptInfor.add(recieptMap);

        Map<String, Object> folderInformation = new HashMap<>();
        folderInformation.put("receipts", recieptInfor);

        ArrayList<Map> folderInfo = new ArrayList<>();
        folderInfo.add(folderInformation);

        Map<String, Object> folders = new HashMap<>();
        folders.put("Hobby" ,folderInfo);

        ArrayList<Map> foldersArray = new ArrayList<>();
        foldersArray.add(folders);

        Map<String, Object> recieptData = new HashMap<>();
        recieptData.put("folder", foldersArray );

       db.collection("user_data").document("kLS4mOm1zc2GWQFBxxog.folder.Hobby.receipts")
                .set(recieptData, SetOptions.mergeFields("folder"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast toast = Toast.makeText(Validate_reciept.this, "funkade!!!!!!!!!!!!!!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast toast = Toast.makeText(Validate_reciept.this, "ABOOOOOO", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

        //db.collection("user_data").document("kLS4mOm1zc2GWQFBxxog.folder.Hobby.receipts").update(recieptData);

        //db.collection("user_data").document("kLS4mOm1zc2GWQFBxxog").collection("folder").add(recieptData);

        //db.collection("user_data").document("kLS4mOm1zc2GWQFBxxog").set(recieptData, SetOptions.merge());

    }



}
