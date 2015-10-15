/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

public class MLObject extends MLArray {
	private final MLStructure o;
	private final String className;

	public MLObject(String name, String className, MLStructure o) {
		super(name, new int[]{1, 1}, MLArray.mxOBJECT_CLASS, 0);
		this.o = o;
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public MLStructure getObject() {
		return o;
	}
}
