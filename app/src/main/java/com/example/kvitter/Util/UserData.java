package com.example.kvitter.Util;

public class UserData {
    private String folderName, name, amount, comment, photoRef, supplier;

    public UserData(String folderName, String name, String amount, String comment, String photoRef, String supplier) {
        this.folderName = folderName;
        this.name = name;
        this.amount = amount;
        this.comment = comment;
        this.photoRef = photoRef;
        this.supplier = supplier;
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
}
