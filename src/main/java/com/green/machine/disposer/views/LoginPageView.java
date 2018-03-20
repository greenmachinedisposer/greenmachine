package com.green.machine.disposer.views;

import com.green.machine.disposer.authentication.AccessControl;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;

import java.io.Serializable;


/**
 * Created by Jurol on 3/21/2018.
 */
public class LoginPageView extends VerticalLayout implements View{
    protected TextField username;
    protected PasswordField password;
    protected Button loginButton;
    protected CheckBox remember;

    private LoginListener loginListener;
    private AccessControl accessControl;

    public interface LoginListener extends Serializable {
        void loginSuccess();
    }

    public LoginPageView(AccessControl accessControl, LoginListener loginListener){
        this.accessControl = accessControl;
        this.loginListener = loginListener;

        username = new TextField("User name");
        password = new PasswordField("Password");
        loginButton = new Button("Login");
        remember = new CheckBox("Remember me");
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.setDisableOnClick(true);
        loginButton.addClickListener(event -> {
            if ( ( username == null || username.isEmpty() ) && ( password == null || password.isEmpty() ) ) {
                showNotification( new Notification( "Unable to Login",
                        "Please check your username and password and try again.",
                        Notification.Type.TRAY_NOTIFICATION ) );
                loginButton.setEnabled(true);
            } else {
                try {
                    login();
                } finally {
                    loginButton.setEnabled(true);
                }

            }
        });


        addComponent(username);
        addComponent(password);
        addComponent(loginButton);
        addComponent(remember);

    }



    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

        System.out.println("Enter Login Page");
    }

    private void login() {
        if (accessControl.signIn( username.getValue(), password.getValue())) {
            loginListener.loginSuccess();
        } else {
            showNotification(new Notification("Unable to Login", "Please check your username and password and try again.", Notification.Type.TRAY_NOTIFICATION));
            username.setValue("");
            password.setValue("");
            username.focus();
        }
    }

    private void showNotification(Notification notification) {
        notification.setDelayMsec(2000);
        notification.show(Page.getCurrent());
    }

}
