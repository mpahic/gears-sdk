package org.cloudcog.gears.sdk.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.cloudcog.gears.sdk.repository.config.ConfigurationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfiguration {
	
	public static final Logger log = LoggerFactory.getLogger(AbstractConfiguration.class);

	public enum CONFIGURATION_TYPE {STRING, INTEGER, STRING_ARRAY, GROUP};
	
	private List<AbstractConfiguration> childConfigurations = null;
	private AbstractConfiguration parentConfig = null;
	private Object value = null;
	
	public abstract Object getDefaultValue();
	public abstract CONFIGURATION_TYPE getType();
	public abstract String getIdentifierName();
	public abstract String getName();

	public Object getValue() {
		if(value == null) {
			return getDefaultValue();
		} else {
			return value;
		}
	}
	
	public List<AbstractConfiguration> getChildConfigurations() {
		return childConfigurations;
	}
	
	public void addChildConfiguration(AbstractConfiguration childConfiguration) {
		if(this.childConfigurations == null) {
			this.childConfigurations = new ArrayList<AbstractConfiguration>();
		}
		this.childConfigurations.add(childConfiguration);
		childConfiguration.setParentConfig(this);
	}
	
	private void setParentConfig(AbstractConfiguration parentConfig) {
		this.parentConfig = parentConfig;
	}
	
	public AbstractConfiguration getParentConfig() {
		return parentConfig;
	}
	
	public void setValue(Session session, Object value) throws RepositoryException {
		String path = getConfigPath();
		switch (getType()) {
		case STRING:
			if(value instanceof String) {
				ConfigurationDao.saveConfiguration(session, path, (String) value);
			} else {
				log.error("Configuration value is not a String");
			}
			break;
		case INTEGER:
			if(value instanceof Integer) {
				ConfigurationDao.saveConfiguration(session, path, (Integer) value);
			} else {
				log.error("Configuration value is not a Integer");
			}
			break;
		case STRING_ARRAY:
			if(value instanceof List<?>) {
				ConfigurationDao.saveConfiguration(session, path, (List) value);
			} else {
				log.error("Configuration value is not a List");
			}
			break;
		case GROUP:
			if(value instanceof String) {
				ConfigurationDao.saveConfiguration(session, path, (String) value);
			} else {
				log.error("Configuration value of group is not a String");
			}
			break;
			

		default:
			log.warn("Type of configuratio nis not defined");
		}
				
	}
	
	public String getConfigPath() {
		if(parentConfig != null) {
			return parentConfig.getParentConfig() + getIdentifierName();
		} else {
			return getIdentifierName();
		}
	}
}
