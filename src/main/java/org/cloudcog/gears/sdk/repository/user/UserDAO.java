package org.cloudcog.gears.sdk.repository.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.apache.jackrabbit.value.StringValue;

public class UserDAO {

	public static final String ANONIMOUS_USER = "anonymous";
	private static final String PERMISSIONS_PROP = "permissions";

	public static User  getCurrentUser(Session session) throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
		JackrabbitSession js = (JackrabbitSession) session;
		User user = (User) js.getUserManager().getAuthorizable(session.getUserID());
		if (user == null) {
			throw new RepositoryException("User is null");
		}
		return user;
	}

	public static User getUser(Session session, String username)
			throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
		JackrabbitSession js = (JackrabbitSession) session;
		User user = (User) js.getUserManager().getAuthorizable(username);
		if (user == null) {
			throw new RepositoryException("User is null");
		}
		return user;
	}

	public static List<User> getAllUsers(Session session) throws RepositoryException {
		final List<User> users = new ArrayList<User>();

		final UserManager userManager = ((JackrabbitSession) session).getUserManager();
		Iterator<Authorizable> iter = userManager.findAuthorizables("jcr:primaryType", "rep:User");

		while (iter.hasNext()) {
			Authorizable auth = iter.next();
			if (!auth.isGroup() && !ANONIMOUS_USER.equalsIgnoreCase(auth.getPrincipal().getName())) {
				users.add((User) auth);
			}
		}

		return users;
	}

	public static void changePassword(User user, String password, Session session) throws RepositoryException {
		UserManager userManager = ((JackrabbitSession) session).getUserManager();
        Authorizable authorizable = userManager.getAuthorizable(user.getID());

        ((User) authorizable).changePassword(password);

	}

	public static List<Group> getAllGroups(Session session) throws RepositoryException {
		final List<Group> groups = new ArrayList<Group>();

		final UserManager userManager = ((JackrabbitSession) session).getUserManager();
		Iterator<Authorizable> iter = userManager.findAuthorizables("jcr:primaryType", "rep:User");

		while (iter.hasNext()) {
			Authorizable auth = iter.next();
			if (auth.isGroup()) {
				groups.add((Group) auth);
			}
		}

		return groups;
	}

	public static List<User> getAllUsersFromGroup(Session session, String groupName) throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
		final List<User> members = new ArrayList<User>();
		final UserManager userManager = ((JackrabbitSession) session).getUserManager();
		final Authorizable auth2 = userManager.getAuthorizable(groupName);
		if (auth2.isGroup()) {
			Iterator<Authorizable> iter = ((Group) auth2).getMembers();
			while (iter.hasNext()) {
				Authorizable auth = iter.next();
				if (!auth.isGroup()) {
					members.add((User) auth);
				}
			}
		}

		return members;
	}

	public static User createUser(Session session, String username, String password) throws AuthorizableExistsException, RepositoryException {
		final User user = ((JackrabbitSession) session).getUserManager().createUser(username, password);
		addPrivileges(session, user);
		session.save();
		return user;
	}

	private static void addPrivileges(Session session, User user) throws RepositoryException {
		AccessControlManager aMgr = session.getAccessControlManager();
		JackrabbitAccessControlList acl = AccessControlUtils.getAccessControlList(session, null);

		if (acl != null) {
			Privilege[] privileges = AccessControlUtils.privilegesFromNames(session, Privilege.JCR_ALL);

			acl.addEntry(user.getPrincipal(), privileges, true);
			aMgr.setPolicy(null, acl);
		}

	}

	public static Group createGroup(Session session, String groupName) throws AuthorizableExistsException, RepositoryException {
		final Group group = ((JackrabbitSession) session).getUserManager().createGroup(groupName);
		session.save();
		return group;
	}

	public static void addUserToGroup(Session session, Group group, User user) throws AuthorizableExistsException, RepositoryException {
		group.addMember(user);
		session.save();
	}

	public static boolean userHasPermission(Session session, User user, String... permissions) throws RepositoryException {
		if (user.isAdmin()) {
			return true;
		} else {
			Iterator<Group> groupsIterator = user.declaredMemberOf();
			while (groupsIterator.hasNext()) {
				Group group = groupsIterator.next();
				if (goupHasPermissions(group, permissions)) {
					return true;
				}
			}
			return false;
		}
	}

	public static boolean goupHasPermissions(Group group, String... permissions) throws RepositoryException {
		StringValue[] values = (StringValue[]) group.getProperty(PERMISSIONS_PROP);
		System.out.println(values.toString());
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < permissions.length; j++) {
				if (values[i].getString().equalsIgnoreCase(permissions[j])) {
					return true;
				}
			}
		}
		return false;
	}

	public static void removeUser(Session session, String username) throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
		final UserManager userManager = ((JackrabbitSession) session).getUserManager();
		final User user = (User) userManager.getAuthorizable(username);
		user.remove();
		session.save();
	}

	public static void removeGroup(Session session, String groupName) throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
		final UserManager userManager = ((JackrabbitSession) session).getUserManager();
		final Group group = (Group) userManager.getAuthorizable(groupName);
		group.remove();
		session.save();
	}
}
