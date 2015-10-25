/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

import java.util.Map;

/**
 * This class represents Matlab's Object object (object array).
 * 
 * Note: array of structures can contain only structures of the same type
 * , that means structures that have the same field names.
 * 
 * @author Wojciech Gradkowski <wgradkowski@gmail.com>
 */
public class MLObject extends MLStructureObjectBase {
	private final String className;

	public MLObject(String name, String className, int[] dimensions, int attributes) {
		super(name, dimensions, MLArray.mxOBJECT_CLASS, 0);
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setFields(int i, Map<String, MLArray> structure) {
		mlStructArray.put(i, structure);
	}
}
