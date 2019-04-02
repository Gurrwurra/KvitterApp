package com.example.kvitter.Adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvitter.R;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    private String[] mDataset;
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
            testData[0] = "Kvitto för knätch";
            testData[1] = "Plankor";
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
    public FolderAdapter(Context context, String[] myDataset) {
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
        holder.folderName.setText(mDataset[position]);
        holder.note.setText(mDataset[position]);
        holder.reciept.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        holder.reciept.setLayoutManager(layoutManager);
        folderAdapter = new ReceiptAdapter(context, testData);
        holder.reciept.setAdapter(folderAdapter);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
