package org.cloudcog.gears.sdk.services;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.cloudcog.gears.sdk.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GearServiceLoader {
	Logger log = LoggerFactory.getLogger(GearServiceLoader.class);
	
	private static GearServiceLoader service;

    private ServiceLoader<GearService> loader;
    
	private GearServiceLoader() {
        loader = ServiceLoader.load(GearService.class);
    }

	public static synchronized GearServiceLoader getInstance() {
		if (service == null) {
            service = new GearServiceLoader();
        }
        return service;
	}
	
	public AbstractConfiguration getConfigurations() {
		AbstractConfiguration configuration = null;

        try {
            Iterator<GearService> services = loader.iterator();
            while (services != null && services.hasNext()) {
            	GearService service = services.next();
            	configuration = service.getConfiguration();
            }
        } catch (ServiceConfigurationError e) {
        	configuration = null;
            log.error(e.getMessage(), e);

        }
        return configuration;
	}
	
}
