package com.example.kvitter.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvitter.Adapters.FolderAdapter;
import com.example.kvitter.Adapters.ReceiptAdapter;
import com.example.kvitter.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyReceiptActivity extends AppCompatActivity {
    private TextView folder, note;
    private RecyclerView folderView;
    private RecyclerView.Adapter folderAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] testData = new String[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_receipt);
        folderView = (RecyclerView)findViewById(R.id.folder_list);

        testData[0] = "Hobby";
        testData[1] = "Båt";
        testData[2] = "Hus";

        folderView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        folderView.setLayoutManager(layoutManager);
        folderAdapter = new FolderAdapter(this,testData);
        folderView.setAdapter(folderAdapter);


    }
}
