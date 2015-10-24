/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

public class MLJavaObject extends MLArray {
	private final Object o;
	private final String className;

	public MLJavaObject(String name, String className, Object o) {
		super(name, new int[]{1, 1}, MLArray.mxOPAQUE_CLASS, 0);
		this.o = o;
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public Object getObject() {
		return o;
	}

}
