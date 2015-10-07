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
package com.jmatio.io.stream;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.jmatio.types.MLArray;

/**
 * This is an {@link OutputStream} that is backed by a {@link RandomAccessFile}
 * and accessed with buffered access.
 * 
 * @author Wojciech Gradkowski (<a
 *         href="mailto:wgradkowski@gmail.com">wgradkowski@gmail.com</a>)
 * 
 */
public class FileBufferedOutputStream extends BufferedOutputStream {
	private static final int BUFFER_SIZE = 1024;
	private ByteBuffer buf;
	private FileChannel rwChannel;
	private RandomAccessFile raFile;
	private final File file;

	public FileBufferedOutputStream() throws IOException {
		file = File.createTempFile("jmatio-", null);
		file.deleteOnExit();
		raFile = new RandomAccessFile(file, "rw");
		rwChannel = raFile.getChannel();
		buf = ByteBuffer.allocate(BUFFER_SIZE);
	}

	public FileBufferedOutputStream(MLArray array) throws IOException {
		file = File.createTempFile("jmatio-" + array.getName() + "-", null);
		file.deleteOnExit();
		raFile = new RandomAccessFile(file, "rw");
		rwChannel = raFile.getChannel();
		buf = ByteBuffer.allocate(BUFFER_SIZE);
	}

	@Override
	public void write(int b) throws IOException {
		if (buf.position() >= buf.capacity()) {
			flush();
		}

		buf.put((byte) (b & 0xff));
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		int wbytes = len;
		int offset = off;

		while (wbytes > 0) {
			if (buf.position() >= buf.capacity()) {
				flush();
			}

			int length = Math.min(wbytes, buf.limit() - buf.position());

			buf.put(b, offset, length);

			offset += length;
			wbytes -= length;
		}
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		flush();

		buf = null;

		if (rwChannel.isOpen()) {

			rwChannel.close();
		}

		raFile.close();
		rwChannel = null;
		raFile = null;
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		if (buf != null && buf.position() > 0) {
			buf.flip();
			rwChannel.write(buf);
			buf.clear();
		}
	}

	/* (non-Javadoc)
	 * @see com.jmatio.io.DataOutputStream#size()
	 */
	public long size() throws IOException {
		flush();

		return (int) file.length();
	}

	/* (non-Javadoc)
	 * @see com.jmatio.io.DataOutputStream#getByteBuffer()
	 */
	@Override
	public ByteBuffer buffer() throws IOException {
		return rwChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
	}

	/* (non-Javadoc)
	 * @see com.jmatio.io.DataOutputStream#write(java.nio.ByteBuffer)
	 */
	public void write(ByteBuffer byteBuffer) throws IOException {
		byte[] tmp = new byte[BUFFER_SIZE];

		while (byteBuffer.hasRemaining()) {
			int length = Math.min(byteBuffer.remaining(), tmp.length);
			byteBuffer.get(tmp, 0, length);
			write(tmp, 0, length);
		}
	}

}
