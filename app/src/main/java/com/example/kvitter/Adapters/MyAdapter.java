package com.example.kvitter.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kvitter.Activities.Specific_receipt;
import com.example.kvitter.R;
import com.example.kvitter.Util.Folders;
import com.example.kvitter.Util.UserData;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UserData> mList;

    public MyAdapter(List<UserData> list) {
        this.mList = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case UserData.FOLDER_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
                return new FolderViewHolder(view);
            case UserData.RECIEPT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt, parent, false);
                return new ReceiptViewHolder(view);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UserData object = mList.get(position);
        if (object != null) {
            switch (object.getType()) {
                case UserData.FOLDER_TYPE:
                    ((FolderViewHolder) holder).folderName.setText(object.getFolderName());
                    break;
                case UserData.RECIEPT_TYPE:
                    ((ReceiptViewHolder) holder).recieptName.setText(object.getName());
                    ((ReceiptViewHolder) holder).recieptAmount.setText("Amount: " +object.getAmount() + "\nMapp: " + object.getFolderName());
                    break;
            }
        }
    }
    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }
    @Override
    public int getItemViewType(int position) {
        if (mList != null) {
            UserData object = mList.get(position);
            if (object != null) {
                return object.getType();
            }
        }
        return 0;
    }
    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        private TextView folderName;
        public FolderViewHolder(View itemView) {
            super(itemView);
            folderName = (TextView) itemView.findViewById(R.id.folder_name);
        }
    }
    public static class ReceiptViewHolder extends RecyclerView.ViewHolder {
        private TextView recieptName;
        private TextView recieptAmount;
        public ReceiptViewHolder(View itemView) {
            super(itemView);
            recieptName = (TextView) itemView.findViewById(R.id.receipt_name);
            recieptAmount = (TextView) itemView.findViewById(R.id.receipt_amount);
        }
    }
}