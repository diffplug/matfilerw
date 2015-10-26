/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.io;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Date;

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
	public static final int DEFAULT_VERSION = 0x0100;
	public static final ByteOrder DEFAULT_ENDIAN = ByteOrder.BIG_ENDIAN;

	private static final byte[] ENDIAN_INDICATOR_BIG = new byte[]{(byte) 'M', (byte) 'I'};
	private static final byte[] ENDIAN_INDICATOR_LITTLE = new byte[]{(byte) 'I', (byte) 'M'};

	private int version;
	private String description;
	private final ByteOrder byteOrder;

	/**
	 * Parses a MatFileHeader from its desciption and the raw bytes of the version and endian indicator.
	 * 
	 * @param description - descriptive test
	 * @param bversion - 2-byte array containing the version (raw from a MAT-File)
	 * @param endianIndicator - 2-byte array containing the endian indicator (raw from a MAT-File(
	 * @return
	 */
	public static MatFileHeader parseFrom(String description, byte[] bversion, byte[] endianIndicator) {
		int version;
		ByteOrder byteOrder = parseByteOrder(endianIndicator);
		if (byteOrder == ByteOrder.BIG_ENDIAN) {
			version = bversion[0] & 0xff | bversion[1] << 8;
		} else if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
			version = bversion[1] & 0xff | bversion[0] << 8;
		} else {
			throw new IllegalArgumentException("Unknown byteOrder " + byteOrder);
		}
		return new MatFileHeader(description, version, byteOrder);
	}

	/**
	 * Parses out the byte order based on a byte array containing
	 * either 'MI' (big-endian) or 'IM' (little-endian).
	 *
	 * @param endianIndicator 2-byte long array holding the endian indicator
	 */
	public static ByteOrder parseByteOrder(byte[] endianIndicator) {
		if (Arrays.equals(ENDIAN_INDICATOR_BIG, endianIndicator)) {
			return ByteOrder.BIG_ENDIAN;
		} else if (Arrays.equals(ENDIAN_INDICATOR_LITTLE, endianIndicator)) {
			return ByteOrder.LITTLE_ENDIAN;
		} else {
			StringBuilder arrayBuilder = new StringBuilder();
			for (int i = 0; i < endianIndicator.length; ++i) {
				arrayBuilder.append(endianIndicator[i]);
			}
			throw new IllegalArgumentException("Unknown endian indicator " + arrayBuilder.toString());
		}
	}

	/**
	 * New MAT-file header
	 * 
	 * @param description - descriptive text (no longer than 116 characters)
	 * @param version - by default is set to 0x0100
	 * @param endianIndicator - byte array size of 2 indicating byte-swapping requirement
	 */
	public MatFileHeader(String description, int version, ByteOrder byteOrder) {
		this.description = description;
		this.version = version;
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
		if (byteOrder == ByteOrder.BIG_ENDIAN) {
			return Arrays.copyOf(ENDIAN_INDICATOR_BIG, 2);
		} else if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
			return Arrays.copyOf(ENDIAN_INDICATOR_LITTLE, 2);
		} else {
			throw new IllegalArgumentException("Unknown byteOrder '" + byteOrder + "'");
		}
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
				DEFAULT_ENDIAN);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("desriptive text: " + description);
		sb.append(", version: " + version);
		sb.append(", byteOrder: " + byteOrder);
		sb.append("]");
		return sb.toString();
	}

	public ByteOrder getByteOrder() {
		return byteOrder;
	}
}
