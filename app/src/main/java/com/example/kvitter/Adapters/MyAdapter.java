package com.example.kvitter.Adapters;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvitter.Activities.MyReceiptActivity;
import com.example.kvitter.Activities.Specific_receipt;
import com.example.kvitter.DataEngine;
import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentReceipt;
import com.example.kvitter.Util.UserData;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UserData> mList;
    public Context context;
    private ConstraintLayout itemReceipt;
    private List<RecyclerView.ViewHolder> list = new ArrayList<>();
    private List<String> recList = new ArrayList<>();
    private List<Boolean> curStates = new ArrayList<>();

    /*
    Constructor for MyAdapter -
    @param list - data of all folders and their receipts for current user
    @param context - context of application (MyReceiptActivity)
     */
    public MyAdapter(List<UserData> list, Context context) {
        this.mList = list;
        this.context = context;
    }

    /*
    ViewHolder with different viewType -
    Iterates over List<UserData> mList and returns view that matches position in List (FOLDER_TYPE) OR (RECEIPT_TYPE)
     */
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

    /*
    Gets position of object in List<Userdata> mList and updating data for current position and type
 */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UserData object = mList.get(position);
        if (object != null) {
            switch (object.getType()) {
                case UserData.FOLDER_TYPE:
                    ((FolderViewHolder) holder).folderName.setText(object.getFolderName());
                    break;
                case UserData.RECIEPT_TYPE:
                    System.out.println(holder.getAdapterPosition());
                    ((ReceiptViewHolder) holder).recieptName.setText(object.getName());
                    ((ReceiptViewHolder) holder).recieptAmount.setText("Amount: " + object.getAmount() + "\nMapp: " + object.getFolderName());
                    String date = object.getDate();
                    String[] dateSplit = date.split("/");
                    String day = dateSplit[0];
                    String month = dateSplit[1];
                    month = getMonth(month);
                    ((ReceiptViewHolder) holder).txtDateDay.setText(day);
                    ((ReceiptViewHolder) holder).txtDateMonth.setText(month);

                    ViewGroup.LayoutParams layoutParams =holder.itemView.getLayoutParams();
                    layoutParams.width= View.GONE;
                    layoutParams.height= View.GONE;
                    holder.itemView.setLayoutParams(layoutParams);
                    recList.add(object.getFolderName());
                    curStates.add(false);
                    list.add(holder);
                    break;
            }
        }
    }

    private String getMonth(String month) {
        if (month.contains("1")) {
            return "Jan";
        }
        if (month.contains("2")) {
            return "Feb";
        }
        if (month.contains("3")) {
            return "Mar";
        }
        if (month.contains("4")) {
            return "Apr";
        }
        if (month.contains("5")) {
            return "Maj";
        }
        if (month.contains("6")) {
            return "Jun";
        }
        if (month.contains("7")) {
            return "Jul";
        }
        if (month.contains("8")) {
            return "Aug";
        }
        if (month.contains("9")) {
            return "Sep";
        }
        if (month.contains("10")) {
            return "Okt";
        }
        if (month.contains("11")) {
            return "Nov";
        }
        if (month.contains("12")) {
            return "Dec";
        }
        return month;
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

    public class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView folderName, txtFolderName;
        private EditText editFolderName;
        private ImageButton editFolder, saveFolder;
        ConstraintLayout editFolderLayout, constraintLayout;


        /*
        For each item in folderViewHolder - method binds every element in that item
         */
        public FolderViewHolder(View itemView) {
            super(itemView);
            editFolderName = itemView.findViewById(R.id.edit_spec_folder);
            //editFolderName.setVisibility(View.GONE);
            txtFolderName = itemView.findViewById(R.id.txt_editFolder_name);
            //txtFolderName.setVisibility(View.GONE);
            folderName = itemView.findViewById(R.id.folder_name);
            saveFolder = itemView.findViewById(R.id.btn_save_editFolderName);
            saveFolder.setTag(itemView);
            saveFolder.setOnClickListener(this);
            //saveFolder.setVisibility(View.GONE);
            editFolder = itemView.findViewById(R.id.btn_edit_folder);
            editFolder.setTag(itemView);
            editFolder.setOnClickListener(this);
            editFolderLayout = itemView.findViewById(R.id.edit_folder_layout);
            constraintLayout = itemView.findViewById(R.id.constraintLayout2);
            constraintLayout.setOnClickListener(this);
        }

        /*
        OnClick listener for each item in FolderViewHolder
        case R.id.btn_save_editFolderName - Data for new folderName from user gets collected and saved. Runs method updateFolder to update database
        case R.id.btn_edit_folder - If user clicks on edit_folder button - information will show for user on how to edit folderName
         */
        @Override
        public void onClick(View v) {
            //ImageButton btn = (ImageButton) v;
            switch (v.getId()) {
                case R.id.btn_save_editFolderName: {
                    editFolderLayout.setVisibility(View.GONE);
                    String newFolderName = editFolderName.getText().toString();
                    DataEngine engine = new DataEngine();
                    engine.updateFolder(newFolderName, folderName.getText().toString(), mList.get(getAdapterPosition()));
                    Intent myAccount = new Intent(context, MyReceiptActivity.class);
                    context.startActivity(myAccount);
                    break;
                }
                case R.id.btn_edit_folder: {
                    if (editFolderLayout.getVisibility() == View.VISIBLE) {
                        editFolderLayout.setVisibility(View.GONE);
                    } else {
                        editFolderLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                }
                case R.id.constraintLayout2: {

                    String curFolder = folderName.getText().toString();
                    for(int i =0; i< recList.size(); i++) {
                        if (recList.get(i).contains(curFolder)) {
                            onBindReceipts(list.get(i),curStates.get(i),i);
                        }
                    }
                    break;
                }
                }
        }
        private void onBindReceipts(RecyclerView.ViewHolder holder, Boolean curState, int posOfState) {
            ViewGroup.LayoutParams layoutParams =holder.itemView.getLayoutParams();

            if (curState==true) {
                onTrue(holder, posOfState);
            }
            else if (curState == false) {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.itemView.setLayoutParams(layoutParams);
                curStates.set(posOfState,true);
            }
        }
        private void onTrue(RecyclerView.ViewHolder holder, int i) {
            ViewGroup.LayoutParams layoutParams =holder.itemView.getLayoutParams();
            layoutParams.width= View.GONE;
            layoutParams.height= View.GONE;
            holder.itemView.setLayoutParams(layoutParams);
            curStates.set(i,false);

        }
    }

    public class ReceiptViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView recieptName, recieptAmount, txtDateDay, txtDateMonth;


        public ReceiptViewHolder(View itemView) {
            super(itemView);
            recieptName = (TextView) itemView.findViewById(R.id.receipt_name);
            recieptAmount = (TextView) itemView.findViewById(R.id.receipt_amount);
            txtDateDay = (TextView) itemView.findViewById(R.id.txt_dateDay);
            txtDateMonth = (TextView) itemView.findViewById(R.id.txt_DateMonth);
            recieptName.setTag(itemView);
            itemReceipt = itemView.findViewById(R.id.item_receipt);
            itemReceipt.setOnClickListener(this);
        }

        /*
        onClickListener - When user clicks on specific receipt in ReceiptViewHolder - method will run Activity "Specific_receipt.class"
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_receipt: {
                    Intent intent = new Intent(context, Specific_receipt.class);
                    CurrentReceipt.setReceipt(mList.get(getLayoutPosition()));
                    context.startActivity(intent);
                    break;
                }

            }
        }
    }
}