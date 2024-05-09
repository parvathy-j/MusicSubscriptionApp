package com.example.musicsubscription.model;


public class Usermodel {

    private String email;
    private String password;
    private String user_name;


    public void User() {
    }

    // Constructor with parameters
    public void  User(String email, String password, String user_name) {
        this.email = email;
        this.password = password;
        this.user_name=user_name;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return user_name;
    }

    public void setUsername(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
