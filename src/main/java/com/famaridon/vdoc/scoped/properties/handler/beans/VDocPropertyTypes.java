package com.famaridon.vdoc.scoped.properties.handler.beans;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Created by famaridon on 27/08/2014.
 */
@XmlEnum
public enum VDocPropertyTypes {
	// java type
	string,
	// directory
	organization, group, localization, user,
	// project
	project,
	// workflow
	catalog, workflow, workflowContainer, resourceDefinition,workflowInstance,storageResource
}
