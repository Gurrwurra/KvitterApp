package com.example.kvitter.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.content.FileProvider;
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
    private static final int PICK_IMAGE = 100;
    private static final int PICK_PDF = 1000;

    int validate = 0;

    static final String CURRENT_PHOTO = "currentPhoto";

    Uri photoURI;
    File photoFile;
    Uri uri;

    Button take_picture, get_picture, get_pdf, decline;

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
        take_picture = findViewById(R.id.btn_accept_change);
        get_picture = findViewById(R.id.btn_accept_get_pic);
        get_pdf = findViewById(R.id.btn_accept_pdf);
        decline = findViewById(R.id.btn_decline_change);
    }

    /**
     * Sets the different buttons to the correct functions
     */
    private void setListiners(){
        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        get_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        get_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFiles();
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
        DatabaseLogic logic = new DatabaseLogic();
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            uri = Uri.fromFile(photoFile);
            validate = 0;
            logic.newSequenceNumberForNewPhoto(this, uri, receipt, validate, null);
        } else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            uri = data.getData();
            File file = new File(ImageHelper.getRealPathFromURI(getApplicationContext(), uri));
            Uri photoUri = Uri.fromFile(file);
            validate = 0;
            logic.newSequenceNumberForNewPhoto(this, photoUri, receipt, validate, null);
        } else if(requestCode == PICK_PDF && resultCode == RESULT_OK){
            uri = data.getData();
            validate = 1;
            String fileName = getFileName(uri);
            logic.newSequenceNumberForNewPhoto(this, uri, receipt, validate, fileName);

        }

      /* todo: if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                setPic();
                fileUri = null;
                file.setVisibility(View.INVISIBLE);
                validate = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK ) {
            photoURI = data.getData();
            photoFile = new File(ImageHelper.getRealPathFromURI(getApplicationContext(), photoURI));
            try {
                setPic();
                fileUri = null;
                file.setVisibility(View.INVISIBLE);
                validate = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(requestCode == PICK_PDF && resultCode == RESULT_OK){
            recieptPic.setVisibility(View.GONE);
            photoURI = null;
            fileUri = data.getData();
            fileName = getFileName(fileUri);
            file.setText(fileName);
            validate = 1;
        }*/
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

    /**
     * Opens gallery of photos on the users phone
     */
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    /**
     * Open the users documents folder on their phone and allows them to choose documents with the format PDF.
     */
    private void openFiles(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF);
    }

    /**
     * Returns the name of the file the user has choosen
     * @param uri from the file the user has choosen.
     * @return
     */
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}
