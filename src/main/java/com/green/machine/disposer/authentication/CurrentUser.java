package com.green.machine.disposer.authentication;

import com.green.machine.disposer.models.UserID;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;

public class CurrentUser {

	public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = CurrentUser.class.getCanonicalName();

	public CurrentUser() {}

	public static UserID get() {
		UserID currentUser =  (UserID) getCurrentRequest().getWrappedSession().getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
		if (currentUser == null) {
			return null;
		} else {
			return currentUser;
		}
	}

	public static void set(UserID currentUser) {
		if (currentUser == null) {
			getCurrentRequest().getWrappedSession().removeAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
		} else {
			getCurrentRequest().getWrappedSession().setAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY, currentUser);
		}
	}

	private static VaadinRequest getCurrentRequest() {
		VaadinRequest request = VaadinService.getCurrentRequest();
		if (request == null) {
			throw new IllegalStateException("No request bound to current thread");
		}
		return request;
	}	
}
