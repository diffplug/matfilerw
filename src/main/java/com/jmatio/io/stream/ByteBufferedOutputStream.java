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

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * From https://gist.github.com/manzke/985007
 *
 */
public class ByteBufferedOutputStream extends BufferedOutputStream {
	private ByteBuffer buffer;

	private boolean onHeap;

	private float increasing = DEFAULT_INCREASING_FACTOR;

	public static final float DEFAULT_INCREASING_FACTOR = 1.5f;

	public ByteBufferedOutputStream(int size) {
		this(size, DEFAULT_INCREASING_FACTOR, false);
	}

	public ByteBufferedOutputStream(int size, boolean onHeap) {
		this(size, DEFAULT_INCREASING_FACTOR, onHeap);
	}

	public ByteBufferedOutputStream(int size, float increasingBy) {
		this(size, increasingBy, false);
	}

	public ByteBufferedOutputStream(int size, float increasingBy, boolean onHeap) {
		if (increasingBy <= 1) {
			throw new IllegalArgumentException("Increasing Factor must be greater than 1.0");
		}
		if (onHeap) {
			buffer = ByteBuffer.allocate(size);
		} else {
			buffer = ByteBuffer.allocateDirect(size);
		}
		this.onHeap = onHeap;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		int position = buffer.position();
		int limit = buffer.limit();

		long newTotal = position + len;
		if (newTotal > limit) {
			int capacity = (int) (buffer.capacity() * increasing);
			while (capacity <= newTotal) {
				capacity = (int) (capacity * increasing);
			}

			increase(capacity);
		}

		buffer.put(b, 0, len);
	}

	@Override
	public void write(int b) throws IOException {
		if (!buffer.hasRemaining()) {
			increase((int) (buffer.capacity() * increasing));
		}
		buffer.put((byte) b);
	}

	protected void increase(int newCapacity) {
		buffer.limit(buffer.position());
		buffer.rewind();

		ByteBuffer newBuffer;
		if (onHeap) {
			newBuffer = ByteBuffer.allocate(newCapacity);
		} else {
			newBuffer = ByteBuffer.allocateDirect(newCapacity);
		}

		newBuffer.put(buffer);
		buffer.clear();
		buffer = newBuffer;
	}

	@Override
	public long size() {
		return buffer.position();
	}

	public long capacity() {
		return buffer.capacity();
	}

	public ByteBuffer buffer() {
		return buffer;
	}
}
