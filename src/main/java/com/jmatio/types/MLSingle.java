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

public class MLSingle extends MLNumericArray<Float> {

	public MLSingle(String name, Float[] vals, int m) {
		super(name, MLArray.mxSINGLE_CLASS, vals, m);
	}

	public MLSingle(String name, int[] dims, int type, int attributes) {
		super(name, dims, type, attributes);
	}

	public Float buldFromBytes(byte[] bytes) {
		if (bytes.length != getBytesAllocated()) {
			throw new IllegalArgumentException(
					"To build from byte array I need array of size: "
							+ getBytesAllocated());
		}
		return ByteBuffer.wrap(bytes).getFloat();
	}

	public byte[] getByteArray(Float value) {
		int byteAllocated = getBytesAllocated();
		ByteBuffer buff = ByteBuffer.allocate(byteAllocated);
		buff.putFloat(value);
		return buff.array();
	}

	public int getBytesAllocated() {
		return Float.SIZE >> 3;
	}

	public Class<?> getStorageClazz() {
		return Float.class;
	}

}
