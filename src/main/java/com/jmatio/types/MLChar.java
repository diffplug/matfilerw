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

import java.util.Arrays;
import java.util.List;

public class MLChar extends MLArray {
	Character[] chars;

	/**
	 * Creates the 1 x {@link String#length()} {@link MLChar} from the given
	 * String.
	 * 
	 * @param name the {@link MLArray} name
	 * @param value the String
	 */
	public MLChar(String name, String value) {
		this(name, new int[]{1, value.length()}, MLArray.mxCHAR_CLASS, 0);
		set(value);
	}

	/**
	 * Create the {@link MLChar} from array of {@link String}s. 
	 * 
	 * @param name the {@link MLArray} name
	 * @param values the array of {@link String}s
	 */
	public MLChar(String name, String[] values) {
		this(name, new int[]{values.length, values.length > 0 ? getMaxLength(values) : 0}, MLArray.mxCHAR_CLASS, 0);

		for (int i = 0; i < values.length; i++) {
			set(values[i], i);
		}
	}

	/**
	 * Returns the maximum {@link String} length of array of {@link String}s. 
	 * @param values the array of {@link String}s
	 * @return the maximum {@link String} length of array of {@link String}s
	 */
	private static int getMaxLength(String[] values) {
		int result = 0;

		for (int i = 0, curr = 0; i < values.length; i++) {
			if ((curr = values[i].length()) > result) {
				result = curr;
			}
		}
		return result;
	}

	/**
	 * Added method to allow initialization of a char array representing 
	 * an array of strings.
	 * 
	 * @param name
	 * @param values
	 * @param maxlen
	 */
	public MLChar(String name, String[] values, int maxlen) {
		this(name, new int[]{values.length, maxlen}, MLArray.mxCHAR_CLASS, 0);
		int idx = 0;
		for (String v : values) {
			set(v, idx);
			idx++;
		}
	}

	public MLChar(String name, int[] dims, int type, int attributes) {
		super(name, dims, type, attributes);
		chars = createArray(getM(), getN());
	}

	protected Character[] createArray(int m, int n) {
		return new Character[m * n];
	}

	public void setChar(char ch, int index) {
		chars[index] = ch;
	}

	/**
	 * Populates the {@link MLChar} with the {@link String} value.
	 * @param value the String value
	 */
	public void set(String value) {
		char[] cha = value.toCharArray();
		for (int i = 0; i < getN() && i < value.length(); i++) {
			setChar(cha[i], i);
		}
	}

	/** 
	 * Set one row, specifying the row.
	 * 
	 * @param value
	 * @param idx
	 */
	public void set(String value, int idx) {
		int rowOffset = getM();

		for (int i = 0; i < getN(); i++) {
			if (i < value.length()) {
				setChar(value.charAt(i), idx + (rowOffset * i));
			} else {
				setChar(' ', idx + (rowOffset * i));
			}
		}
	}

	public Character getChar(int m, int n) {
		return chars[getIndex(m, n)];
	}

	public List<Character> exportChar() {
		return Arrays.asList(chars);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MLChar) {
			return Arrays.equals(chars, ((MLChar) o).chars);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(chars);
	}

	/**
	 * Gets the m-th character matrix's row as <code>String</code>.
	 * 
	 * @param m - row number
	 * @return - <code>String</code>
	 */
	public String getString(int m) {
		StringBuffer charbuff = new StringBuffer();

		for (int n = 0; n < getN(); n++) {
			charbuff.append(getChar(m, n));
		}

		return charbuff.toString().trim();
	}

	public String contentToString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name + " = \n");

		for (int m = 0; m < getM(); m++) {
			sb.append("\t");
			StringBuffer charbuff = new StringBuffer();
			charbuff.append("'");
			for (int n = 0; n < getN(); n++) {
				charbuff.append(getChar(m, n));
			}
			charbuff.append("'");
			sb.append(charbuff);
			sb.append("\n");
		}
		return sb.toString();

	}
}
