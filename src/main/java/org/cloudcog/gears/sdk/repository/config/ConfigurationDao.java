package org.cloudcog.gears.sdk.repository.config;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class ConfigurationDao {

	public static void saveConfiguration(Session session, String path, String value) throws RepositoryException {
		Node root = session.getRootNode();
        Node configurationNode = null;
		if(root.hasNode(path)) {
			configurationNode = root.addNode(path); 
		} else {
			configurationNode = root.getNode(path);
		}
		configurationNode.setProperty("value", value); 
        session.save(); 
		
	}

	public static void saveConfiguration(Session session, String path, Integer value) throws RepositoryException {
		Node root = session.getRootNode();
        Node configurationNode = null;
		if(root.hasNode(path)) {
			configurationNode = root.addNode(path); 
		} else {
			configurationNode = root.getNode(path);
		}
		configurationNode.setProperty("value", value); 
        session.save(); 
		
	}

	public static void saveConfiguration(Session session, String path, List<String> value) throws RepositoryException {
		Node root = session.getRootNode();
        Node configurationNode = null;
		if(root.hasNode(path)) {
			configurationNode = root.addNode(path); 
		} else {
			configurationNode = root.getNode(path);
		}
		configurationNode.setProperty("value", value.toArray(new String[value.size()])); 
        session.save(); 
		
	}

}
