package com.example.kvitter.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.R;
import com.example.kvitter.Util.ImageHelper;

import java.io.File;
import java.io.IOException;

public class Validate_reciept extends NavigationActivity {

    private EditText title, amount,supplier, comment;
    private TextView file, folder;
    private ImageView recieptImage;
    private Button accept,deny;

    private File fileOfPhoto;
    Uri uri, fileUri;
    int validatePhotoOrigin;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_reciept);
        runNavigation(R.id.activity_validate);
        bindViews();
        try {
            validateValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setListiners();
    }

    /**
     * Sets the different buttons to the correct functions
     */
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

                //depending if its photo or file it will start different methods for uploading the document
                if(validatePhotoOrigin == 0) {
                    logic.newSequenceNumber(Validate_reciept.this, uri, receiptInfo, validatePhotoOrigin, null);
                }else{
                    logic.newSequenceNumber(Validate_reciept.this, fileUri, receiptInfo, validatePhotoOrigin, fileName);
                }
            }
        });
    }

    /**
     * Binds elements to the right view.
     * Sets the values from AddReceiptActivity from the keys in method setListeners
     */
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

    /**
     * Binds elements to the right view
     */
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
