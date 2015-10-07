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
 * Class represents Double array (matrix)
 * 
 * @author Wojciech Gradkowski <wgradkowski@gmail.com>
 */
public class MLDouble extends MLNumericArray<Double> {

	/**
	 * Normally this constructor is used only by MatFileReader and MatFileWriter
	 * 
	 * @param name - array name
	 * @param dims - array dimensions
	 * @param type - array type: here <code>mxDOUBLE_CLASS</code>
	 * @param attributes - array flags
	 */
	public MLDouble(String name, int[] dims, int type, int attributes) {
		super(name, dims, type, attributes);
	}

	/**
	 * Create a <code>MLDouble</code> array with given name,
	 * and dimensions.
	 * 
	 * @param name - array name
	 * @param dims - array dimensions
	 */
	public MLDouble(String name, int[] dims) {
		super(name, dims, MLArray.mxDOUBLE_CLASS, 0);
	}

	/**
	 * <a href="http://math.nist.gov/javanumerics/jama/">Jama</a> [math.nist.gov] style: 
	 * construct a 2D real matrix from a one-dimensional packed array
	 * 
	 * @param name - array name
	 * @param vals - One-dimensional array of doubles, packed by columns (ala Fortran).
	 * @param m - Number of rows
	 */
	public MLDouble(String name, Double[] vals, int m) {
		super(name, MLArray.mxDOUBLE_CLASS, vals, m);
	}

	/**
	 * <a href="http://math.nist.gov/javanumerics/jama/">Jama</a> [math.nist.gov] style: 
	 * construct a 2D real matrix from <code>double[][]</code>
	 * 
	 * Note: array is converted to Double[]
	 * 
	 * @param name - array name
	 * @param vals - two-dimensional array of values
	 */
	public MLDouble(String name, double[][] vals) {
		this(name, double2DToDouble(vals), vals.length);
	}

	/**
	 * <a href="http://math.nist.gov/javanumerics/jama/">Jama</a> [math.nist.gov] style: 
	 * construct a matrix from a one-dimensional packed array
	 * 
	 * @param name - array name
	 * @param vals - One-dimensional array of doubles, packed by columns (ala Fortran).
	 * @param m - Number of rows
	 */
	public MLDouble(String name, double[] vals, int m) {
		this(name, castToDouble(vals), m);
	}

	/**
	 * Gets two-dimensional real array.
	 * 
	 * @return - 2D real array
	 */
	public double[][] getArray() {
		double[][] result = new double[getM()][];

		for (int m = 0; m < getM(); m++) {
			result[m] = new double[getN()];

			for (int n = 0; n < getN(); n++) {
				result[m][n] = getReal(m, n);
			}
		}
		return result;
	}

	/**
	 * Casts <code>Double[]</code> to <code>double[]</code>
	 * 
	 * @param - source <code>Double[]</code>
	 * @return - result <code>double[]</code>
	 */
	private static Double[] castToDouble(double[] d) {
		Double[] dest = new Double[d.length];
		for (int i = 0; i < d.length; i++) {
			dest[i] = (Double) d[i];
		}
		return dest;
	}

	/**
	 * Converts double[][] to Double[]
	 * 
	 * @param dd
	 * @return
	 */
	private static Double[] double2DToDouble(double[][] dd) {
		Double[] d = new Double[dd.length * dd[0].length];
		for (int n = 0; n < dd[0].length; n++) {
			for (int m = 0; m < dd.length; m++) {
				d[m + n * dd.length] = dd[m][n];
			}
		}
		return d;
	}

	public int getBytesAllocated() {
		return Double.SIZE >> 3;
	}

	public Double buldFromBytes(byte[] bytes) {
		if (bytes.length != getBytesAllocated()) {
			throw new IllegalArgumentException(
					"To build from byte array I need array of size: "
							+ getBytesAllocated());
		}
		return ByteBuffer.wrap(bytes).getDouble();

	}

	public byte[] getByteArray(Double value) {
		int byteAllocated = getBytesAllocated();
		ByteBuffer buff = ByteBuffer.allocate(byteAllocated);
		buff.putDouble(value);
		return buff.array();
	}

	public Class<Double> getStorageClazz() {
		return Double.class;
	}
}
