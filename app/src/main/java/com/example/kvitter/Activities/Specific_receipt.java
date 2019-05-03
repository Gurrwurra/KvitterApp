package com.example.kvitter.Activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentReceipt;
import com.example.kvitter.Util.GlideApp;
import com.example.kvitter.Util.UserData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class Specific_receipt extends NavigationActivity {
    private TextView name, amount, supplier, comment, folderName, file;
    private ImageView receipt_image;
    private Button edit;
    private Button share;
    private UserData receipt;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_reciept);
        bindViews();
        runNavigation(R.id.activity_specificReceipt);
        setValues();
        setListiners();
    }

    /**
     * Binds elements to the right view
     */
    private void bindViews() {
        edit = findViewById(R.id.btn_edit_specific_reciept);
        share = findViewById(R.id.btn_share);
        name = findViewById(R.id.txt_specific_name);
        amount = findViewById(R.id.txt_specific_amount);
        supplier = findViewById(R.id.txt_specific_supplier);
        comment = findViewById(R.id.txt_specific_comment);
        folderName = findViewById(R.id.txt_specific_folder);
        receipt_image = findViewById(R.id.specific_img);
        file = findViewById(R.id.txt_file_from);
    }


    /**
     * Sets the different buttons to the correct functions
     */
    private void setListiners() {
        //TODO: dela kvitto

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Specific_receipt.this, EditSpecificRecieptActivity.class);
                startActivity(intent);
            }
        });

        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference ref = FirebaseStorage.getInstance().getReference();
                StorageReference storageRef = ref.child(receipt.getPhotoRef());

                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        downloadFile(Specific_receipt.this, fileName, ".pdf", DIRECTORY_DOWNLOADS, url);
                        Toast.makeText(Specific_receipt.this, "Du har laddat hem filen: " + fileName, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    /**
     * Set the values of the specific receipt. Checks if the receipt belongs to a image or a PDF.
     */
    private void setValues(){
        receipt = CurrentReceipt.getReceipt();
        name.setText(receipt.getName());
        amount.setText(receipt.getAmount());
        supplier.setText(receipt.getSupplier());
        comment.setText(receipt.getComment());
        folderName.setText(receipt.getFolderName());

        String val = receipt.getPhotoRef();

        String[] splitRef = val.split("\\.");
        String last = splitRef[splitRef.length-1];

        String first = splitRef[0];

        if(last.contains("pdf")) {
            String[] fileNameSplit = first.split("\\/", 2);
            fileName = fileNameSplit[1];
            receipt_image.setVisibility(View.INVISIBLE);
            file.setVisibility(View.VISIBLE);
            file.setText(fileName);
        } else
        {
            file.setVisibility(View.GONE);
            StorageReference mStorage = FirebaseStorage.getInstance().getReference(receipt.getPhotoRef());

            GlideApp.with(this /* context */)
                    .load(mStorage)
                    .into(receipt_image);
        }
    }

    /**
     * Downloads the PDF that belongs to the specific receipt.
     * @param context of the activity
     * @param fileName name of the file to download
     * @param fileExtension file format
     * @param destinationDirectory to the directory
     * @param url / uri of the file
     */
    private void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url){

        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        dm.enqueue(request);
    }
}
