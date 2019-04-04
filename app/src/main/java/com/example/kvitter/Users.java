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
    public Map<String, Boolean> stars = new HashMap<>();

    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Users( String uid,
             String address,
             String city,
             String email,
             String firstname,
             String password,
             String personal_number,
             String surname) {
        this.uid = uid;
        this.address = address;
        this.city = city;
        this.email = email;
        this.firstname = firstname;
        this.password = password;
        this.personal_number = personal_number;
        this.surname = surname;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("address", address);
        result.put("city", city);
        result.put("email", email);
        result.put("firstname", firstname);
        result.put("password", password);
        result.put("personal_number", personal_number);
        result.put("surname", surname);

        return result;
    }

}