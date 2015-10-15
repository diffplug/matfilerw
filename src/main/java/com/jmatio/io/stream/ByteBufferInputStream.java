/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.io.stream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {
	private ByteBuffer buf;

	private long limit;

	public ByteBufferInputStream(final ByteBuffer buf, final long limit) {
		this.buf = buf;
		this.limit = limit;
	}

	@Override
	public synchronized int read() throws IOException {
		if (!(limit > 0)) {
			return -1;
		}
		limit--;
		return buf.get() & 0xFF;
	}

	@Override
	public synchronized int read(byte[] bytes, int off, int len)
			throws IOException {
		if (!(limit > 0)) {
			return -1;
		}
		len = (int) Math.min(len, limit);
		// Read only what's left
		buf.get(bytes, off, len);
		limit -= len;
		return len;
	}
}
