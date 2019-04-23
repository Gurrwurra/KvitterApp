package com.example.kvitter.Activities;

public class User {
    private String firstName, surName, address, city, phone, personalNumber, mail, pwd;

    public User(String firstName,String surName,String address,String city,String phone,String personalNumber, String mail, String password) {
        this.firstName = firstName;
        this.surName = surName;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.personalNumber = personalNumber;
        this.mail = mail;
        this.pwd = password;
    }
    public User() {
    }

    public String getPassword() { return pwd; }

    public void setPassword(String password) { this.pwd = password; }

    public String getMail() { return mail; }

    public void setMail(String mail) { this.mail = mail; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }
}
