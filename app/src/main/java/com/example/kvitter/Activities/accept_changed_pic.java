package com.example.kvitter.Activities;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentReceipt;
import com.example.kvitter.Util.ImageHelper;

import java.io.File;
import java.io.IOException;
import com.example.kvitter.Util.UserData;

public class accept_changed_pic extends NavigationActivity {
    private String currentPhoto;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String CURRENT_PHOTO = "currentPhoto";

    Uri photoURI;
    File photoFile;

    Button accept;
    Button decline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_changed_pic);
        bindViews();
        setListiners();
        runNavigation(R.id.activity_accChangedPic);
    }

    /**
     * Binds elements to the right view
     */
    private void bindViews(){
        accept = findViewById(R.id.btn_accept_change);
        decline = findViewById(R.id.btn_decline_change);
    }

    /**
     * Sets the different buttons to the correct functions
     */
    private void setListiners(){
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    /**
     * Starts the camera and starts method thats creates temporary file.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;
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

    /**
     * When photo/file is taken on result its sets Uri.
     * @param requestCode for the different options, if the user has choosen a file, photo or taken one.
     * @param resultCode if the procedure went right or wrong
     * @param data from the result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UserData receipt = CurrentReceipt.getReceipt();
        Uri uri = Uri.fromFile(photoFile);
        DatabaseLogic logic = new DatabaseLogic();
        logic.newSequenceNumberForNewPhoto(this ,uri, receipt);
        //todo: ladda upp bild samt ta bort gamla
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(CURRENT_PHOTO, currentPhoto);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPhoto = savedInstanceState.getString(CURRENT_PHOTO);

    }
}
