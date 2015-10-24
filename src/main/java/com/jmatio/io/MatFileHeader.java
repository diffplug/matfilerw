/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.io;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.Date;

import com.jmatio.common.MatDataTypes;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * MAT-file header
 * 
 * Level 5 MAT-files begin with a 128-byte header made up of a 124 byte text field
 * and two, 16-bit flag fields
 * 
 * @author Wojciech Gradkowski (<a href="mailto:wgradkowski@gmail.com">wgradkowski@gmail.com</a>)
 */
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "This code is unlikely to be used in a security-sensitive environment.")
public class MatFileHeader {
	private static String DEFAULT_DESCRIPTIVE_TEXT = "MATLAB 5.0 MAT-file, Platform: "
			+ System.getProperty("os.name")
			+ ", CREATED on: ";
	private static int DEFAULT_VERSION = 0x0100;
	private static byte[] DEFAULT_ENDIAN_INDICATOR = new byte[]{(byte) 'M', (byte) 'I'};
	private final ByteOrder byteOrder;

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
	public MatFileHeader(String description, int version, byte[] endianIndicator, ByteOrder byteOrder) {
		this.description = description;
		this.version = version;
		this.endianIndicator = endianIndicator;
		this.byteOrder = byteOrder;
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
	public byte[] getEndianIndicator() {
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
				DEFAULT_ENDIAN_INDICATOR,
				ByteOrder.BIG_ENDIAN);
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

	public ByteOrder getByteOrder() {
		assert((byteOrder != ByteOrder.LITTLE_ENDIAN || endianIndicator[0] == 'I') && (byteOrder != ByteOrder.BIG_ENDIAN || endianIndicator[0] == 'M'));
		return byteOrder;
	}
}
