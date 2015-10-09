package org.cloudcog.gears.sdk.services;

import com.vaadin.ui.Component;

public interface Module {

	public String getName();

	public Component createComponent();

}
