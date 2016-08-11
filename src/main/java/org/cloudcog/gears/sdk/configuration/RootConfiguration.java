package org.cloudcog.gears.sdk.configuration;

public class RootConfiguration extends AbstractConfiguration {

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public CONFIGURATION_TYPE getType() {
		return CONFIGURATION_TYPE.GROUP;
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public String getIdentifierName() {
		return "configurations";
	}

	@Override
	public String getName() {
		return null;
	}

	
}
