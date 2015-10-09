package org.cloudcog.gears.sdk.login;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.commons.cnd.ParseException;
import org.cloudcog.gears.sdk.RepositoryContext;
import org.cloudcog.gears.sdk.login.exception.LoginException;
import org.cloudcog.gears.sdk.ui.AbstractAuthorizableUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserSessionHandler {

	private static final Logger log = LoggerFactory.getLogger(UserSessionHandler.class);
	private List<ISessionListener> listeners = new ArrayList<ISessionListener>();

	public UserSessionHandler() {
		this.addListener((AbstractAuthorizableUI) AbstractAuthorizableUI.getCurrent());
	}

	public void loginUser(String username, String password) throws LoginException {

		try {
			Session session = RepositoryContext.login(username, password);
			RepositoryContext.setJcrSession(session);
			this.fireSessionCreatedEvent();
		} catch (javax.jcr.LoginException e) {
			throw new LoginException();
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private static String hash(String s) {
		try {
			MessageDigest m;
			m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			return new BigInteger(1, m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public void fireSessionCreatedEvent() {
		for (ISessionListener listener : listeners) {
			listener.sessionCreateHandler();
		}
	}

	public void fireSessionDeletedEvent() {
		for (ISessionListener listener : listeners) {
			listener.sessionDestroyHandler();
		}
	}

	public void addListener(ISessionListener listener) {
		this.listeners.add(listener);
	}
}
