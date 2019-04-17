package com.example.kvitter.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvitter.Activities.MyAccountActivity;
import com.example.kvitter.Activities.Specific_receipt;
import com.example.kvitter.R;

import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {
    private List<String> mDataset;
    private Context context;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView receiptName, amount;

        public ViewHolder(View itemView) {

            super(itemView);
            receiptName = itemView.findViewById(R.id.txt_receiptName);
            amount = itemView.findViewById(R.id.txt_amount);
            receiptName.setTag(itemView);

            receiptName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, Specific_receipt.class);
            context.startActivity(intent);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReceiptAdapter(Context context, List<String> myDataset) {
        this.context = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_receipt, parent, false);
        return new ViewHolder(v);
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.receiptName.setText(mDataset.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

