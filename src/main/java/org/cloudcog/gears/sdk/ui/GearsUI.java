package org.cloudcog.gears.sdk.ui;

import java.util.List;

import org.cloudcog.gears.sdk.login.ISessionListener;
import org.cloudcog.gears.sdk.services.CogService;

import com.vaadin.ui.UI;

public abstract class GearsUI  extends UI implements ISessionListener {
	private static final long serialVersionUID = 1046054214005681328L;

	public abstract List<Class<? extends CogService>> getModules();

}
