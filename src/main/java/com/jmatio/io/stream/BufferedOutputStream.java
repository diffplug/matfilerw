/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.io.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public abstract class BufferedOutputStream extends OutputStream {
	/**
	 * Returns the backing {@link ByteBuffer}
	 * @return
	 */
	public abstract ByteBuffer buffer() throws IOException;

	public abstract long size() throws IOException;
}
