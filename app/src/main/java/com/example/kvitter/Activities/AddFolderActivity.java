package com.example.kvitter.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.R;

public class AddFolderActivity extends AppCompatActivity {

    private EditText folderName;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_folder);

        bindViews();
        setListiners();
    }

    private void setListiners() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseLogic logic = new DatabaseLogic();
                logic.addNewfolder(folderName.getText().toString(), AddFolderActivity.this);
            }
        });
    }

    private void bindViews(){
        folderName = findViewById(R.id.etxt_folder_name);
        save = findViewById(R.id.btn_save_folder);
    }
}
