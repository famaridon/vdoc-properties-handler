package com.famaridon.vdoc.scoped.properties.handler.tools;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

/**
 * Created by famaridon on 28/08/2014.
 */
public class JavaClass {

	// exceptions
	public final JClass illegalArgumentExceptionClass;

	// general classes
	public final JClass stringType;

	public JavaClass(JCodeModel cm) {
		illegalArgumentExceptionClass = cm.ref(IllegalArgumentException.class);
		stringType = cm.ref(String.class);
	}
}
