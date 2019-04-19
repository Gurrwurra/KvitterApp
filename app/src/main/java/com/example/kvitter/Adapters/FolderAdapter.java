package com.example.kvitter.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kvitter.R;
import com.example.kvitter.Util.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    private List<Data> mDataset;
    private Context context;
    private List<String> receipts = new ArrayList<>();
    private RecyclerView.Adapter folderAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView folderName, note;
        public RecyclerView reciept;

        public ViewHolder(View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.txt_folderName);
            reciept = itemView.findViewById(R.id.receipt_List);
            note = itemView.findViewById(R.id.txt_note);
            folderName.setTag(itemView);
            folderName.setOnClickListener(this);
            reciept.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == folderName.getId()) {
                if (reciept.getVisibility() == View.VISIBLE) {
                    reciept.setVisibility(View.GONE);
                } else {
                    reciept.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FolderAdapter(Context context, List<Data> myDataset) {
        this.context = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_folder, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            String folderName = mDataset.get(position).getName();
            holder.folderName.setText(folderName);
            holder.reciept.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(context);
            holder.reciept.setLayoutManager(layoutManager);
            AddReceiptsToFolder(holder,folderName);
        }

    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    //  holder.note.setText(mDataset.get(position));

    // Return the size of your dataset (invoked by the layout manager)
    private void AddReceiptsToFolder(ViewHolder holder, String folderName) {
            String[] testData = new String[4];
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("user_data").document("HINCqfhWGB9XwGtGBtYl");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        //HÄMTAR FOLDERNAME OCH LAGRAR I ARRAY (FOLDERS)
                        String data = document.get("folder.!" + folderName).toString();
                        System.out.println("DATA SOM BEHÖVS" + data);
                        String[] partOfData = data.split(",");
                        for (int i = 0; i < partOfData.length; i++) {
               //             testData.add(partOfData[i]);
                            System.out.println(partOfData[i] + " "+ folderName);
                        }
                    } else {
                        System.out.println("Cached get failed:" + task.getException());
                    }
                }
            });
        testData[0] = "test 1";
        testData[1] = "test 2";
        testData[2] = "test 3";
        testData[3] = "test 4";
        folderAdapter = new ReceiptAdapter(context, testData);
        holder.reciept.setAdapter(folderAdapter);
    }
}

