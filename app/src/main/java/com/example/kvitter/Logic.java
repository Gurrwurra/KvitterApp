package com.example.kvitter;

public class Logic {


    public boolean validateUser(String usrName, String pwd) {
        if (usrName.contains("Henke")&& pwd.contains("ebög")) {
            return true;
        }
        else {
            return false;
        }
    }
}
