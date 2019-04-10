package com.example.kvitter.Util;

import android.content.res.Configuration;

public class CurrentId {

    private static String usrId;

    public static void setUserId(String id)
    {
        usrId = id;
    }
    public static String getUserId() { return usrId; }
}

