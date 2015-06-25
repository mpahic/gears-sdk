package org.cloudcog.kani.ui;

import org.cloudcog.kani.RepositoryContext;
import org.cloudcog.kani.login.ISessionListener;
import org.cloudcog.kani.ui.login.LoginView;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

public abstract class AbstractAuthorizableUI extends UI implements ISessionListener {
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
