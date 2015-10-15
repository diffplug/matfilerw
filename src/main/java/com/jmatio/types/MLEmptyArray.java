/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

public class MLEmptyArray extends MLArray {
	public MLEmptyArray() {
		this(null);
	}

	public MLEmptyArray(String name) {
		this(name, new int[]{0, 0}, mxDOUBLE_CLASS, 0);
	}

	public MLEmptyArray(String name, int[] dims, int type, int attributes) {
		super(name, dims, type, attributes);
		// TODO Auto-generated constructor stub
	}

}
