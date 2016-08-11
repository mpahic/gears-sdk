package org.cloudcog.gears.sdk.ui;

import org.cloudcog.gears.sdk.RepositoryContext;
import org.cloudcog.gears.sdk.login.ISessionListener;
import org.cloudcog.gears.sdk.ui.login.LoginView;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

public abstract class AbstractAuthorizableUI extends GearsUI implements ISessionListener {
	private static final long serialVersionUID = 4786085432570500274L;

	@Override
	protected void init(VaadinRequest request) {

		VaadinSession.getCurrent().setLocale(request.getLocale());
		if (hasAccessRights()) {
			showAppScreen();
		} else {
			showLoginScreen();
		}
	}

	protected abstract boolean hasAccessRights();

	private void showLoginScreen() {
		setContent(new LoginView());
	}

	protected abstract void showAppScreen();

	@Override
	public void sessionCreateHandler() {
		showAppScreen();
	}

	@Override
	public void sessionDestroyHandler() {
		RepositoryContext.destroyJcrSession();
		showLoginScreen();
	}

	public static AbstractAuthorizableUI getCurrent() {
		return (AbstractAuthorizableUI) UI.getCurrent();
	}

}
