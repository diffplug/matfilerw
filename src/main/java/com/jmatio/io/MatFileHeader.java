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

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.jmatio.common.MatDataTypes;

/**
 * MAT-file header
 * 
 * Level 5 MAT-files begin with a 128-byte header made up of a 124 byte text field
 * and two, 16-bit flag fields
 * 
 * @author Wojciech Gradkowski (<a href="mailto:wgradkowski@gmail.com">wgradkowski@gmail.com</a>)
 */
public class MatFileHeader {
	private static final String DEFAULT_DESCRIPTIVE_TEXT = "MATLAB 5.0 MAT-file, Platform: "
			+ System.getProperty("os.name")
			+ ", CREATED on: ";
	public static final int DEFAULT_VERSION = 0x0100;

	private int version;
	private String description;
	private byte[] endianIndicator;

	/**
	 * New MAT-file header
	 * 
	 * @param description - descriptive text (no longer than 116 characters)
	 * @param version - by default is set to 0x0100
	 * @param endianIndicator - byte array size of 2 indicating byte-swapping requirement
	 */
	public MatFileHeader(String description, int version, byte[] endianIndicator) {
		this.description = description;
		this.version = version;
		this.endianIndicator = new byte[endianIndicator.length];
		System.arraycopy(endianIndicator, 0, this.endianIndicator, 0, endianIndicator.length);
	}

	/**
	 * Gets descriptive text
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets endian indicator. Bytes written as "MI" suggest that byte-swapping operation is required
	 * in order to interpret data correctly. If value is set to "IM" byte-swapping is not needed.
	 * 
	 * @return - a byte array size of 2
	 */
	byte[] getEndianIndicator() {
		return endianIndicator;
	}

	/**
	 * When creating a MAT-file, set version to 0x0100
	 * 
	 * @return
	 */
	public int getVersion() {
		return version;
	}

	//@facotry
	/**
	 * A factory. Creates new <code>MatFileHeader</code> instance with default header values:
	 * <ul>
	 *  <li>MAT-file is 5.0 version</li>
	 *  <li>version is set to 0x0100</li>
	 *  <li>no byte-swapping ("IM")</li>
	 * </ul>
	 * 
	 * @return - new <code>MatFileHeader</code> instance
	 */
	public static MatFileHeader createHeader() {
		return new MatFileHeader(DEFAULT_DESCRIPTIVE_TEXT + (new Date()).toString(),
				DEFAULT_VERSION,
				DEFAULT_ENDIAN_INDICATOR());
	}

	/** Returns the default endianness indicator. */
	private static final byte[] DEFAULT_ENDIAN_INDICATOR() {
		return new byte[]{(byte) 'M', (byte) 'I'};
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			sb.append("desriptive text: " + description);
			sb.append(", version: " + version);
			sb.append(", endianIndicator: " + new String(endianIndicator, MatDataTypes.CHARSET));
			sb.append("]");
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
