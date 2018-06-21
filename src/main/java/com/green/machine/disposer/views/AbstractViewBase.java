package com.green.machine.disposer.views;

import com.green.machine.disposer.authentication.CurrentUser;
import com.green.machine.disposer.models.UserID;
import com.vaadin.navigator.View;
import com.vaadin.ui.VerticalLayout;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Jurol on 6/22/2018.
 */
public class AbstractViewBase extends VerticalLayout implements CommonViewInterface, View {

    protected UserID user;

    public AbstractViewBase() {
        user = CurrentUser.get();
    }
    @Override
    public void setUpUI() {

    }

    @Override
    public void getInitData() throws SQLException, IOException, PropertyVetoException {

    }
}
