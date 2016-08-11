package org.cloudcog.gears.sdk;

import java.io.InputStreamReader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class RepositoryInitializer implements ServletContextListener {

	private static final Logger log = LoggerFactory.getLogger(RepositoryInitializer.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			CndImporter.registerNodeTypes(
					new InputStreamReader(
							this.getClass().getResourceAsStream("/org/cloudcog/gears/repository/data/nodeTypes.cnd")),
					RepositoryContext.getJcrSession());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
