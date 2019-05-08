package com.example.kvitter.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MyReceiptActivity extends NavigationActivity {
    private RecyclerView folderView;
    private FloatingActionButton fab;
    List<UserData> usersReceiptList = new ArrayList<>();
    List<String> folderData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_receipt);
        runNavigation(R.id.activity_myReceipt);
        bindViews();
        populateAdapterList(this);
        addListiners();
    }
    /*
    OnClick - Starts activity "newFolderActivity" to add new folder
     */
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

    /*
    Populates List<UserData> usersReceiptList with data to current user from database
    Method retrieves all folders that the user have and populate usersReceiptList with receipt for that folder
    Method runs class MyAdapter with usersReceiptList as parameter
     */
    public void populateAdapterList(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("data").document(CurrentId.getUserId())
                .get()
                .addOnCompleteListener(task -> {
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
                            usersReceiptList.add(new UserData(folderData.get(i), UserData.FOLDER_TYPE));
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
                                        newData.setDate(document.get(key+".date").toString());
                                        usersReceiptList.add(newData);
                                }
                                }
                            }
                        }
                    }
                    setAdapterSettings();
                });
    }
        /*
    Creates layout for myAdapter class
     */
    private void setAdapterSettings() {
        MyAdapter adapter = new MyAdapter(usersReceiptList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        folderView = folderView.findViewById(R.id.folder_list);
        folderView.setLayoutManager(linearLayoutManager);
        folderView.setItemAnimator(new DefaultItemAnimator());
        folderView.setAdapter(adapter);
    }
}


