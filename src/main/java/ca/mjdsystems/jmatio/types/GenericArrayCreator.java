/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package ca.mjdsystems.jmatio.types;

@Deprecated
public interface GenericArrayCreator<T> {
	T[] createArray(int m, int n);

}
