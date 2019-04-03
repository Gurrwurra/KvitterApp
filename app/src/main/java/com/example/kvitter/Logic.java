package com.example.kvitter;

public class Logic {


    public boolean validateUser(String usrName, String pwd) {
        if (usrName.contains("gurra")&& pwd.contains("snopp")) {
            return true;
        }
        else {
            return false;
        }
    }
}
