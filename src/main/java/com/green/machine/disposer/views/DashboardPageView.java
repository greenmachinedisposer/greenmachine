package com.green.machine.disposer.views;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;


/**
 * Created by Jurol on 4/9/2018.
 */
public class DashboardPageView extends AbstractViewBase{
    public static final String VIEW_NAME = "dashboard";


    public DashboardPageView(){
        super();
        System.out.println("Dashboard");
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.setUpUI();
        System.out.println("Dashboard View Enter");
    }

    @Override
    public void setUpUI() {
        Label l = new Label("Dashboard");
        addComponent(l);
    }

    @Override
    public void getInitData() throws SQLException, IOException, PropertyVetoException {

    }
}
