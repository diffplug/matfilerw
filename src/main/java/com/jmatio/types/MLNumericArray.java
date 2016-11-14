/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Abstract class for numeric arrays.
 * 
 * @author Wojciech Gradkowski <wgradkowski@gmail.com>
 *
 * @param <T>
 */
/**
 * @author Wojciech Gradkowski <wgradkowski@gmail.com>
 *
 * @param <T>
 */
public abstract class MLNumericArray<T extends Number> extends MLArray implements
		ByteStorageSupport<T> {
	private ByteBuffer real;
	private ByteBuffer imaginary;
	/** The buffer for creating Number from bytes */
	private byte[] bytes;

	/**
	 * Normally this constructor is used only by MatFileReader and MatFileWriter
	 * 
	 * @param name - array name
	 * @param dims - array dimensions
	 * @param type - array type
	 * @param attributes - array flags
	 */
	public MLNumericArray(String name, int[] dims, int type, int attributes) {
		super(name, dims, type, attributes);
		allocate();
	}

	protected void allocate() {
		real = ByteBuffer.allocate(getSize() * getBytesAllocated());
		if (isComplex()) {
			imaginary = ByteBuffer.allocate(getSize() * getBytesAllocated());
		}
		bytes = new byte[getBytesAllocated()];
	}

	/** Returns the value of "zero" for this type of array. */
	protected abstract T zero();

	/**
	 * <a href="http://math.nist.gov/javanumerics/jama/">Jama</a> [math.nist.gov] style: 
	 * construct a 2D real matrix from a one-dimensional packed array
	 * 
	 * @param name - array name
	 * @param type - array type
	 * @param vals - One-dimensional array of doubles, packed by columns (ala Fortran).
	 * @param m - Number of rows
	 */
	public MLNumericArray(String name, int type, T[] vals, int m) {
		this(name, new int[]{m, vals.length / m}, type, 0);
		//fill the array
		for (int i = 0; i < vals.length; i++) {
			set(vals[i], i);
		}
	}

	/** Gets a single real array element. */
	public T getReal(int index) {
		return _get(real, index);
	}

	/** Gets a single real array element. */
	public T getReal(int m, int n) {
		return getReal(getIndex(m, n));
	}

	/** Gets a single real array element. */
	public T getReal(int... indices) {
		return getReal(getIndex(indices));
	}

	/** Sets a single real array element. */
	public void setReal(T value, int index) {
		_set(real, value, index);
	}

	/** Sets a single real array element. */
	public void setReal(T value, int m, int n) {
		setReal(value, getIndex(m, n));
	}

	/** Sets a single real array element. */
	public void setReal(T value, int... indices) {
		setReal(value, getIndex(indices));
	}

	/** Sets real part of a matrix. */
	public void setReal(T[] vector) {
		if (vector.length != getSize()) {
			throw new IllegalArgumentException("Matrix dimensions do not match. " + getSize() + " not " + vector.length);
		}
		System.arraycopy(vector, 0, real, 0, vector.length);
	}

	/** Sets a single imaginary array element. */
	public void setImaginary(T value, int index) {
		assertComplex();
		_set(imaginary, value, index);
	}

	/** Sets a single imaginary array element. */
	public void setImaginary(T value, int m, int n) {
		setImaginary(value, getIndex(m, n));
	}

	/** Sets a single imaginary array element. */
	public void setImaginary(T value, int... indices) {
		setImaginary(value, getIndex(indices));
	}

	/** Returns the imaginary value at the given index, always 0 for non-complex arrays. */
	public T getImaginary(int index) {
		if (isComplex()) {
			return _get(imaginary, index);
		} else {
			return zero();
		}
	}

	/** Returns the imaginary value at the given index. */
	public T getImaginary(int m, int n) {
		return getImaginary(getIndex(m, n));
	}

	/** Returns the imaginary value at the given index. */
	public T getImaginary(int... indices) {
		return getImaginary(getIndex(indices));
	}

	protected void assertComplex() {
		if (!isComplex()) {
			throw new UnsupportedOperationException("Cannot use this method for non-Complex matrices");
		}
	}

	/** Sets the value at the given index for non-complex arrays. */
	public void set(T value, int index) {
		setReal(value, index);
	}

	/** Sets the value at the given index for non-complex arrays. */
	public void set(T value, int m, int n) {
		set(value, getIndex(m, n));
	}

	/** Sets the value at the given index for non-complex arrays. */
	public void set(T value, int... indices) {
		set(value, getIndex(indices));
	}

	/** Returns the value at the given index for non-complex arrays. */
	public T get(int index) {
		return getReal(index);
	}

	/** Returns the value at the given index for non-complex arrays. */
	public T get(int m, int n) {
		return get(getIndex(m, n));
	}

	/** Returns the value at the given index for non-complex arrays. */
	public T get(int... indices) {
		return get(getIndex(indices));
	}

	/** Sets the content of this entire array for non-complex arrays. */
	public void set(T[] vector) {
		if (vector.length != getSize()) {
			throw new IllegalArgumentException("Matrix dimensions do not match. " + getSize() + " not " + vector.length);
		}
		System.arraycopy(vector, 0, real, 0, vector.length);
	}

	private int getByteOffset(int index) {
		return index * getBytesAllocated();
	}

	protected T _get(ByteBuffer buffer, int index) {
		buffer.position(getByteOffset(index));
		buffer.get(bytes, 0, bytes.length);
		return buldFromBytes(bytes);
	}

	protected void _set(ByteBuffer buffer, T value, int index) {
		buffer.position(getByteOffset(index));
		buffer.put(getByteArray(value));
	}

	public void putImaginaryByteBuffer(ByteBuffer buff) {
		if (!isComplex()) {
			throw new RuntimeException("Array is not complex");
		}
		imaginary.rewind();
		imaginary.put(buff);
	}

	public ByteBuffer getImaginaryByteBuffer() {
		return imaginary;
	}

	public void putRealByteBuffer(ByteBuffer buff) {
		real.rewind();
		real.put(buff);
	}

	public ByteBuffer getRealByteBuffer() {
		return real;
	}

	@Override
	public String contentToString() {
		if (getSize() > 1000) {
			return "Cannot display variables with more than 1000 elements.";
		}
		StringBuilder sb = new StringBuilder();
		for (int m = 0; m < getM(); m++) {
			for (int n = 0; n < getN(); n++) {
				sb.append(getReal(m, n));
				if (isComplex()) {
					sb.append("+" + getImaginary(m, n));
				}
				sb.append("\t");
			}
			sb.append("\n");
		}
		// don't let it end in \t\n
		if (sb.length() > 2) {
			sb.setLength(sb.length() - 2);
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MLNumericArray) {
			boolean result = directByteBufferEquals(real, ((MLNumericArray<?>) o).real)
					&& Arrays.equals(dims, ((MLNumericArray<?>) o).dims);
			if (isComplex() && result) {
				result &= directByteBufferEquals(imaginary, ((MLNumericArray<?>) o).imaginary);
			}
			return result;
		}
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		if (isComplex()) {
			return Arrays.hashCode(new Object[]{real, dims, imaginary});
		} else {
			return Arrays.hashCode(new Object[]{real, dims});
		}
	}

	/**
	 * Equals implementation for direct <code>ByteBuffer</code>
	 * 
	 * @param buffa the source buffer to be compared
	 * @param buffb the destination buffer to be compared
	 * @return <code>true</code> if buffers are equal in terms of content
	 */
	private static boolean directByteBufferEquals(ByteBuffer buffa, ByteBuffer buffb) {
		if (buffa == buffb) {
			return true;
		}

		if (buffa == null || buffb == null) {
			return false;
		}

		buffa.rewind();
		buffb.rewind();

		int length = buffa.remaining();

		if (buffb.remaining() != length) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			if (buffa.get() != buffb.get()) {
				return false;
			}
		}

		return true;
	}

	public void dispose() {
		if (real != null) {
			real.clear();
		}
		if (imaginary != null) {
			real.clear();
		}
	}
}
