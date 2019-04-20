package com.example.kvitter.Util;

public class CurrentReceipt {
    private static UserData currentReceipt;

    public static void setReceipt(UserData receipt)
    {
        currentReceipt = receipt;
    }
    public static UserData getReceipt() { return currentReceipt; }
}
