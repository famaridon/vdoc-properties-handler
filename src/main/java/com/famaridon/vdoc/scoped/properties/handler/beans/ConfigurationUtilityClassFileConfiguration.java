package com.famaridon.vdoc.scoped.properties.handler.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by famaridon on 26/08/2014.
 */
@XmlRootElement
public class ConfigurationUtilityClassFileConfiguration {

	private String generationPackage;
	private String className;

	@XmlElement
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@XmlElement(required = true)
	public String getGenerationPackage() {
		return generationPackage;
	}

	public void setGenerationPackage(String generationPackage) {
		this.generationPackage = generationPackage;
	}

	public String getCustomizableFullyQualifiedName() {
		return generationPackage + "." + className;
	}

	public String getGeneratedFullyQualifiedName() {
		return generationPackage + ".generated." + className;
	}

	public String getBaseClassName() {
		return generationPackage + ".BaseConfiguration";
	}
}
