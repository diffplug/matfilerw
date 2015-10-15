/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
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
