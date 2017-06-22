package com.bandfitproject.data;

import java.util.ArrayList;

public class User {
    public String firebasekey;
    public String id;
    public String password;
    public String name;
    public String email;
    public boolean isLogin;
    public String fcmToken;
    public ArrayList<String> engaging_board;

    public String getFirebasekey() {
        return firebasekey;
    }

    public void setFirebasekey(String firebasekey) {
        this.firebasekey = firebasekey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getEngaging_board() {
        return engaging_board;
    }

    public void setEngaging_board(ArrayList<String> engaging_board) {
        this.engaging_board = engaging_board;
    }

    public User() {

    }

    public User(String id, String password, String name, String email, ArrayList<String> engaging_board) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
        this.engaging_board = engaging_board;
    }
}
