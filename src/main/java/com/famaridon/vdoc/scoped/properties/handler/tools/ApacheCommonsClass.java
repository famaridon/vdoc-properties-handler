package com.famaridon.vdoc.scoped.properties.handler.tools;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

/**
 * Created by famaridon on 28/08/2014.
 */
public class ApacheCommonsClass {

	public final JClass validate;
	public final JClass stringUtils;

	public ApacheCommonsClass(JCodeModel cm) {
		validate = cm.directClass("org.apache.commons.lang.Validate");
		stringUtils = cm.directClass("org.apache.commons.lang.StringUtils");
	}
}
