/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

import com.jmatio.io.stream.ByteBufferInputStream;

@SuppressWarnings("rawtypes")
public class MLJavaObject extends MLArray {

	private final String className;
	private final MLNumericArray content;

	public MLJavaObject(String name, String className, MLNumericArray content) {
		super(name, new int[]{1, 1}, MLArray.mxOPAQUE_CLASS, 0);

		this.className = className;
		this.content = content;
	}

	public String getClassName() {
		return className;
	}

	public ByteBuffer getContent() {
		return content.getRealByteBuffer();
	}

	/** Attempts to instantiate the Java Object, and all kinds of stuff can go wrong. */
	public Object instantiateObject() throws Exception {
		// de-serialize object
		ObjectInputStream ois = new ObjectInputStream(
				new ByteBufferInputStream(content.getRealByteBuffer(), content.getRealByteBuffer().limit()));
		try {
			return ois.readObject();
		} finally {
			ois.close();
		}
	}
}
