package com.example.kvitter.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvitter.DataEngine;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.ImageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AddReceiptActivity extends NavigationActivity implements View.OnClickListener {
    private Spinner folder;
    private ImageView recieptPic;



    private ConstraintLayout add_folder_layout;
    private EditText title, amount, supplier, comment, folderName;
    private TextView file, date;
    private static final int PICK_IMAGE = 100;
    private static final int PICK_PDF = 1000;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    private String currentPhoto;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String CURRENT_PHOTO = "currentPhoto";
    int validate = 0;
    Uri photoURI = null;
    File photoFile = null;
    Uri fileUri = null;
    String fileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);
        populateSpinner(this);
        runNavigation(R.id.activity_addReceipt);
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                bindViews();
                setListeners();
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                showDate(year, month+1, day);
            } else {
                requestPermission(); // Code for permission
            }
        }
    }
    /**
     * Binds elements to the right view
     */
    private void bindViews(){
        folder = findViewById(R.id.spi_addToFolder);
        recieptPic = findViewById(R.id.receiptImage);
        title = findViewById(R.id.txt_name_validate);
        amount = findViewById(R.id.txt_amount_validate);
        supplier = findViewById(R.id.txt_supplier_validate);
        comment = findViewById(R.id.txt_comment_validate);
        date = findViewById(R.id.txt_date);
        file = findViewById(R.id.txt_file_path);
        folderName = findViewById(R.id.etxt_add_folder_recact);
        add_folder_layout = findViewById(R.id.add_folder_layout);
    }
    private void setListeners() {
        findViewById(R.id.btn_save_folder_recact).setOnClickListener(this);
        findViewById(R.id.btn_take_pic).setOnClickListener(this);
        findViewById(R.id.btn_upload_image).setOnClickListener(this);
        findViewById(R.id.btn_pdf_upload).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_add_folder_recact).setOnClickListener(this);
        findViewById(R.id.btn_cancel_add_folder).setOnClickListener(this);
    }
    private void showDate(int year, int month, int day) {
        date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
    /**
     * Sets the different buttons to the correct functions
     */
    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        switch (btn.getId()) {
            case R.id.btn_save_folder_recact: {
                DataEngine engine = new DataEngine();
                engine.createFolder(folderName.getText().toString());
                populateSpinner(AddReceiptActivity.this);
                add_folder_layout.setVisibility(View.GONE);
                break;
            }
            case R.id.btn_take_pic: {
                dispatchTakePictureIntent();
                break;
            }
            case R.id.btn_upload_image: {
                openGallery();
                break;
            }
            case R.id.btn_pdf_upload: {
                openFiles();
                break;
            }
            case R.id.btn_save: {
                DataEngine engine = new DataEngine();
                add_folder_layout.setVisibility(View.GONE);
                if (TextUtils.isEmpty(title.getText().toString())) {
                    Toast.makeText(AddReceiptActivity.this, "Du måste fylla i ett namn och ladda upp bild eller fil av fotot", Toast.LENGTH_LONG).show();
                    break;
                }
                else if(fileUri == null && photoURI != null){
                    startValidate();
                }
                else if(photoURI == null && fileUri != null){
                    startValidate();
                }
                else{
                    Toast.makeText(AddReceiptActivity.this, "Du måste fylla i ett namn och ladda upp bild eller fil av fotot", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.btn_add_folder_recact: {
                add_folder_layout.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.btn_cancel_add_folder: {
                add_folder_layout.setVisibility(View.GONE);
                break;
            }
        }
    }

    public void startValidate(){

        DataEngine engine = new DataEngine();


        String folderName = String.valueOf(folder.getSelectedItem());
        Intent intent = new Intent(this, Validate_reciept.class);

            intent.putExtra("name", title.getText().toString());
            intent.putExtra("amount", amount.getText().toString());
            intent.putExtra("supplier", supplier.getText().toString());
            intent.putExtra("comment", comment.getText().toString());
            intent.putExtra("photoPath", currentPhoto);
            intent.putExtra("validate", validate);
            intent.putExtra("folderName", folderName);
            intent.putExtra("date", date.getText().toString());
            if (photoURI != null) {
                intent.putExtra("uri", photoURI.toString());
                intent.putExtra("fileOfPhoto", photoFile.toString());
            } else if (fileUri != null) {
                intent.putExtra("fileUri", fileUri.toString());
                intent.putExtra("fileName", fileName);
            }
        engine.checkReceiptName(title.getText().toString(), this, intent);
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    /**
     * Populates the dropdownlist with the different folders the user has created.
     * @param context from the activity
     */
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
                        if (folderNames.isEmpty()) {
                            folderNames.add("Övriga kvitton");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item, folderNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        folder.setAdapter(adapter);
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
     * When photo/file is taken on result its sets Uri for the photo/file depending if the user has taken, chosen a photo
     * or choosen a PDF.
     * @param requestCode for the different options, if the user has choosen a file, photo or taken one.
     * @param resultCode if the procedure went right or wrong
     * @param data from the result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
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
                }
    }

    /**
     * Sets the the picture the user choosen or taken to a thumbnail. Starts getCorrectlyOrientedImage in class ImageHelper
     * to get correct position of the photo.
     * @throws IOException
     */
    private void setPic() throws IOException {
        recieptPic.setVisibility(View.VISIBLE);
        Uri uri = Uri.fromFile(photoFile);
        Bitmap imageBitmap = ImageHelper.getCorrectlyOrientedImage(getApplicationContext(), uri);
        recieptPic.setImageBitmap(imageBitmap);
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
                try {
                    setPic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
     * Checks if the user has granted the application to access their camera, photo gallery or documents folder.
     * @return
     */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(AddReceiptActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Ask the user permission to access their camera, photo gallery and documents folder.
     */
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(AddReceiptActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(AddReceiptActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(AddReceiptActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Writes if the user has granted it or not.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
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
