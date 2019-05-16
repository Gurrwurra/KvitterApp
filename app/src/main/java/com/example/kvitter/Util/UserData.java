package com.example.kvitter.Util;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.Map;

public class UserData {
    public static final int FOLDER_TYPE = 0;
    public static final int RECIEPT_TYPE = 1;
    private String folderName, name, amount, comment, photoRef, supplier, date;
    private int type;

    public UserData(String folderName, String name, String amount, String comment, String photoRef, String supplier, int type, String date) {
        this.folderName = folderName;
        this.type = type;
        this.name = name;
        this.amount = amount;
        this.comment = comment;
        this.photoRef = photoRef;
        this.supplier = supplier;
        this.date = date;
    }
    public UserData(String folderName, int type) {
        this.folderName = folderName;
        this.type = type;
    }

    public UserData() {
    }

    public String getDate() {return date;}
    public void setDate(String date) { this.date = date; }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(String photoRef) {
        this.photoRef = photoRef;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    @Override
    public String toString() {
        return "Reciept :" + "\nname: " + name + "\namount: " + amount + "\nsupplier: " + supplier + "\nphotoRef: "+ photoRef + "\n"+ "\nfolderName: "+ folderName + "\n" + "\ncomment: "+ comment + "\n";
    }
}
