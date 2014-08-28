package com.famaridon.vdoc.scoped.properties.handler.tools;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

/**
 * Created by famaridon on 28/08/2014.
 */
public class VDocClass {

	// Modules
	public final JClass iPortalModule;
	public final JClass iDirectoryModule;
	public final JClass iProjectModule;
	public final JClass iWorkflowModule;

	// Directory classes
	public final JClass iOrganization;
	public final JClass iGroup;
	public final JClass iLocalization;
	public final JClass iUser;

	// Project classes
	public final JClass iProject;

	// workflow classes

	public final JClass iCatalog;
	public final JClass iWorkflow;
	public final JClass iWorkflowContainer;
	public final JClass iResourceDefinition;
	public final JClass iWorkflowInstance;
	public final JClass iStorageResource;


	public final JClass iConfiguration;

	// Exceptions
	public final JClass modulesException;

	public VDocClass(JCodeModel cm) {

		iPortalModule = cm.directClass("com.axemble.vdoc.sdk.modules.IPortalModule");
		iDirectoryModule = cm.directClass("com.axemble.vdoc.sdk.modules.IDirectoryModule");
		iProjectModule = cm.directClass("com.axemble.vdoc.sdk.modules.IProjectModuleClass");
		iWorkflowModule = cm.directClass("com.axemble.vdoc.sdk.modules.IWorkflowModule");

		iOrganization = cm.directClass("com.axemble.vdoc.sdk.interfaces.IOrganization");
		iGroup = cm.directClass("com.axemble.vdoc.sdk.interfaces.IGroup");
		iLocalization = cm.directClass("com.axemble.vdoc.sdk.interfaces.ILocalization");
		iUser = cm.directClass("com.axemble.vdoc.sdk.interfaces.IUser");

		iProject = cm.directClass("com.axemble.vdoc.sdk.interfaces.IProject");

		iCatalog = cm.directClass("com.axemble.vdoc.sdk.interfaces.ICatalog");
		iWorkflow = cm.directClass("com.axemble.vdoc.sdk.interfaces.IWorkflow");
		iWorkflowContainer = cm.directClass("com.axemble.vdoc.sdk.interfaces.IWorkflowContainer");
		iResourceDefinition = cm.directClass("com.axemble.vdoc.sdk.interfaces.IResourceDefinition");
		iWorkflowInstance = cm.directClass("com.axemble.vdoc.sdk.interfaces.IWorkflowInstance");
		iStorageResource = cm.directClass("com.axemble.vdoc.sdk.interfaces.IStorageResource");

		iConfiguration = cm.directClass("com.axemble.vdoc.sdk.interfaces.IConfiguration");

		modulesException = cm.directClass("com.axemble.vdoc.sdk.exceptions.ModuleException");
	}
}
