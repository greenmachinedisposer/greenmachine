package com.green.machine.disposer.database;

/**
 * Created by Jurol on 3/21/2018.
 */
public class Query {
    public static final String GET_PASS =
            "SELECT user_id as id, password " +
            "FROM access.users " +
            "WHERE user_name = :username";

    public static final String GET_USER_BY_ID =
            "SELECT display_name, user_name, email, user_id " +
                    "FROM access.users " +
                    "WHERE user_id = :user_id";
}
