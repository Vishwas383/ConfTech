package com.example.conftech.models;

public class UserModel {

    String userId,fname,lname,email,role,password,photo;

    public UserModel() {
    }

    public UserModel(String userId, String fname, String lname, String email, String role, String password, String photo) {
        this.userId = userId;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.role = role;
        this.password = password;
        this.photo = photo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
