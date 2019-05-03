package com.example.kvitter.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kvitter.R;

public class EditAccountActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        runNavigation(R.id.activity_editAccount);
    }
}
