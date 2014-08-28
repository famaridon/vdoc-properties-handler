package com.famaridon.vdoc.scoped.properties.handler.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by famaridon on 26/08/2014.
 */
@XmlRootElement
public class ConfigurationUtilityClassPropertyConfiguration {

	private VDocPropertyTypes type;
	private boolean mandatory;
	private String customGetterName;

	@XmlAttribute
	public VDocPropertyTypes getType() {
		return type;
	}

	public void setType(VDocPropertyTypes type) {
		this.type = type;
	}

	@XmlAttribute
	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	@XmlAttribute
	public String getCustomGetterName() {
		return customGetterName;
	}

	public void setCustomGetterName(String customGetterName) {
		this.customGetterName = customGetterName;
	}
}
