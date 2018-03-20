package com.green.machine.disposer.database;

/**
 * Created by Jurol on 3/21/2018.
 */
public class Query {
    public static final String GET_PASS =
            "SELECT id, password " +
            "FROM xe5z0_users" +
            "WHERE username = :username";
}
