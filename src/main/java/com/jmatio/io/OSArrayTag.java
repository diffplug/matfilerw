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
package com.jmatio.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Tiny class that represents MAT-file TAG 
 * It simplifies writing data. Automates writing padding for instance.
 */
class OSArrayTag extends MatTag {
	private ByteBuffer data;

	/**
	 * Creates TAG and stets its <code>size</code> as size of byte array
	 * 
	 * @param type
	 * @param data
	 */
	public OSArrayTag(int type, byte[] data) {
		this(type, ByteBuffer.wrap(data));
	}

	/**
	 * Creates TAG and stets its <code>size</code> as size of byte array
	 * 
	 * @param type
	 * @param data
	 */
	public OSArrayTag(int type, ByteBuffer data) {
		super(type, data.limit());
		this.data = data;
		data.rewind();
	}

	/**
	 * Writes tag and data to <code>DataOutputStream</code>. Wites padding if neccesary.
	 * 
	 * @param os
	 * @throws IOException
	 */
	public void writeTo(DataOutputStream os) throws IOException {

		int padding;
		if (size <= 4 && size > 0) {
			// Use small data element format (Page 1-10 in "MATLAB 7 MAT-File Format", September 2010 revision)
			os.writeShort(size);
			os.writeShort(type);
			padding = getPadding(data.limit(), true);
		} else {
			os.writeInt(type);
			os.writeInt(size);
			padding = getPadding(data.limit(), false);
		}

		int maxBuffSize = 1024;
		int writeBuffSize = data.remaining() < maxBuffSize ? data.remaining() : maxBuffSize;
		byte[] tmp = new byte[writeBuffSize];
		while (data.remaining() > 0) {
			int length = data.remaining() > tmp.length ? tmp.length : data.remaining();
			data.get(tmp, 0, length);
			os.write(tmp, 0, length);
		}

		if (padding > 0) {
			os.write(new byte[padding]);
		}
	}
}
