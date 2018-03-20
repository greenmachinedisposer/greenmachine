package com.green.machine.disposer.models;

import java.io.Serializable;

/**
 * Created by Jurol on 3/20/2018.
 */
public class Credential implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

    private String password;

    public long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
