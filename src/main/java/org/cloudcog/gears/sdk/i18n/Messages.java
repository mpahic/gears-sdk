package org.cloudcog.gears.sdk.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.cloudcog.gears.sdk.RepositoryContext;


/**
 * 
 * @author mpahic
 * 
 */
public class Messages {

	private static Map<Locale, ResourceBundle> resourceMap = new HashMap<Locale, ResourceBundle>();
	private static final String BUNDLE_NAME = "org.cloudcog.gears.i18n.messages"; //$NON-NLS-1$

	private Messages() {
	}

	private static ResourceBundle getBundle(Locale locale) {
		ResourceBundle bundle = resourceMap.get(locale);
		if (bundle == null) {
			try {
				bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
			} catch (MissingResourceException e) {
				bundle = ResourceBundle.getBundle(BUNDLE_NAME);
			}
			resourceMap.put(locale, bundle);
		}
		return bundle;
	}

	public static String getString(String key) {
		try {
			return Messages.getBundle(RepositoryContext.getSessionLocale()).getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static String getString(String key, String strings) {
		try {
			return String.format(Messages.getBundle(RepositoryContext.getSessionLocale()).getString(key), strings);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

}
