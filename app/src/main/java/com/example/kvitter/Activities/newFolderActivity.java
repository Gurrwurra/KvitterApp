package com.example.kvitter.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.kvitter.DataEngine;
import com.example.kvitter.R;

public class newFolderActivity extends NavigationActivity implements View.OnClickListener {
    private EditText folder;
    private Button saveFolder, regretFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_folder);
        bindViews();
        runNavigation(R.id.activity_newFolder);
    }

    private void bindViews() {
        folder = findViewById(R.id.new_folder_name);
        saveFolder = findViewById(R.id.btn_save_new_folder);
        regretFolder = findViewById(R.id.btn_regret_new_folder);
        saveFolder.setOnClickListener(this);
        regretFolder.setOnClickListener(this);
    }

    /**
     * case btn_save_new_folder - method createFolder in DataEngine will run and store folder to database for that user and return view for MyReceiptActivity
     * btn_regret_new_folder - method will return view MyReceiptActivity
     * @param v
     */
    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        switch (btn.getId()) {
            case R.id.btn_save_new_folder: {
                DataEngine engine = new DataEngine();
                engine.createFolder(folder.getText().toString());
                Intent intent = new Intent(getApplicationContext(), MyReceiptActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_regret_new_folder: {
                Intent intent = new Intent(getApplicationContext(), MyReceiptActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
