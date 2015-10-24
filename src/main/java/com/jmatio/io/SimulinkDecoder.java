/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.jmatio.common.MatDataTypes;

/** This class decodes the Simulink base64 representation of a MAT file.
 *
 * @author Matthew Dawson <matthew@mjdsystems.ca>
 */
public class SimulinkDecoder extends InputStream {
	public SimulinkDecoder(String input) {
		try {
			this.input = input.getBytes(MatDataTypes.CHARSET);
			for (byte i = 0; i < 64; ++i) {
				decoderRing.put((byte) (i + ' '), i);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int read() throws IOException {
		try {
			int ret;
			switch (pos % 4) {
			case 0: {
				byte first = decoderRing.get(input[pos++]);
				byte second = decoderRing.get(input[pos++]);

				ret = (first << 2) | (second >> 4);
				leftover = (byte) (second & 0x0f);
				break;
			}
			case 2: {
				byte next = decoderRing.get(input[pos++]);

				ret = (leftover << 4) | (next >> 2);
				leftover = (byte) (next & 0x03);
				break;
			}
			case 3: {
				byte next = decoderRing.get(input[pos++]);
				ret = (leftover << 6) | next;
				break;
			}
			case -1: { // We need to explicitly deal with the -1 case, as -1 % 4 in Java is -1.
				return -1;
			}
			default:
				throw new RuntimeException("Case " + (pos % 4));
			}

			return ret;
		} catch (ArrayIndexOutOfBoundsException ex) {
			pos = -1; // Ensure that the exception will be re-thrown, avoiding a buffer overflow.
			return -1;
		}
	}

	private final byte[] input;
	private int pos;
	private byte leftover;
	final private Map<Byte, Byte> decoderRing = new HashMap<Byte, Byte>();
}
