package org.cloudcog.kani.services;

import com.vaadin.ui.Component;

public interface Module {

	public String getName();

	public Component createComponent();

}
