package com.example.kvitter.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvitter.Adapters.FolderAdapter;
import com.example.kvitter.Adapters.MyAdapter;
import com.example.kvitter.Adapters.ReceiptAdapter;
import com.example.kvitter.DataEngine;
import com.example.kvitter.DatabaseLogic;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.Data;
import com.example.kvitter.Util.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
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
    List<UserData> reciepts = new ArrayList<>();
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
                Intent intent = new Intent(MyReceiptActivity.this, AddFolderActivity.class);
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
        db.collection("data").document("HINCqfhWGB9XwGtGBtYl")
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
                                //POPULATE WITH FOLDERNAME
                                testData.add(new UserData(folderData.get(i), UserData.FOLDER_TYPE));
                                for (int j = 2; j < eachObject.length; j++) {
                                    String[] eachDataInObject = eachObject[j].split(",");
                                    String[] amount = eachDataInObject[0].split("=");
                                    String[] supplier = eachDataInObject[1].split("=");
                                    String[] name = eachDataInObject[2].split("=");
                                    String[] comment = eachDataInObject[3].split("=");
                                    String[] photoRef = eachDataInObject[4].split("=");
                                    String[] folder = eachDataInObject[5].split("=");
                                    String curFolder = folder[folder.length-1];
                                    curFolder.replace("}","");
                                    //  testData.add(new UserData("Renovering", "test3 - nålar", "1000", "test", "asdsacssa21", "Beijer", UserData.RECIEPT_TYPE));
                                    if (curFolder.substring(0,curFolder.length()-1).contains(folderData.get(i))) {
                                        testData.add(new UserData(folderData.get(i), name[name.length - 1], amount[amount.length - 1], comment[comment.length - 1], photoRef[photoRef.length - 1], supplier[supplier.length - 1], UserData.RECIEPT_TYPE));
                                    }
                                    /*   System.out.println("amount: " + amount[amount.length-1] +
                                            "\nsupplier: " + supplier[supplier.length-1] +
                                             "\nname: " + name[name.length-1] +
                                            "\ncomment: " + comment[comment.length-1] +
                                            "\nphotoRef: " + photoRef[photoRef.length-1] +
                                            "\nfolder: " + folder[folder.length-1]
                                    ); */

                                }
                           //     populateReciepts(fulLData,folderData.get(i));
                            }
                        }
                        //SKRIVA UT TESTDATA (KÖRA METODER OCH ANNAT)
                        MyAdapter adapter = new MyAdapter(testData);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, OrientationHelper.VERTICAL, false);
                        folderView = folderView.findViewById(R.id.folder_list);
                        folderView.setLayoutManager(linearLayoutManager);
                        folderView.setItemAnimator(new DefaultItemAnimator());
                        folderView.setAdapter(adapter);
                    }
                });
    }
    private void populateReciepts(String data, String folderName) {
         List<String> test = new ArrayList<>();
        System.out.println("FolderName::::: " + folderName);
        String[] eachObject = data.split("\\{");
        for (int i = 2; i < eachObject.length; i++) {
            System.out.println("Detta ska bara skriva ut 2 gånger för : " + folderName );
            //  System.out.println("each object: " + eachObject[i]);
            String[] eachDataInObject = eachObject[i].split(",");
            String [] correctName = new String[1];
            for (int j = 0; j < eachDataInObject.length; j++) {
                //        System.out.println("each data in object: " + eachDataInObject[j]);
                String [] specificFolderName = eachDataInObject[5].split("=");
                correctName = specificFolderName[1].split("\\}");
                if (correctName[0].equals(folderName)) {
                    test.add(eachDataInObject[j]);
                    for (int k=0; k <test.size(); k++) {
                        System.out.println("Vad skrivs här?: " + test.get(k));
                    }
                    //  testData.add(new UserData("Renovering", "test3 - nålar", "1000", "test", "asdsacssa21", "Beijer", UserData.RECIEPT_TYPE));
                    //    testData.add(new UserData(correctName[0],))
                    //        System.out.println("test " + dataElements[dataElements.length-1]);
                    //          System.out.println("data to save? " + eachDataInObject[j]);
                }

            }

        }
    }
    private void populateFolders(String data) {
        //  System.out.println("full data: " + data);

        String[] eachObject = data.split("\\{");

        for (int i = 2; i < eachObject.length; i++) {
            //  System.out.println("each object: " + eachObject[i]);

            String[] eachDataInObject = eachObject[i].split(",");

            for (int j = 0; j < eachDataInObject.length; j++) {
                //        System.out.println("each data in object: " + eachDataInObject[j]);
                String [] folderName = eachDataInObject[5].split("=");
                String [] correctName = folderName[1].split("\\}");
                if (!folderData.contains(correctName[0])) {
                    folderData.add(correctName[0]);
                    //               System.out.println("test " + correctName[0]);
                }
            }
        }
    }
    }

