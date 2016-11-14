/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

/** Singleton implementations of zero for each number type. */
public class Zeros {
	static final Double DOUBLE = Double.valueOf(0.0);
	static final Float FLOAT = Float.valueOf(0.0f);

	static final Long LONG = Long.valueOf(0l);
	static final Integer INTEGER = Integer.valueOf(0);
	static final Short SHORT = Short.valueOf((short) 0);
	static final Byte BYTE = Byte.valueOf((byte) 0);
}
