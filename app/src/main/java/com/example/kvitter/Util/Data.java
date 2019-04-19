package com.example.kvitter.Util;

public class Data {
   public static final int TYPE_FOLDER = 0;
   public static final int TYPE_RECEIPT = 1;

   private String name, amount;
   private int type;

   public Data() {

   }
   public Data(String name, String amount, int type) {
       this.name = name;
       this.amount = amount;
       this.type = type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
