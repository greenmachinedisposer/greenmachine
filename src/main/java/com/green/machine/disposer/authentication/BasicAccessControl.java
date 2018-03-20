package com.green.machine.disposer.authentication;

import com.green.machine.disposer.database.DataSource;
import com.green.machine.disposer.database.Query;
import com.green.machine.disposer.database.QueryBuilder;
import com.green.machine.disposer.models.Credential;
import com.green.machine.disposer.models.UserID;
import org.apache.commons.codec.digest.DigestUtils;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Jurol on 3/20/2018.
 */
public class BasicAccessControl implements  AccessControl {

    private static final long serialVersionUID = 1L;


    @Override
    public boolean signIn(String username, String password) {
        try {
            Credential credential = QueryBuilder.create(DataSource.getInstance().getConnection(), Query.GET_PASS)
                    .setString("username", username).executeQuerySingle(Credential.class);

            if (credential != null) {
                String salt = credential.getPassword().split(":")[1];

                if (credential.getPassword().split(":")[0].equalsIgnoreCase(getCryptedPassword(password, salt))) {

                    CurrentUser.set(QueryBuilder.create(DataSource.getInstance().getConnection(), Query.GET_USER_BY_ID)
                            .setLong("user_id", credential.getId()).executeQuerySingle(UserID.class));

                    return true;
                }
            }

        }catch  (SQLException | IOException | PropertyVetoException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isUserSignIn() {
        return CurrentUser.get() != null;
    }

    @Override
    public boolean isUserRole(String role) {
        if ("owner".equals(role))
            return getPrincipalName().equals("owner");

        return true;
    }

    @Override
    public String getPrincipalName() {
        return CurrentUser.get().getDisplay_name();
    }

    private String getCryptedPassword(String password, String salt) {
        return salt != null ? DigestUtils.md5Hex(password.concat(salt).getBytes())
                : DigestUtils.md5Hex(password.getBytes());
    }
}
