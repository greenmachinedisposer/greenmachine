package com.green.machine.disposer.views;

import com.green.machine.disposer.authentication.CurrentUser;
import com.green.machine.disposer.design.MainPage;
import com.green.machine.disposer.models.UserID;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class MainPageView extends MainPage {
    protected CssLayout content;

    private final UserID USER;

    public MainPageView() {
        this.USER = CurrentUser.get();

        Navigator navigator = new Navigator(UI.getCurrent(), content);
        navigator.addView(DashboardPageView.VIEW_NAME, DashboardPageView.class);

        doNavigate(DashboardPageView.VIEW_NAME);
    }

    private void doNavigate(String viewName) {
        UI.getCurrent().getNavigator().navigateTo(viewName);
    }
}
