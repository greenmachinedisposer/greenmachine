package com.green.machine.disposer.views;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Jurol on 4/9/2018.
 */
public interface CommonViewInterface {
    void setUpUI();
    void getInitData() throws SQLException, IOException, PropertyVetoException;
}
