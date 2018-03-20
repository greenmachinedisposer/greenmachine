package com.green.machine.disposer.models;

/**
 * Created by Jurol on 3/21/2018.
 */
public class UserID {
    private String display_name;
    private String user_name;
    private String email;
    private int user_id;

    public UserID(){}

    public UserID(String display_name, String user_name, String email, int user_id) {
        this.display_name = display_name;
        this.user_name = user_name;
        this.email = email;
        this.user_id = user_id;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getEmail() {
        return email;
    }

    public int getUser_id() {
        return user_id;
    }
}
