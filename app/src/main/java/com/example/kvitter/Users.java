package com.example.kvitter;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Users {


    public String uid;
    public String address;
    public String city;
    public String email;
    public String firstname;
    public String password;
    public String personal_number;
    public String surname;
    public Users() {
    }

    public Users(String uid, String address, String city, String email, String firstname, String password, String personal_number, String surname) {
        this.uid = uid;
        this.address = address;
        this.city = city;
        this.email = email;
        this.firstname = firstname;
        this.password = password;
        this.personal_number = personal_number;
        this.surname = surname;
    }


}