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

import java.util.ArrayList;

public class MLCell extends MLArray {
	private ArrayList<MLArray> cells;

	public MLCell(String name, int[] dims) {
		this(name, dims, MLArray.mxCELL_CLASS, 0);
	}

	public MLCell(String name, int[] dims, int type, int attributes) {
		super(name, dims, type, attributes);

		cells = new ArrayList<MLArray>(getM() * getN());

		for (int i = 0; i < getM() * getN(); i++) {
			cells.add(new MLEmptyArray());
		}
	}

	public void set(MLArray value, int m, int n) {
		cells.set(getIndex(m, n), value);
	}

	public void set(MLArray value, int index) {
		cells.set(index, value);
	}

	public MLArray get(int m, int n) {
		return cells.get(getIndex(m, n));
	}

	public MLArray get(int index) {
		return cells.get(index);
	}

	public int getIndex(int m, int n) {
		return m + n * getM();
	}

	public ArrayList<MLArray> cells() {
		return cells;
	}

	public String contentToString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name + " = \n");

		for (int m = 0; m < getM(); m++) {
			sb.append("\t");
			for (int n = 0; n < getN(); n++) {
				sb.append(get(m, n));
				sb.append("\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

}
