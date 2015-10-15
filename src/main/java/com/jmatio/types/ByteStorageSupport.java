/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

public interface ByteStorageSupport<T extends Number> {
	int getBytesAllocated();

	T buldFromBytes(byte[] bytes);

	byte[] getByteArray(T value);

	Class<?> getStorageClazz();

}
