/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

public class MLUInt16 extends MLInt16 {

	public MLUInt16(String name, int[] dims, int type, int attributes) {
		super(name, dims, type, attributes);
	}

	public MLUInt16(String name, Short[] vals, int m) {
		super(name, vals, m);
	}

	public MLUInt16(String name, int[] dims) {
		super(name, dims);
	}

	public MLUInt16(String name, short[][] vals) {
		super(name, vals);
	}

	public MLUInt16(String name, short[] vals, int m) {
		super(name, vals, m);
	}
}
