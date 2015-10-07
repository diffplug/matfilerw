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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

import com.jmatio.io.MatFileHeader;
import com.jmatio.types.MLArray;

/**
 * MatFileReader's API is vague.  Do you pass a file in the constructor
 * or the read method?  It also limits your ability to partially parse
 * a section of code, such as the case where you have a MAT-file without
 * a header
 * <p>
 * MatFile is a value class which presents a simplified API for parsing either a
 * ByteBuffer or a RandomAccessFile.
 */
public class MatFile {
	private final MatFileHeader header;
	private final Map<String, MLArray> content;

	public MatFile(MatFileHeader header, Map<String, MLArray> content) {
		this.header = header;
		this.content = content;
	}

	public MatFileHeader getHeader() {
		return header;
	}

	public Map<String, MLArray> getContent() {
		return content;
	}

	/** Reads a bare set of bytes. */
	public static Map<String, MLArray> readBare(MatFileHeader header, ByteBuffer buffer) throws IOException {
		MatFileReader reader = new MatFileReader();
		reader.matFileHeader = header;

		// set the byteOrder based on this
		if (header.getEndianIndicator()[0] == 'I' && header.getEndianIndicator()[1] == 'M') {
			reader.byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else if (header.getEndianIndicator()[0] == 'M' && header.getEndianIndicator()[1] == 'I') {
			reader.byteOrder = ByteOrder.BIG_ENDIAN;
		} else {
			throw new IllegalArgumentException("Unsupported endianness!");
		}
		buffer.order(reader.byteOrder);

		reader.readData(buffer);
		return reader.data;
	}

	/** Reads a full set of bytes (including the header). */
	public static MatFile readFull(ByteBuffer buf) throws IOException {
		MatFileReader reader = new MatFileReader();
		reader.readHeader(buf);
		while (buf.remaining() > 0) {
			reader.readData(buf);
		}
		return new MatFile(reader.getMatFileHeader(), reader.getContent());
	}

	/** Reads a full set of bytes (including the header). */
	public static MatFile readFull(RandomAccessFile file) throws IOException {
		MatFileReader reader = new MatFileReader();
		reader.read(file, new MatFileFilter(), MatFileReader.HEAP_BYTE_BUFFER);
		return new MatFile(reader.getMatFileHeader(), reader.getContent());
	}
}
