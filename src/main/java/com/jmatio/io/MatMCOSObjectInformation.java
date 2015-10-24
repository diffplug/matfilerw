/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.io;

import java.util.HashMap;
import java.util.Map;

import com.jmatio.types.MLArray;

/**
 *
 * @author Matthew Dawson <matthew@mjdsystems.ca>
 */
class MatMCOSObjectInformation {
	MatMCOSObjectInformation(String className, int classId, int objectId, int segment2PropertiesIndex, int segment4PropertiesIndex) {
		this.className = className;

		this.objectId = objectId;
		this.classId = classId;

		this.segment2PropertiesIndex = segment2PropertiesIndex;
		this.segment4PropertiesIndex = segment4PropertiesIndex;
	}

	final String className;
	final int objectId;
	final int classId;
	final int segment2PropertiesIndex;
	final int segment4PropertiesIndex;
	final Map<String, MLArray> structure = new HashMap<String, MLArray>();
}
