/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.io;

import com.jmatio.types.MLObject;

/**
 *
 * @author Matthew Dawson <matthew@mjdsystems.ca>
 */
class MLObjectPlaceholder extends MLObject {
	MLObjectPlaceholder(String name, String className, int[][] information) {
		super(name, className, new int[]{information[2][0], information[3][0]}, -1);

		this.objectIds = new int[information.length - 5];
		for (int i = 0; i < objectIds.length; ++i) {
			objectIds[i] = information[i + 4][0];
		}
		this.classId = information[information.length - 1][0];
	}

	final int[] objectIds;
	final int classId;

	void setTarget(MLObject obj) {
		copyFrom(obj);
	}
}
