/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

/**
 * We have observed a need for this class in a proprietary model.
 * It is [not documented in the MAT-File spec](http://stackoverflow.com/questions/36025747/matlab-documentation-on-handle-variables-and-mat-files),
 * and we can't figure out how to make it happen ourselves.  In
 * the testsuite (`TestHandleClass.m` and `handles.mat`) we have
 * made our best effort at creating a handle class which triggers
 * this code, but these test cases do not actually trigger this code.
 * 
 * Highly experimental until MATLAB releases docs on this behavior.
 */
public class MLHandle extends MLArray {
	private final String className;
	private final MLCell content;

	public MLHandle(String name, String className, MLCell content) {
		super(name, new int[]{1, 1}, MLArray.mxOBJECT_CLASS, 0);
		this.className = className;
		this.content = content;
	}

	public String getClassName() {
		return className;
	}

	public MLCell getContent() {
		return content;
	}
}
