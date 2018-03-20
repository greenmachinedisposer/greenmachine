package com.green.machine.disposer.authentication;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Jurol on 3/20/2018.
 */
public class BasicAccessControl implements  AccessControl {

    @Override
    public boolean signIn(String username, String password) {
        try {


        }catch  (SQLException | IOException | PropertyVetoException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isUserSignIn() {
        return false;
    }

    @Override
    public boolean isUserRole(String role) {
        return false;
    }

    @Override
    public String getPrincipalName() {
        return null;
    }
}
