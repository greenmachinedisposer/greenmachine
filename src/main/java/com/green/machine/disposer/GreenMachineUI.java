package com.green.machine.disposer;

import javax.servlet.annotation.WebServlet;

import com.green.machine.disposer.authentication.AccessControl;
import com.green.machine.disposer.authentication.BasicAccessControl;
import com.green.machine.disposer.authentication.CurrentUser;
import com.green.machine.disposer.views.DashboardPageView;
import com.green.machine.disposer.views.LoginPageView;
import com.green.machine.disposer.views.LoginPageView.LoginListener;
import com.green.machine.disposer.views.MainPageView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;


/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class GreenMachineUI extends UI {

    private AccessControl accessControl = new BasicAccessControl();
    public static boolean isNotSignedIn;


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Page.getCurrent().setTitle("Green Machine Disposer");
        Responsive.makeResponsive(this);

        isNotSignedIn = !accessControl.isUserSignIn();

        if (isNotSignedIn) {
            setContent(new LoginPageView(accessControl, new LoginListener() {
                private static final long serialVersionUID = 1L;

                @Override
                public void loginSuccess() {
                    showMainView();
                }
            }));
        } else{
            showMainView();
        }

    }

    protected void showMainView() {
        System.out.println( CurrentUser.get().getDisplay_name());
        System.out.println( "Successfully Logged in");
//        setContent(new MainPageView());
    }
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = GreenMachineUI.class, productionMode = true)
    public static class MyUIServlet extends VaadinServlet {
    }
}
