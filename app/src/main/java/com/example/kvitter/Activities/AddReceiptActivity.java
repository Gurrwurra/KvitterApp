package com.example.kvitter.Activities;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kvitter.R;
import com.example.kvitter.Util.ImageHelper;

import java.io.File;
import java.io.IOException;

public class AddReceiptActivity extends AppCompatActivity {


    private ImageButton recieptPic;
    private Button fileUpload;
    private Button save;
    private EditText title;
    private EditText amount;
    private EditText supplier;
    private EditText comment;
    private TextView file;

    private String currentPhoto;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String CURRENT_PHOTO = "currentPhoto";
    Uri photoURI;

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
               takePhoto();
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


                if(photoURI != null) {
                    intent.putExtra("uri", photoURI.toString() );
                }

                if(file.getText().toString() != ""){
                    intent.putExtra("file", file.getText().toString());
                }
                startActivity(intent);
            }
        });
    }

    private void takePhoto() {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = ImageHelper.createImageFile(getApplicationContext());
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                currentPhoto = photoFile.getAbsolutePath();
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.KvitterApp",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {

        recieptPic.setImageBitmap(ImageHelper.scaleImage(recieptPic.getWidth(), recieptPic.getHeight(), currentPhoto));


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(CURRENT_PHOTO, currentPhoto);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPhoto = savedInstanceState.getString(CURRENT_PHOTO);
        recieptPic.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recieptPic.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setPic();
            }
        });
    }
}
