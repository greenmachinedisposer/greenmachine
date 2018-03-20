package com.green.machine.disposer.authentication;

import java.io.Serializable;

/**
 * Created by Jurol on 3/20/2018.
 */
public interface AccessControl extends Serializable {

    boolean signIn(String username, String password);

    boolean isUserSignIn();

    boolean isUserRole(String role);

    String getPrincipalName();
}
