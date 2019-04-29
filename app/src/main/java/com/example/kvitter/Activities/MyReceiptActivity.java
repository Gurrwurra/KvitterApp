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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                            Map<String, Object> map = document.getData();

                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                int folderType = Integer.parseInt(document.get(key+ ".type").toString());
                                if (folderType == 0) {
                                    folderData.add(key);
                                }
                            }
                            for (int i = 0; i < folderData.size(); i++) {
                                testData.add(new UserData(folderData.get(i), UserData.FOLDER_TYPE));

                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    UserData newData = new UserData();
                                    String key = entry.getKey();
                                    int type = Integer.parseInt(document.get(key+ ".type").toString());
                                    if (type ==1) {
                                        String curFolder = document.get(key + ".folderName").toString();
                                        if (curFolder.contains(folderData.get(i)) && Integer.parseInt(document.get(key + ".type").toString()) == 1) {
                                            newData.setName(key);
                                            newData.setSupplier(document.get(key + ".supplier").toString());
                                            newData.setAmount(document.get(key + ".amount").toString());
                                            newData.setComment(document.get(key + ".comment").toString());
                                            newData.setPhotoRef(document.get(key + ".photoRef").toString());
                                            newData.setFolderName(document.get(key + ".folderName").toString());
                                            newData.setType(Integer.parseInt(document.get(key + ".type").toString()));
                                            testData.add(newData);
                                    }
                                    }
                                }
                            }
                        }
                        MyAdapter adapter = new MyAdapter(testData, context);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, OrientationHelper.VERTICAL, false);
                        folderView = folderView.findViewById(R.id.folder_list);
                        folderView.setLayoutManager(linearLayoutManager);
                        folderView.setItemAnimator(new DefaultItemAnimator());
                        folderView.setAdapter(adapter);
                    }
                });
    }
}


