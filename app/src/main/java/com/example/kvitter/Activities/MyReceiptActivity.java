package com.example.kvitter.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.example.kvitter.Adapters.MyAdapter;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MyReceiptActivity extends AppCompatActivity {
    private RecyclerView folderView;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;
    List<UserData> testData = new ArrayList<>();
    List<String> folderData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_receipt);
        bindViews();
        folderView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        folderView.setLayoutManager(layoutManager);
        readAllReciepts(this);
        addListiners();
    }

    private void addListiners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyReceiptActivity.this, newFolderActivity.class);
                startActivity(intent);
            }
        });
    }

    private void bindViews() {
        folderView = (RecyclerView) findViewById(R.id.folder_list);
        fab = findViewById(R.id.FAB_folder);
    }

    public void readAllReciepts(Context context) {
        folderView = folderView.findViewById(R.id.folder_list);
        folderView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        folderView.setLayoutManager(layoutManager);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("data").document(CurrentId.getUserId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String fulLData = document.getData().toString();
                            String[] eachObject = fulLData.split("\\{");
                            System.out.println("COMPLETE DATA" + " => " + fulLData);
                            populateFolders(fulLData);
                            for(int i=0; i< folderData.size(); i++) {
                                testData.add(new UserData(folderData.get(i), UserData.FOLDER_TYPE));
                                for (int j = 2; j < eachObject.length; j++) {
                                    String[] eachDataInObject = eachObject[j].split(",");
                                    String[] amount = eachDataInObject[0].split("=");
                                    String[] supplier = eachDataInObject[1].split("=");
                                    String[] name = eachDataInObject[2].split("=");
                                    String[] comment = eachDataInObject[3].split("=");
                                    String[] photoRef = eachDataInObject[4].split("=");
                                    String[] folder = eachDataInObject[5].split("=");
                                    String[] type = eachDataInObject[6].split("=");
                                    String curFolder = folder[folder.length-1];
                                    if (curFolder.contains(folderData.get(i)) && type[type.length-1].contains("1")) {
                                        testData.add(new UserData(folderData.get(i), name[name.length - 1], amount[amount.length - 1], comment[comment.length - 1], photoRef[photoRef.length - 1], supplier[supplier.length - 1], UserData.RECIEPT_TYPE));
                                    }
                                }
                            }
                        }
                        MyAdapter adapter = new MyAdapter(testData,context);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, OrientationHelper.VERTICAL, false);
                        folderView = folderView.findViewById(R.id.folder_list);
                        folderView.setLayoutManager(linearLayoutManager);
                        folderView.setItemAnimator(new DefaultItemAnimator());
                        folderView.setAdapter(adapter);
                    }
                });
    }

    private void populateFolders(String data) {
        String[] eachObject = data.split("\\{");
        for (int i = 2; i < eachObject.length; i++) {
            String[] eachDataInObject = eachObject[i].split(",");
            for (int j = 0; j < eachDataInObject.length; j++) {
             String [] folderName = eachDataInObject[5].split("=");
                String [] correctName = folderName[1].split("\\}");
                if (!folderData.contains(correctName[0])) {
                    folderData.add(correctName[0]);
               }
            }
        }
    }
    }

