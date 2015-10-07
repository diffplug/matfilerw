/*
 * Copyright (c) 2006, Wojciech Gradkowski
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 *    disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other materials provided with the distribution.
 *  - Neither the name of JMatIO nor the names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jmatio.types;

import java.nio.ByteBuffer;

/**
 * Class represents Int64 (long) array (matrix)
 * 
 * @author Wojciech Gradkowski <wgradkowski@gmail.com>
 */
public class MLUInt64 extends MLNumericArray<Long> {

	/**
	 * Normally this constructor is used only by MatFileReader and MatFileWriter
	 * 
	 * @param name - array name
	 * @param dims - array dimensions
	 * @param type - array type: here <code>mxDOUBLE_CLASS</code>
	 * @param attributes - array flags
	 */
	public MLUInt64(String name, int[] dims, int type, int attributes) {
		super(name, dims, type, attributes);
	}

	/**
	 * Create a <code>{@link MLUInt64}</code> array with given name,
	 * and dimensions.
	 * 
	 * @param name - array name
	 * @param dims - array dimensions
	 */
	public MLUInt64(String name, int[] dims) {
		super(name, dims, MLArray.mxUINT64_CLASS, 0);
	}

	/**
	 * <a href="http://math.nist.gov/javanumerics/jama/">Jama</a> [math.nist.gov] style: 
	 * construct a 2D real matrix from a one-dimensional packed array
	 * 
	 * @param name - array name
	 * @param vals - One-dimensional array of doubles, packed by columns (ala Fortran).
	 * @param m - Number of rows
	 */
	public MLUInt64(String name, Long[] vals, int m) {
		super(name, MLArray.mxUINT64_CLASS, vals, m);
	}

	/**
	 * <a href="http://math.nist.gov/javanumerics/jama/">Jama</a> [math.nist.gov] style: 
	 * construct a 2D real matrix from <code>byte[][]</code>
	 * 
	 * Note: array is converted to Byte[]
	 * 
	 * @param name - array name
	 * @param vals - two-dimensional array of values
	 */
	public MLUInt64(String name, long[][] vals) {
		this(name, long2DToLong(vals), vals.length);
	}

	/**
	 * <a href="http://math.nist.gov/javanumerics/jama/">Jama</a> [math.nist.gov] style: 
	 * construct a matrix from a one-dimensional packed array
	 * 
	 * @param name - array name
	 * @param vals - One-dimensional array of doubles, packed by columns (ala Fortran).
	 * @param m - Number of rows
	 */
	public MLUInt64(String name, long[] vals, int m) {
		this(name, castToLong(vals), m);
	}

	/**
	 * Gets two-dimensional real array.
	 * 
	 * @return - 2D real array
	 */
	public long[][] getArray() {
		long[][] result = new long[getM()][];

		for (int m = 0; m < getM(); m++) {
			result[m] = new long[getN()];

			for (int n = 0; n < getN(); n++) {
				result[m][n] = getReal(m, n);
			}
		}
		return result;
	}

	/**
	 * Casts <code>Double[]</code> to <code>byte[]</code>
	 * 
	 * @param - source <code>Long[]</code>
	 * @return - result <code>long[]</code>
	 */
	private static Long[] castToLong(long[] d) {
		Long[] dest = new Long[d.length];
		for (int i = 0; i < d.length; i++) {
			dest[i] = (long) d[i];
		}
		return dest;
	}

	/**
	 * Converts byte[][] to Long[]
	 * 
	 * @param dd
	 * @return
	 */
	private static Long[] long2DToLong(long[][] dd) {
		Long[] d = new Long[dd.length * dd[0].length];
		for (int n = 0; n < dd[0].length; n++) {
			for (int m = 0; m < dd.length; m++) {
				d[m + n * dd.length] = dd[m][n];
			}
		}
		return d;
	}

	public Long buldFromBytes(byte[] bytes) {
		if (bytes.length != getBytesAllocated()) {
			throw new IllegalArgumentException(
					"To build from byte array I need array of size: "
							+ getBytesAllocated());
		}
		return ByteBuffer.wrap(bytes).getLong();
	}

	public int getBytesAllocated() {
		return Long.SIZE >> 3;
	}

	public Class<Long> getStorageClazz() {
		return Long.class;
	}

	public byte[] getByteArray(Long value) {
		int byteAllocated = getBytesAllocated();
		ByteBuffer buff = ByteBuffer.allocate(byteAllocated);
		buff.putLong(value);
		return buff.array();
	}

}
