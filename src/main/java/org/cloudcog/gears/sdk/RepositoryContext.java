package org.cloudcog.gears.sdk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

import javax.jcr.GuestCredentials;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.core.TransientRepository;
import org.cloudcog.gears.sdk.repository.user.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;

public class RepositoryContext {

	private static final Logger log = LoggerFactory.getLogger(RepositoryContext.class);

	public static final String SESSION_PARAM = "jcrSession";
	
	private static Repository repository;
	// private static Session anonymousSession;

	public static Session login(String username, String password) throws LoginException, RepositoryException, FileNotFoundException, ParseException, IOException {
		log.info("Logging user: " + username);

		Session session = RepositoryContext.getRepository().login(new SimpleCredentials(username, password.toCharArray()));
		InputStream inputStream = RepositoryContext.class.getResourceAsStream("/org/cloudcog/gears/repository/data/nodeTypes.cnd");
		Reader reader = new InputStreamReader(inputStream);
		CndImporter.registerNodeTypes(reader, session);
		// TODO should importer be here at all?
		if(session != null) {
			RepositoryContext.setJcrSession(session);
		}

		return session;
	}
	
	public static void destroyJcrSession() {
		Session session = (Session) VaadinService.getCurrentRequest().getWrappedSession().getAttribute(SESSION_PARAM);
		if (session != null) {
			session.logout();
			VaadinService.getCurrentRequest().getWrappedSession().removeAttribute(SESSION_PARAM);
		}
        VaadinSession.getCurrent().close();
	}

	public static Boolean isAuthorized() {
		Session session = (Session) VaadinService.getCurrentRequest().getWrappedSession().getAttribute(SESSION_PARAM);
		if (session == null || UserDAO.ANONIMOUS_USER.equals(session.getUserID())) {
			return false;
		} else {
			return true;
		}
	}

	public static void setJcrSession(Session session) {
		Session existingSession = (Session) VaadinService.getCurrentRequest().getWrappedSession().getAttribute(SESSION_PARAM);
		if (existingSession != null) {
			existingSession.logout();
		}
		VaadinService.getCurrentRequest().getWrappedSession().setAttribute(SESSION_PARAM, session);
	}

	public static Locale getSessionLocale() {
		return VaadinSession.getCurrent().getLocale();
	}

	public static Session getJcrSession() throws LoginException, RepositoryException {
		Session session = (Session) VaadinService.getCurrentRequest().getWrappedSession().getAttribute(SESSION_PARAM);
		if (session == null) {
			session = RepositoryContext.createAnonymusSession();
			VaadinService.getCurrentRequest().getWrappedSession().setAttribute(SESSION_PARAM, session);
		} else if (!session.isLive()) {
			VaadinService.getCurrentRequest().getWrappedSession().invalidate();
		}
		return session;
	}

	public static Session impersonate(String user) throws LoginException, RepositoryException {
		Session session = RepositoryContext.getJcrSession().impersonate(new SimpleCredentials(user, new char[0]));

		VaadinService.getCurrentRequest().getWrappedSession().setAttribute(SESSION_PARAM, session);
		return session;
	}

	public static Repository getRepository() throws RepositoryException {
		if (repository == null) {
			repository = new TransientRepository(); 
		}
		return repository;
	}

	public static Session createAnonymusSession() throws LoginException, RepositoryException {
		log.info("Using guest credentials");
		return getRepository().login(new GuestCredentials());
	}

}
