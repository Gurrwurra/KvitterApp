package com.example.kvitter.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentId;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MyReceiptActivity extends AppCompatActivity {
    private TextView folder, note;
    private RecyclerView folderView;
    private RecyclerView.Adapter folderAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;
    private String[] testData = new String[3];
    List<String> folders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_receipt);
        bindViews();
        folderView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        folderView.setLayoutManager(layoutManager);
        populateFolders(this);
        addListiners();
      }
    private void addListiners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyReceiptActivity.this, AddFolderActivity.class);
                startActivity(intent);
            }
        });
    }
    private void bindViews() {
        folderView = (RecyclerView)findViewById(R.id.folder_list);
        fab = findViewById(R.id.FAB_folder);
    }
    private void populatesFolders() {
        String documentName = CurrentId.getUserId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("user_data").document(documentName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // {receipts=[{amount=500, supplier=Willys, name=Test2, comment=test2, photoRef=reciept/4c789837-26ee-4582-906e-1a120b0544e1-35}]}
                    //         System.out.println(document.getData().toString());
           //         testData[0] = document.get("folder.Sommarstugan").toString();
                    System.out.println();
             testData[0] = "Test1" + document.get("folder");
             testData[1] = "Test2";
             testData[2] = "Test3";
                    folderView.setAdapter(folderAdapter);
             //    stringValues(document.getData().toString());
                } else {
                    System.out.println("Cached get failed:" + task.getException()); }
            }
        });
    }

    private void populateFolders(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("user_data").document("HINCqfhWGB9XwGtGBtYl");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String data = document.get("folder").toString();
                    String [] partOfData = data.split("!");
                    for (int i=1; i < partOfData.length; i++) {
                        String [] folderName = partOfData[i].split("=");
                        folders.add(folderName[0]);
                    }
                    for (int j=0; j < folders.size(); j++) {
                        System.out.println(folders.get(j));
                    }
                    folderAdapter = new FolderAdapter(context,folders);
                    folderView.setAdapter(folderAdapter);
                    //    stringValues(document.getData().toString());
                    //TODO PLOCKA UT FÖRSTA STRING VÄRDET OCH LAGRA TILL LISTA FÖR ARRAYEN
                } else {
                    System.out.println("Cached get failed:" + task.getException()); }
            }
        });
    }
}
