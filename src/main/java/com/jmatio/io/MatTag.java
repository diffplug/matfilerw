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

import com.jmatio.common.MatDataTypes;

/**
 * 
 * @author Wojciech Gradkowski (<a href="mailto:wgradkowski@gmail.com">wgradkowski@gmail.com</a>)
 */
class MatTag {
	protected int type;
	protected int size;

	/**
	 * @param type
	 * @param size
	 * @param compressed
	 */
	public MatTag(int type, int size) {
		this.type = type;
		this.size = size;
	}

	/**
	 * Calculate padding
	 */
	protected int getPadding(int size, boolean compressed) {
		int padding;
		//data not packed in the tag
		if (!compressed) {
			int b;
			padding = (b = (((size / sizeOf()) % (8 / sizeOf())) * sizeOf())) != 0 ? 8 - b : 0;
		} else //data _packed_ in the tag (compressed)
		{
			int b;
			padding = (b = (((size / sizeOf()) % (4 / sizeOf())) * sizeOf())) != 0 ? 4 - b : 0;
		}
		return padding;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s;

		s = "[tag: " + MatDataTypes.typeToString(type) + " size: " + size + "]";

		return s;
	}

	/**
	 * Get size of single data in this tag.
	 * 
	 * @return - number of bytes for single data
	 */
	public int sizeOf() {
		return MatDataTypes.sizeOf(type);
	}

}
