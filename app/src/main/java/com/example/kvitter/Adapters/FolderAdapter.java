package com.example.kvitter.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentId;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    private List<String> mDataset;
    private Context context;

    private RecyclerView folderView;
    private RecyclerView.Adapter folderAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] testData = new String[3];

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
            if (v.getId() == folderName.getId()){
                reciept.setVisibility(View.VISIBLE);
            }
            }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FolderAdapter(Context context, List<String> myDataset) {
        this.context = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_folder, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.folderName.setText(mDataset.get(position));
        holder.note.setText(mDataset.get(position));
        holder.reciept.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        holder.reciept.setLayoutManager(layoutManager);
        folderAdapter = new ReceiptAdapter(context, testData);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String documentName = CurrentId.getUserId();
        DocumentReference docRef = db.collection("user_data").document(documentName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    holder.reciept.setAdapter(folderAdapter);
                    // {receipts=[{amount=500, supplier=Willys, name=Test2, comment=test2, photoRef=reciept/4c789837-26ee-4582-906e-1a120b0544e1-35}]}
                    //         System.out.println(document.getData().toString());
                    testData[0] = document.get("folder.!Sommarstugan").toString();
                    //    stringValues(document.getData().toString());
                } else {
                    System.out.println("Cached get failed:" + task.getException()); }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
