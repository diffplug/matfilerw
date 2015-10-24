/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.io.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class HeapBufferDataOutputStream extends ByteArrayOutputStream implements DataOutputStream {
	private static final int BUFFER_SIZE = 1024;

	public ByteBuffer getByteBuffer() throws IOException {
		return ByteBuffer.wrap(super.buf);
	}

	public void write(ByteBuffer byteBuffer) throws IOException {

		byte[] tmp = new byte[BUFFER_SIZE];

		while (byteBuffer.hasRemaining()) {
			int length = Math.min(byteBuffer.remaining(), tmp.length);
			byteBuffer.get(tmp, 0, length);
			write(tmp, 0, length);
		}
	}
}
