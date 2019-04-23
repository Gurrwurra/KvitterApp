package com.example.kvitter.Util;

import com.example.kvitter.Activities.User;

public class CurrentUser {
    private static User currentUser;

    public static void setUser(User user)
    {
        currentUser = user;
    }
    public static User getUser() { return currentUser; }
}
