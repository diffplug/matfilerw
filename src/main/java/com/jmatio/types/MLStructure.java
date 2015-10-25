/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

/**
 * This class represents Matlab's Structure object (structure array).
 * 
 * Note: array of structures can contain only structures of the same type
 * , that means structures that have the same field names.
 * 
 * @author Wojciech Gradkowski <wgradkowski@gmail.com>
 */
public class MLStructure extends MLStructureObjectBase {
	public MLStructure(String name, int[] dims) {
		this(name, dims, 0);
	}

	public MLStructure(String name, int[] dims, int attributes) {
		super(name, dims, MLArray.mxSTRUCT_CLASS, attributes);
	}
}
