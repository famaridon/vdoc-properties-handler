package com.famaridon.vdoc.scoped.properties.handler;

import com.famaridon.maven.scoped.properties.annotations.CustomHandler;
import com.famaridon.maven.scoped.properties.beans.ScopedPropertiesConfiguration;
import com.famaridon.maven.scoped.properties.beans.properties.Property;
import com.famaridon.maven.scoped.properties.exceptions.BuildPropertiesFilesException;
import com.famaridon.maven.scoped.properties.extension.interfaces.ScopedPropertiesHandler;
import com.famaridon.vdoc.scoped.properties.handler.beans.ConfigurationUtilityClassFileConfiguration;
import com.famaridon.vdoc.scoped.properties.handler.beans.ConfigurationUtilityClassPropertyConfiguration;
import com.famaridon.vdoc.scoped.properties.handler.tools.ApacheCommonsClass;
import com.famaridon.vdoc.scoped.properties.handler.tools.JavaClass;
import com.famaridon.vdoc.scoped.properties.handler.tools.VDocClass;
import com.sun.codemodel.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by famaridon on 19/08/2014.
 */
@CustomHandler(shortName = "VDocUtilityClass")
public class ConfigurationUtilityClassHandler implements ScopedPropertiesHandler<ConfigurationUtilityClassFileConfiguration, ConfigurationUtilityClassPropertyConfiguration> {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationUtilityClassHandler.class);

	public static final char[] KEY_DELIMITERS = new char[]{'.', ' '};
	public static final String KEY_DELIMITERS_REGEX = "\\.* *";

	protected ScopedPropertiesConfiguration configuration;
	protected ConfigurationUtilityClassFileConfiguration fileHandlerConfiguration;
	protected File currentFile;

	protected JCodeModel cm;
	protected JDefinedClass c;
	protected JFieldVar missingConfigurationMessage;
	protected JFieldVar invalidConfigurationMessage;

	private VDocClass vdoc;
	private JavaClass java;
	private ApacheCommonsClass apache;


	/**
	 * call at the begin of each .properties.xml parsing
	 *
	 * @param configuration the scoped properties input configuration.
	 * @param currentFile   the current parsing .properties.xml file
	 */
	@Override
	public void startDocument(ScopedPropertiesConfiguration configuration, ConfigurationUtilityClassFileConfiguration fileHandlerConfiguration, File currentFile) throws BuildPropertiesFilesException {
		this.configuration = configuration;
		this.fileHandlerConfiguration = fileHandlerConfiguration;
		this.currentFile = currentFile;
		cm = new JCodeModel();
		// create all types
		vdoc = new VDocClass(cm);
		java = new JavaClass(cm);
		apache = new ApacheCommonsClass(cm);

		try {
			c = cm._class(JMod.PUBLIC, fileHandlerConfiguration.getGeneratedFullyQualifiedName(), ClassType.CLASS);
			JClass baseConfigurationClass = cm.directClass(fileHandlerConfiguration.getBaseClassName());
			c._extends(baseConfigurationClass);
			// create the default private constructor
			c.constructor(JMod.PROTECTED);

			JDefinedClass customizableClass = cm._class(JMod.PUBLIC, fileHandlerConfiguration.getCustomizableFullyQualifiedName(), ClassType.CLASS);
			customizableClass._extends(c);
			customizableClass.constructor(JMod.PRIVATE);

		} catch (JClassAlreadyExistsException e) {
			throw new BuildPropertiesFilesException("Can't create the class '" + fileHandlerConfiguration.getCustomizableFullyQualifiedName() + "' for file '" + currentFile.getName() + "'!", e);
		}

		// create the static final string
		missingConfigurationMessage = c.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class, "MISSING_CONFIGURATION_MESSAGE", JExpr.lit("Mandatory property '%1$s' is missing in file '" + currentFile.getName() + "'!"));
		invalidConfigurationMessage = c.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class, "BAD_CONFIGURATION_MESSAGE", JExpr.lit("Property '%1$s' is not valid in file '" + currentFile.getName() + "'!" + currentFile.getName() + "'!"));


	}

	/**
	 * call at any property add
	 *
	 * @param property
	 */
	@Override
	public void startProperty(Property property, ConfigurationUtilityClassPropertyConfiguration propertyHandlerConfiguration) throws BuildPropertiesFilesException {

		// if no configuration found skip this property.
		if (propertyHandlerConfiguration == null) {
			LOG.warn("Skip property {} in file {} no handler configuration found!",property.getName(), this.currentFile.getName());
			return;
		}

		switch (propertyHandlerConfiguration.getType()) {
			// all java type
			case string:
				appendStringGetter(property, propertyHandlerConfiguration);
				break;

			// all directory elements
			case organization:
				appendUriGetter(property, propertyHandlerConfiguration, vdoc.iOrganization, vdoc.iDirectoryModule);
				break;
			case localization:
				appendUriGetter(property, propertyHandlerConfiguration, vdoc.iLocalization, vdoc.iDirectoryModule);
				break;
			case group:
				appendUriGetter(property, propertyHandlerConfiguration, vdoc.iGroup, vdoc.iDirectoryModule);
				break;
			case user:
				appendUriGetter(property, propertyHandlerConfiguration, vdoc.iUser, vdoc.iDirectoryModule);
				break;

			// all project elements
			case project:
				appendUriGetter(property, propertyHandlerConfiguration, vdoc.iProject, vdoc.iProjectModule);
				break;

			// all workflows elements
			case catalog:
				appendUriGetter(property, propertyHandlerConfiguration, vdoc.iCatalog, vdoc.iWorkflowModule);
				break;
			case workflow:
				appendUriGetter(property, propertyHandlerConfiguration, vdoc.iWorkflow, vdoc.iWorkflowModule);
				break;
			case workflowContainer:
				appendUriGetter(property, propertyHandlerConfiguration, vdoc.iWorkflowContainer, vdoc.iWorkflowModule);
				break;
			case resourceDefinition:
				appendUriGetter(property, propertyHandlerConfiguration, vdoc.iResourceDefinition, vdoc.iWorkflowModule);
				break;
			case workflowInstance:
				appendUriGetter(property, propertyHandlerConfiguration, vdoc.iWorkflowInstance, vdoc.iWorkflowModule);
				break;
			case storageResource:
				appendUriGetter(property, propertyHandlerConfiguration, vdoc.iStorageResource, vdoc.iWorkflowModule);
				break;
		}

	}

	private void appendStringGetter(Property property, ConfigurationUtilityClassPropertyConfiguration propertyHandlerConfiguration) {
		JMethod getter = this.buildGetterMethod(property,propertyHandlerConfiguration, java.stringType);

		JVar value = appendValueGet(property, propertyHandlerConfiguration, getter);
		getter.body()._return(value);
	}

	private void appendUriGetter(Property property, ConfigurationUtilityClassPropertyConfiguration propertyHandlerConfiguration, JClass objectType, JClass moduleType) {
		JMethod getter = this.buildGetterMethod(property,propertyHandlerConfiguration, objectType);
		JVar module = getter.param(JMod.FINAL, moduleType, "module");

		// the protocol uri
		JVar value = appendValueGet(property, propertyHandlerConfiguration, getter);

		// the exception message used only if needed
		JInvocation message = java.stringType.staticInvoke("format").arg(invalidConfigurationMessage).arg(property.getName());

		// the try part
		JTryBlock theTry = getter.body()._try();

		JBlock uriGetBlock = null;
		JVar object;
		// the mandatory case value can't be null.
		if (propertyHandlerConfiguration.isMandatory()) {
			uriGetBlock = theTry.body();
			object = uriGetBlock.decl(objectType, "object", JExpr.cast(objectType, module.invoke("getElementByProtocolURI").arg(value)));
		} else {
			object = theTry.body().decl(objectType, "object", JExpr._null());
			JConditional conditional = theTry.body()._if(apache.stringUtils.staticInvoke("isNotEmpty").arg(value));
			uriGetBlock = conditional._then();
			uriGetBlock.assign(object, JExpr.cast(objectType, module.invoke("getElementByProtocolURI").arg(value)));
		}

		JConditional conditional = uriGetBlock._if(object.eq(JExpr._null()));
		conditional._then()._throw(JExpr._new(java.illegalArgumentExceptionClass).arg(message));

		theTry.body()._return(object);

		// the catch part
		JCatchBlock theCatch = theTry._catch(vdoc.modulesException);
		JVar exception = theCatch.param("e");
		theCatch.body()._throw(JExpr._new(java.illegalArgumentExceptionClass).arg(message).arg(exception));
	}

	private JVar appendValueGet(Property property, ConfigurationUtilityClassPropertyConfiguration propertyHandlerConfiguration, JMethod getter) {
		JVar value = getter.body().decl(java.stringType, "value", JExpr.invoke("getConfiguration").invoke("getStringProperty").arg(property.getName()));
		if (propertyHandlerConfiguration.isMandatory()) {
			getter.body().staticInvoke(apache.validate, "notEmpty").arg(value).arg(java.stringType.staticInvoke("format").arg(missingConfigurationMessage).arg(property.getName()));
		}
		return value;
	}

	private JMethod buildGetterMethod(Property property,ConfigurationUtilityClassPropertyConfiguration propertyHandlerConfiguration, JClass type) {
		String getterName;
		if(StringUtils.isEmpty(propertyHandlerConfiguration.getCustomGetterName()))
		{
			getterName = "get" + WordUtils.capitalizeFully(property.getName(), KEY_DELIMITERS);
			getterName = getterName.replaceAll(KEY_DELIMITERS_REGEX, "");
		}
		else
		{
			getterName = propertyHandlerConfiguration.getCustomGetterName();
		}

		return c.method(JMod.PUBLIC | JMod.STATIC, type, getterName);
	}

	/**
	 * call after any property add
	 *
	 * @param property
	 */
	@Override
	public void endProperty(Property property, ConfigurationUtilityClassPropertyConfiguration propertyHandlerConfiguration) throws BuildPropertiesFilesException {
		// not used
	}

	/**
	 * call at the end of each .properties.xml parsing
	 *
	 * @param fileBaseName
	 */
	@Override
	public File endDocument(String fileBaseName) throws BuildPropertiesFilesException {
		try {
			cm.build(this.configuration.getOutputFolder());
		} catch (IOException e) {
			throw new BuildPropertiesFilesException(e);
		}
		return null;
	}
}
