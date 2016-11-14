/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class MLSparse extends MLNumericArray<Double> {
	int nzmax;
	private SortedSet<IndexMN> indexSet;
	private SortedMap<IndexMN, Double> real;
	private SortedMap<IndexMN, Double> imaginary;

	/**
	 * @param name
	 * @param dims
	 * @param attributes
	 * @param nzmax
	 */
	public MLSparse(String name, int[] dims, int attributes, int nzmax) {
		super(name, dims, MLArray.mxSPARSE_CLASS, attributes);
		this.nzmax = nzmax;
	}

	@Override
	protected void allocate() {
		indexSet = new TreeSet<IndexMN>();
		real = new TreeMap<IndexMN, Double>();
		if (isComplex()) {
			imaginary = new TreeMap<IndexMN, Double>();
		} else {
			imaginary = null;
		}
	}

	/**
	 * Gets maximum number of non-zero values
	 * 
	 * @return
	 */
	public int getMaxNZ() {
		return nzmax;
	}

	/**
	 * Gets row indices
	 * 
	 * <tt>ir</tt> points to an integer array of length nzmax containing the row indices of
	 * the corresponding elements in <tt>pr</tt> and <tt>pi</tt>.
	 */
	public int[] getIR() {
		int[] ir = new int[nzmax];
		int i = 0;
		for (IndexMN index : indexSet) {
			ir[i++] = index.m;
		}
		return ir;
	}

	/**
	 * Gets column indices
	 * 
	 * <tt>ic</tt> points to an integer array of length nzmax containing the column indices of
	 * the corresponding elements in <tt>pr</tt> and <tt>pi</tt>.
	 */
	public int[] getIC() {
		int[] ic = new int[nzmax];
		int i = 0;
		for (IndexMN index : indexSet) {
			ic[i++] = index.n;
		}
		return ic;
	}

	/**
	 * Gets column indices. 
	 * 
	 * <tt>jc</tt> points to an integer array of length N+1 that contains column index information.
	 * For j, in the range <tt>0&lt;=j&lt;=N</tt>, <tt>jc[j]</tt> is the index in ir and <tt>pr</tt> (and <tt>pi</tt>
	 * if it exists) of the first nonzero entry in the jth column and <tt>jc[j+1]?????????1</tt> index
	 * of the last nonzero entry. As a result, <tt>jc[N]</tt> is also equal to nnz, the number
	 * of nonzero entries in the matrix. If nnz is less than nzmax, then more nonzero
	 * entries can be inserted in the array without allocating additional storage
	 * 
	 * @return
	 */
	public int[] getJC() {
		int[] jc = new int[getN() + 1];
		// jc[j] is the number of nonzero elements in all preceeding columns
		for (IndexMN index : indexSet) {
			for (int column = index.n + 1; column < jc.length; column++) {
				jc[column]++;
			}
		}
		return jc;
	}

	@Override
	public Double getReal(int m, int n) {
		IndexMN i = new IndexMN(m, n);
		Double result = real.get(i);
		return result == null ? Zeros.DOUBLE : result;
	}

	@Override
	public Double getReal(int index) {
		throw new UnsupportedOperationException("Can't get Sparse array elements by index. " +
				"Please use getReal(int m, int n) instead.");
	}

	@Override
	public void setReal(Double value, int m, int n) {
		IndexMN i = new IndexMN(m, n);
		indexSet.add(i);
		real.put(i, value);
	}

	@Override
	public void setReal(Double value, int index) {
		throw new UnsupportedOperationException("Can't set Sparse array elements by index. " +
				"Please use setReal(Double value, int m, int n) instead.");
	}

	@Override
	public void setImaginary(Double value, int m, int n) {
		assertComplex();
		IndexMN i = new IndexMN(m, n);
		indexSet.add(i);
		imaginary.put(i, value);
	}

	@Override
	public void setImaginary(Double value, int index) {
		throw new IllegalArgumentException("Can't set Sparse array elements by index. " +
				"Please use setImaginary(Double value, int m, int n) instead.");
	}

	@Override
	public Double getImaginary(int m, int n) {
		IndexMN i = new IndexMN(m, n);
		Double result = imaginary.get(i);
		return result == null ? Zeros.DOUBLE : result;
	}

	@Override
	public Double getImaginary(int index) {
		throw new IllegalArgumentException("Can't get Sparse array elements by index. " +
				"Please use getImaginary(int index) instead.");
	}

	@Override
	public void set(Double value, int m, int n) {
		IndexMN i = new IndexMN(m, n);
		indexSet.add(i);
		real.put(i, value);
	}

	@Override
	public void set(Double value, int index) {
		throw new IllegalArgumentException("Can't set Sparse array elements by index. " +
				"Please use setImaginary(Double value, int m, int n) instead.");
	}

	@Override
	public Double get(int m, int n) {
		IndexMN i = new IndexMN(m, n);
		Double result = real.get(i);
		return result == null ? Zeros.DOUBLE : result;
	}

	@Override
	public Double get(int index) {
		throw new IllegalArgumentException("Can't get Sparse array elements by index. " +
				"Please use getImaginary(int index) instead.");
	}

	/**
	 * Returns the real part (PR) array. PR has length number-of-nonzero-values.
	 *
	 * @return real part
	 */
	public Double[] exportReal() {
		Double[] ad = new Double[indexSet.size()];
		int i = 0;
		for (IndexMN index : indexSet) {
			if (real.containsKey(index)) {
				ad[i] = real.get(index);
			} else {
				ad[i] = Zeros.DOUBLE;
			}
			i++;
		}
		return ad;
	}

	/**
	 * Returns the imaginary part (PI) array. PI has length number-of-nonzero-values.
	 *
	 * @return
	 */
	public Double[] exportImaginary() {
		Double[] ad = new Double[indexSet.size()];
		int i = 0;
		for (IndexMN index : indexSet) {
			if (imaginary.containsKey(index)) {
				ad[i] = imaginary.get(index);
			} else {
				ad[i] = Zeros.DOUBLE;
			}
			i++;
		}
		return ad;
	}

	/* (non-Javadoc)
	 * @see com.paradigmdesigner.matlab.types.MLArray#contentToString()
	 */
	@Override
	public String contentToString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name + " = \n");

		for (IndexMN i : indexSet) {
			sb.append("\t(");
			sb.append(i.m + "," + i.n);
			sb.append(")");
			sb.append("\t" + getReal(i.m, i.n));
			if (isComplex()) {
				sb.append("+" + getImaginary(i.m, i.n));
			}
			sb.append("\n");

		}

		return sb.toString();
	}

	/**
	 * Matrix index (m,n)
	 * 
	 * @author Wojciech Gradkowski <wgradkowski@gmail.com>
	 */
	private class IndexMN implements Comparable<IndexMN> {
		int m;
		int n;

		public IndexMN(int m, int n) {
			this.m = m;
			this.n = n;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(IndexMN anOtherIndex) {
			return getIndex(m, n) - getIndex(anOtherIndex.m, anOtherIndex.n);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			if (o instanceof IndexMN) {
				return m == ((IndexMN) o).m && n == ((IndexMN) o).n;
			}
			return super.equals(o);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(new int[]{m, n});
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			sb.append("m=" + m);
			sb.append(", ");
			sb.append("n=" + n);
			sb.append("}");
			return sb.toString();
		}
	}

	@Override
	public int getBytesAllocated() {
		return Double.SIZE << 3;
	}

	@Override
	public Double buldFromBytes(byte[] bytes) {
		if (bytes.length != getBytesAllocated()) {
			throw new IllegalArgumentException(
					"To build from byte array I need array of size: "
							+ getBytesAllocated());
		}
		return ByteBuffer.wrap(bytes).getDouble();

	}

	@Override
	public byte[] getByteArray(Double value) {
		int byteAllocated = getBytesAllocated();
		ByteBuffer buff = ByteBuffer.allocate(byteAllocated);
		buff.putDouble(value);
		return buff.array();
	}

	@Override
	public Class<Double> getStorageClazz() {
		return Double.class;
	}

	@Override
	protected Double zero() {
		return Zeros.DOUBLE;
	}
}
