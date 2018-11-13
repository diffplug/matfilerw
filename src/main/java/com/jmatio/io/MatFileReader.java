/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.io;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.InflaterInputStream;

import com.jmatio.common.MatDataTypes;
import com.jmatio.io.MatFileWriter.ByteArrayOutputStream2;
import com.jmatio.io.stream.ByteBufferInputStream;
import com.jmatio.io.stream.HeapBufferDataOutputStream;
import com.jmatio.io.stream.MatFileInputStream;
import com.jmatio.types.ByteStorageSupport;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLEmptyArray;
import com.jmatio.types.MLHandle;
import com.jmatio.types.MLInt16;
import com.jmatio.types.MLInt32;
import com.jmatio.types.MLInt64;
import com.jmatio.types.MLInt8;
import com.jmatio.types.MLJavaObject;
import com.jmatio.types.MLNumericArray;
import com.jmatio.types.MLObject;
import com.jmatio.types.MLSingle;
import com.jmatio.types.MLSparse;
import com.jmatio.types.MLStructure;
import com.jmatio.types.MLUInt16;
import com.jmatio.types.MLUInt32;
import com.jmatio.types.MLUInt64;
import com.jmatio.types.MLUInt8;

/**
 * MAT-file reader. Reads MAT-file into <code>MLArray</code> objects.
 * 
 * Usage:
 * <pre><code>
 * //read in the file
 * MatFileReader mfr = new MatFileReader( "mat_file.mat" );
 * 
 * //get array of a name "my_array" from file
 * MLArray mlArrayRetrived = mfr.getMLArray( "my_array" );
 * 
 * //or get the collection of all arrays that were stored in the file
 * Map content = mfr.getContent();
 * </pre></code>
 * 
 * @see ca.mjdsystems.jmatio.io.MatFileFilter
 * @author Wojciech Gradkowski (<a href="mailto:wgradkowski@gmail.com">wgradkowski@gmail.com</a>)
 */
/**
 * @author Wojciech Gradkowski (<a
 *         href="mailto:wgradkowski@gmail.com">wgradkowski@gmail.com</a>)
 * 
 */
public class MatFileReader {
	public static final int MEMORY_MAPPED_FILE = 1;
	public static final int DIRECT_BYTE_BUFFER = 2;
	public static final int HEAP_BYTE_BUFFER = 4;

	/**
	 * Type of matlab mat file.
	 */
	private final MatFileType matType;
	/**
	 * MAT-file header
	 */
	MatFileHeader matFileHeader;
	/**
	 * Container for red <code>MLArray</code>s
	 */
	Map<String, MLArray> data;
	/**
	 * Array name filter
	 */
	private MatFileFilter filter;
	/**
	 * Whether or not we have found an MCOS type variable.  Needed to know if further processing is needed.
	 */
	private Set<MLObjectPlaceholder> mcosToFind = new HashSet<MLObjectPlaceholder>();
	/**
	 * Holds the likely candidate for the MCOS extra data at the end of a MAT file.
	 */
	private MLUInt8 mcosData;

	/**
	 * Creates instance of <code>MatFileReader</code> and reads MAT-file 
	 * from location given as <code>fileName</code>.
	 * 
	 * This method reads MAT-file without filtering.
	 * 
	 * @param fileName the MAT-file path <code>String</code>
	 * @throws IOException when error occurred while processing the file.
	 */
	public MatFileReader(String fileName) throws FileNotFoundException, IOException {
		this(new File(fileName), new MatFileFilter(), MatFileType.Regular);
	}

	/**
	 * Creates instance of <code>MatFileReader</code> and reads MAT-file 
	 * from location given as <code>fileName</code>.
	 * 
	 * Results are filtered by <code>MatFileFilter</code>. Arrays that do not meet
	 * filter match condition will not be available in results.
	 * 
	 * @param fileName the MAT-file path <code>String</code>
	 * @param MatFileFilter array name filter.
	 * @throws IOException when error occurred while processing the file.
	 */
	public MatFileReader(String fileName, MatFileFilter filter) throws IOException {
		this(new File(fileName), filter, MatFileType.Regular);
	}

	/**
	 * Creates instance of <code>MatFileReader</code> and reads MAT-file
	 * from <code>file</code>. 
	 * 
	 * This method reads MAT-file without filtering.
	 * 
	 * @param file the MAT-file
	 * @throws IOException when error occurred while processing the file.
	 */
	public MatFileReader(File file) throws IOException {
		this(file, new MatFileFilter(), MatFileType.Regular);

	}

	/**
	 * Creates instance of <code>MatFileReader</code> and reads MAT-file from
	 * <code>file</code>.
	 * <p>
	 * Results are filtered by <code>MatFileFilter</code>. Arrays that do not
	 * meet filter match condition will not be available in results.
	 * <p>
	 * <i>Note: this method reads file using the memory mapped file policy, see
	 * notes to </code>{@link #read(File, MatFileFilter, com.jmatio.io.MatFileReader.MallocPolicy)}</code>
	 * 
	 * @param file
	 *            the MAT-file
	 * @param MatFileFilter
	 *            array name filter.
	 * @throws IOException
	 *             when error occurred while processing the file.
	 */
	public MatFileReader(File file, MatFileFilter filter, MatFileType matType) throws IOException {
		this(matType);

		read(file, filter, MEMORY_MAPPED_FILE);
	}

	public MatFileReader(File file, MatFileFilter filter) throws IOException {
		this(file, filter, MatFileType.Regular);
	}

	public MatFileReader(MatFileType matType) {
		this.matType = matType;
		filter = new MatFileFilter();
		data = new LinkedHashMap<String, MLArray>();
	}

	public MatFileReader() {
		this(MatFileType.Regular);
	}

	/**
	 * Creates instance of <code>MatFileReader</code> and reads MAT-file from
	 * <code>file</code>.
	 * 
	 * This method reads MAT-file without filtering.
	 * 
	 * @param stream
	 *            the MAT-file stream
	 * @throws IOException
	 *             when error occurred while processing the file.
	 */
	public MatFileReader(InputStream stream, MatFileType type) throws IOException {
		this(stream, new MatFileFilter(), type);
	}

	/**
	 * Creates instance of <code>MatFileReader</code> and reads MAT-file from
	 * <code>file</code>.
	 * <p>
	 * Results are filtered by <code>MatFileFilter</code>. Arrays that do not
	 * meet filter match condition will not be available in results.
	 * <p>
	 * <i>Note: this method reads file using the memory mapped file policy, see
	 * notes to </code>
	 * {@link #read(File, MatFileFilter, com.jmatio.io.MatFileReader.MallocPolicy)}
	 * </code>
	 * 
	 * @param stream
	 *            the MAT-file stream
	 * @param MatFileFilter
	 *            array name filter.
	 * @throws IOException
	 *             when error occurred while processing the file.
	 */
	public MatFileReader(InputStream stream, MatFileFilter filter, MatFileType type) throws IOException {
		this(type);

		read(stream, filter);
	}

	/**
	 * Reads the content of a MAT-file and returns the mapped content.
	 * <p>
	 * This method calls
	 * <code>read(file, new MatFileFilter(), MallocPolicy.MEMORY_MAPPED_FILE)</code>.
	 * 
	 * @param file
	 *            a valid MAT-file file to be read
	 * @return the same as <code>{@link #getContent()}</code>
	 * @throws IOException
	 *             if error occurs during file processing
	 */
	public synchronized Map<String, MLArray> read(File file) throws IOException {
		return read(file, new MatFileFilter(), MEMORY_MAPPED_FILE);
	}

	/**
	 * Reads the content of a MAT-file and returns the mapped content.
	 * <p>
	 * This method calls <code>read(stream, new MatFileFilter())</code>.
	 * 
	 * @param stream
	 *            a valid MAT-file stream to be read
	 * @return the same as <code>{@link #getContent()}</code>
	 * @throws IOException
	 *             if error occurs during file processing
	 */
	public synchronized Map<String, MLArray> read(InputStream stream) throws IOException {
		return read(stream, new MatFileFilter());
	}

	/**
	 * Reads the content of a MAT-file and returns the mapped content.
	 * <p>
	 * This method calls
	 * <code>read(file, new MatFileFilter(), policy)</code>.
	 * 
	 * @param file
	 *            a valid MAT-file file to be read
	 * @param policy
	 *            the file memory allocation policy
	 * @return the same as <code>{@link #getContent()}</code>
	 * @throws IOException
	 *             if error occurs during file processing
	 */
	public synchronized Map<String, MLArray> read(File file, int policy) throws IOException {
		return read(file, new MatFileFilter(), policy);
	}

	/**
	 * Reads the content of a MAT-file and returns the mapped content.
	 * <p>
	 * Because of java bug <a
	 * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038">#4724038</a>
	 * which disables releasing the memory mapped resource, additional different
	 * allocation modes are available.
	 * <ul>
	 * <li><code>{@link #MEMORY_MAPPED_FILE}</code> - a memory mapped file</li>
	 * <li><code>{@link #DIRECT_BYTE_BUFFER}</code> - a uses
	 * <code>{@link ByteBuffer#allocateDirect(int)}</code> method to read in
	 * the file contents</li>
	 * <li><code>{@link #HEAP_BYTE_BUFFER}</code> - a uses
	 * <code>{@link ByteBuffer#allocate(int)}</code> method to read in the
	 * file contents</li>
	 * </ul>
	 * <i>Note: memory mapped file will try to invoke a nasty code to relase
	 * it's resources</i>
	 * 
	 * @param file
	 *            a valid MAT-file file to be read
	 * @param filter
	 *            the array filter applied during reading
	 * @param policy
	 *            the file memory allocation policy
	 * @return the same as <code>{@link #getContent()}</code>
	 * @see MatFileFilter
	 * @throws IOException
	 *             if error occurs during file processing
	 */
	private static final int DIRECT_BUFFER_LIMIT = 1 << 25;

	public synchronized Map<String, MLArray> read(File file, MatFileFilter filter, int policy) throws IOException {
		return read(new RandomAccessFile(file, "r"), filter, policy);
	}

	public synchronized Map<String, MLArray> read(RandomAccessFile raFile, MatFileFilter filter, int policy) throws IOException {
		this.filter = filter;

		//clear the results
		for (String key : data.keySet()) {
			data.remove(key);
		}

		FileChannel roChannel = null;
		ByteBuffer buf = null;
		try {
			//Create a read-only memory-mapped file
			roChannel = raFile.getChannel();
			// until java bug #4715154 is fixed I am not using memory mapped files
			// The bug disables re-opening the memory mapped files for writing
			// or deleting until the VM stops working. In real life I need to open
			// and update files
			switch (policy) {
			case DIRECT_BYTE_BUFFER:
				buf = ByteBuffer.allocateDirect((int) roChannel.size());
				roChannel.read(buf, 0);
				buf.rewind();
				break;
			case HEAP_BYTE_BUFFER:
				int filesize = (int) roChannel.size();
				buf = ByteBuffer.allocate(filesize);

				// The following two methods couldn't be used (at least under MS Windows)
				// since they are implemented in a suboptimal way. Each of them
				// allocates its own _direct_ buffer of exactly the same size,
				// the buffer passed as parameter has, reads data into it and
				// only afterwards moves data into the buffer passed as parameter.
				// roChannel.read(buf, 0);        // ends up in outOfMemory
				// raFile.readFully(buf.array()); // ends up in outOfMemory
				int numberOfBlocks = filesize / DIRECT_BUFFER_LIMIT + ((filesize % DIRECT_BUFFER_LIMIT) > 0 ? 1 : 0);
				if (numberOfBlocks > 1) {
					ByteBuffer tempByteBuffer = ByteBuffer.allocateDirect(DIRECT_BUFFER_LIMIT);
					for (long block = 0; block < numberOfBlocks; block++) {
						tempByteBuffer.clear();
						roChannel.read(tempByteBuffer, block * DIRECT_BUFFER_LIMIT);
						tempByteBuffer.flip();
						buf.put(tempByteBuffer);
					}
					tempByteBuffer = null;
				} else
					roChannel.read(buf, 0);

				buf.rewind();
				break;
			case MEMORY_MAPPED_FILE:
				buf = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int) roChannel.size());
				break;
			default:
				throw new IllegalArgumentException("Unknown file allocation policy");
			}
			// Do the actual work.
			parseData(buf);

			return getContent();
		} catch (IOException e) {
			throw e;
		} finally {
			if (buf != null && buf.isDirect()) {
				// Forcefully unmap memory mapped buffer or direct buffer. This is a
				// workaround for <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038">#4724038</a>.
				// Note that subsequent accesses to the buffer will crash the runtime, so it may
				// only be applied to internal buffers.
				Unsafe9R.invokeCleaner(buf);
			}
			if (roChannel != null) {
				roChannel.close();
			}
			if (raFile != null) {
				raFile.close();
			}
		}
	}

	private void parseData(ByteBuffer buf) throws IOException {
		//read in file header
		readHeader(buf);

		while (buf.remaining() > 0) {
			readData(buf);
		}
		if (!mcosToFind.isEmpty()) {
			parseMCOS(mcosData, mcosToFind);
			if (data.get("@") == mcosData) {
				data.remove("@");
			}
			for (Map.Entry<String, MLArray> it : data.entrySet()) {
				if (it.getValue() == mcosData) {
					data.remove(it.getKey());
					break;
				}
			}
		}
		mcosData = null;
		mcosToFind.clear();
	}

	private static void parseMCOS(MLUInt8 mcosData, Set<MLObjectPlaceholder> mcosPlaceholders) throws IOException {
		// First, parse back out the mcosData.
		ByteBuffer buffer = mcosData.getRealByteBuffer();
		ByteBufferInputStream dataStream = new ByteBufferInputStream(buffer, buffer.limit());

		MatFileReader matFile = new MatFileReader(dataStream, MatFileType.ReducedHeader);
		Map<String, MLArray> mcosContent = matFile.getContent();
		MLCell mcosInfo = (MLCell) ((MLStructure) mcosContent.get("@0")).getField("MCOS");
		ByteBuffer mcosDataBuf = ((MLUInt8) mcosInfo.get(0)).getRealByteBuffer();
		// This bytebuffer needs to be read in the byte order of the MAT file order.  Thus fix.
		mcosDataBuf.order(matFile.getMatFileHeader().getByteOrder());

		// Parse out the data buffer.  First get version information.  Should always equal 2.
		int version = mcosDataBuf.getInt();
		if (version != 2) {
			throw new IllegalStateException("MAT file's MCOS data has a different version(?).  Got: " + version + ", wanted 2.");
		}

		// Get the string count + define the string array.
		int strCount = mcosDataBuf.getInt();
		String[] strs = new String[strCount];

		// Get the segment indexes.
		int segmentIndexes[] = new int[6];
		for (int i = 0; i < segmentIndexes.length; ++i) {
			segmentIndexes[i] = mcosDataBuf.getInt();
		}

		// There should now be 8 0 bytes.  Make sure this is true to avoid object format changes.
		if (mcosDataBuf.getLong() != 0) {
			throw new IllegalStateException("MAT file's MCOS data has different byte values for unknown fields!  Aborting!");
		}

		// Finally, read in each string.  Java doesn't provide an easy way to do this in bulk, so just use a stupid formula for now.
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strCount; ++i) {
			sb.setLength(0);
			char next = (char) mcosDataBuf.get();
			while (next != '\0') {
				sb.append(next);
				next = (char) mcosDataBuf.get();
			}
			strs[i] = sb.toString();
		}

		// Sanity check, next 8 byte aligned position in the buffer should equal the start of the first segment!
		if (((mcosDataBuf.position() + 0x07) & ~0x07) != segmentIndexes[0]) {
			throw new IllegalStateException("Data from the strings section was not all read!");
		}

		// First segment, class information.  Really just need the class names.
		List<String> classNamesList = new ArrayList<String>();
		mcosDataBuf.position(segmentIndexes[0]);
		// There are 16 unknown bytes.  Ensure they are 0.
		if (mcosDataBuf.getLong() != 0 || mcosDataBuf.getLong() != 0) {
			throw new IllegalStateException("MAT file's MCOS data has different byte values for unknown fields!  Aborting!");
		}
		while (mcosDataBuf.position() < segmentIndexes[1]) {
			mcosDataBuf.getInt(); // packageNameIndex - Unused for now.
			int classNameIndex = mcosDataBuf.getInt();
			String className = strs[classNameIndex - 1];
			classNamesList.add(className);
			if (mcosDataBuf.getLong() != 0) {
				throw new IllegalStateException("MAT file's MCOS data has different byte values for unknown fields!  Aborting!");
			}
		}

		// Sanity check, position in the buffer should equal the start of the second segment!
		if (mcosDataBuf.position() != segmentIndexes[1]) {
			throw new IllegalStateException("Data from the class section was not all read!");
		}

		// @todo: Second segment, Object properties containing other properties.  Not used yet, thus ignored.
		mcosDataBuf.position(segmentIndexes[2]);

		// Third segment.  Contains all the useful per-object information.
		Map<Integer, MatMCOSObjectInformation> objectInfoList = new HashMap<Integer, MatMCOSObjectInformation>();
		// There are 24 unknown bytes.  Ensure they are 0.
		if (mcosDataBuf.getLong() != 0 || mcosDataBuf.getLong() != 0 || mcosDataBuf.getLong() != 0) {
			throw new IllegalStateException("MAT file's MCOS data has different byte values for unknown fields!  Aborting!");
		}
		int objectCount = 1;
		while (mcosDataBuf.position() < segmentIndexes[3]) {
			// First fetch the data.
			int classIndex = mcosDataBuf.getInt();
			if (mcosDataBuf.getLong() != 0) {
				throw new IllegalStateException("MAT file's MCOS data has different byte values for unknown fields!  Aborting!");
			}
			int segment2Index = mcosDataBuf.getInt();
			int segment4Index = mcosDataBuf.getInt();
			mcosDataBuf.getInt(); // This value is random.  But we need to move the buffer forward, so read it without a check.
			int objectId = objectCount++; // It would appear that the "objectId" is in fact some other MATLAB value.  Thus ignore,
			// and use the index into this segment as the id instead.

			// Then parse it into the form needed for the object.
			objectInfoList.put(objectId - 1, new MatMCOSObjectInformation(classNamesList.get(classIndex - 1), classIndex, objectId, segment2Index, segment4Index));
		}

		// Sanity check, position in the buffer should equal the start of the fourth segment!
		if (mcosDataBuf.position() != segmentIndexes[3]) {
			throw new IllegalStateException("Data from the object section was not all read!  At: " + mcosDataBuf.position() + ", wanted: " + segmentIndexes[3]);
		}

		// Fourth segment.  Contains the regular properties for objects.
		// There are 8 unknown bytes.  Ensure they are 0.
		if (mcosDataBuf.getLong() != 0) {
			throw new IllegalStateException("MAT file's MCOS data has different byte values for unknown fields!  Aborting!");
		}
		List<Map<String, MLArray>> segment4Properties = new ArrayList<Map<String, MLArray>>();
		while (mcosDataBuf.position() < segmentIndexes[4]) {
			Map<String, MLArray> properties = new HashMap<String, MLArray>();
			int propertiesCount = mcosDataBuf.getInt();
			for (int i = 0; i < propertiesCount; ++i) {
				int nameIndex = mcosDataBuf.getInt();
				int flag = mcosDataBuf.getInt();
				int heapIndex = mcosDataBuf.getInt();

				String propertyName = strs[nameIndex - 1];
				MLArray property;
				switch (flag) {
				case 0:
					property = new MLChar(propertyName, strs[heapIndex - 1]);
					break;
				case 1:
					property = mcosInfo.get(heapIndex + 2);
					break;
				case 2:
					// @todo: Handle a boolean.
					throw new UnsupportedOperationException("Mat file parsing does not yet support booleans!");
				default:
					throw new UnsupportedOperationException("Don't yet support parameter type: " + flag + "!");
				}
				if (property instanceof MLUInt32) {
					int[][] data = ((MLUInt32) property).getArray();
					if (data[0][0] == 0xdd000000 && data[1][0] == 0x02) {
						MLObjectPlaceholder objHolder = new MLObjectPlaceholder(propertyName, "", data);
						mcosPlaceholders.add(objHolder);
						property = objHolder;
					}
				}
				properties.put(propertyName, property);
			}
			segment4Properties.add(properties);
			mcosDataBuf.position((mcosDataBuf.position() + 0x07) & ~0x07);
		}

		// Sanity check, position in the buffer should equal the start of the fifth segment!
		if (mcosDataBuf.position() != segmentIndexes[4]) {
			throw new IllegalStateException("Data from the properties section (2) was not all read!  At: " + mcosDataBuf.position() + ", wanted: " + segmentIndexes[4]);
		}

		// Now merge in the properties from segment 4 into object.
		for (MatMCOSObjectInformation it : objectInfoList.values()) {
			Map<String, MLArray> objAttributes = it.structure;
			if (it.segment4PropertiesIndex > 0) {
				for (Map.Entry<String, MLArray> attribute : segment4Properties.get(it.segment4PropertiesIndex - 1).entrySet()) {
					objAttributes.put(attribute.getKey(), attribute.getValue());
				}
			} else {
				throw new IllegalStateException("Properties are not found!  Not sure where to look ...");
			}
		}

		// Finally, merge in attributes from the global grab bag.
		MLCell attribBag = (MLCell) mcosInfo.get(mcosInfo.getSize() - 1); // Get the grab bag.
		for (MatMCOSObjectInformation it : objectInfoList.values()) {
			MLStructure attributes = (MLStructure) attribBag.get(it.classId);
			Collection<String> attributeNames = attributes.getFieldNames();
			Map<String, MLArray> objAttributes = it.structure;
			for (String attributeName : attributeNames) {
				if (objAttributes.get(attributeName) == null) {
					objAttributes.put(attributeName, attributes.getField(attributeName));
				}
			}
		}

		for (MLObjectPlaceholder placeholder : mcosPlaceholders) {
			processMCOS(placeholder, classNamesList, objectInfoList);
		}
	}

	private static void processMCOS(MLObjectPlaceholder objHolder, List<String> classNamesList, Map<Integer, MatMCOSObjectInformation> objectInfoList) {
		int classId = objHolder.classId;
		MLObject obj = new MLObject(objHolder.name, classNamesList.get(classId - 1), objHolder.getDimensions(), 0);
		for (int i = 0; i < obj.getSize(); ++i) {
			MatMCOSObjectInformation objectInformation = objectInfoList.get(objHolder.objectIds[i] - 1);
			if (classId != objectInformation.classId) {
				throw new IllegalStateException("Found an object in array with a different class id! Actual: " + objectInformation.classId + ", expected: " + classId + "!");
			}
			obj.setFields(i, objectInformation.structure);
		}
		objHolder.setTarget(obj);
	}

	/**
	 * Read a mat file from a stream. Internally this will read the stream fully
	 * into memory before parsing it.
	 * 
	 * @param stream
	 *            a valid MAT-file stream to be read
	 * @param filter
	 *            the array filter applied during reading
	 * 
	 * @return the same as <code>{@link #getContent()}</code>
	 * @see MatFileFilter
	 * @throws IOException
	 *             if error occurs during file processing
	 */
	public synchronized Map<String, MLArray> read(InputStream stream, MatFileFilter filter) throws IOException {
		this.filter = filter;

		data.clear();

		ByteBuffer buf = null;

		final ByteArrayOutputStream2 baos = new ByteArrayOutputStream2();
		copy(stream, baos);
		buf = ByteBuffer.wrap(baos.getBuf(), 0, baos.getCount());

		// Do the actual work
		parseData(buf);

		return getContent();
	}

	private void copy(InputStream stream, ByteArrayOutputStream2 output) throws IOException {
		final byte[] buffer = new byte[1024 * 4];
		int n = 0;
		while (-1 != (n = stream.read(buffer))) {
			output.write(buffer, 0, n);
		}
	}

	/**
	 * Gets MAT-file header
	 * 
	 * @return - a <code>MatFileHeader</code> object
	 */
	public MatFileHeader getMatFileHeader() {
		return matFileHeader;
	}

	/**
	 * Returns list of <code>MLArray</code> objects that were inside MAT-file
	 * 
	 * @return - a <code>ArrayList</code>
	 * @deprecated use <code>getContent</code> which returns a Map to provide 
	 *             easier access to <code>MLArray</code>s contained in MAT-file
	 */
	public ArrayList<MLArray> getData() {
		return new ArrayList<MLArray>(data.values());
	}

	/**
	 * Returns the value to which the red file maps the specified array name.
	 * 
	 * Returns <code>null</code> if the file contains no content for this name.
	 * 
	 * @param - array name
	 * @return - the <code>MLArray</code> to which this file maps the specified name, 
	 *           or null if the file contains no content for this name.
	 */
	public MLArray getMLArray(String name) {
		return data.get(name);
	}

	/**
	 * Returns a map of <code>MLArray</code> objects that were inside MAT-file.
	 * 
	 * MLArrays are mapped with MLArrays' names
	 *  
	 * @return - a <code>Map</code> of MLArrays mapped with their names.
	 */
	public Map<String, MLArray> getContent() {
		return data;
	}

	/**
	 * Reads data form byte buffer. Searches for either
	 * <code>miCOMPRESSED</code> data or <code>miMATRIX</code> data.
	 * 
	 * Compressed data are inflated and the product is recursively passed back
	 * to this same method.
	 * 
	 * Modifies <code>buf</code> position.
	 * 
	 * @param buf -
	 *            input byte buffer
	 * @throws IOException when error occurs while reading the buffer.
	 */
	void readData(ByteBuffer buf) throws IOException {
		//read data
		ISMatTag tag = new ISMatTag(buf);
		switch (tag.type) {
		case MatDataTypes.miCOMPRESSED:
			int numOfBytes = tag.size;
			//inflate and recur
			if (buf.remaining() < numOfBytes) {
				throw new MatlabIOException("Compressed buffer length miscalculated!");
			}
			//instead of standard Inlater class instance I use an inflater input
			//stream... gives a great boost to the performance
			InflaterInputStream iis = new InflaterInputStream(new ByteBufferInputStream(buf, numOfBytes));

			//process data decompression
			byte[] result = new byte[1024];

			HeapBufferDataOutputStream dos = new HeapBufferDataOutputStream();
			int i;
			try {
				do {
					i = iis.read(result, 0, result.length);
					int len = Math.max(0, i);
					dos.write(result, 0, len);
				} while (i > 0);
			} catch (EOFException eofe) {
				System.out.println("EOFException detected!");
			} catch (IOException e) {
				throw new MatlabIOException("Could not decompress data: " + e);
			} finally {
				iis.close();
				dos.flush();
			}
			//create a ByteBuffer from the deflated data
			ByteBuffer out = dos.getByteBuffer();

			//with proper byte ordering
			out.order(matFileHeader.getByteOrder());

			try {
				readData(out);
			} catch (IOException e) {
				throw e;
			} finally {
				dos.close();
			}
			break;
		case MatDataTypes.miMATRIX:
			//read in the matrix
			int pos = buf.position();

			MLArray element = readMatrix(buf, true);
			if (element != null) {
				// Sometimes a MAT file will contain more than one unnamed
				// element.  This ensures that all of them will be accessible
				// in the end result.
				if (!data.containsKey(element.getName())) {
					data.put(element.getName(), element);
				}
				if (element.getName() == MLArray.DEFAULT_NAME) {
					// identity comparison is okay because we assigned it in the first place
					int nextIndex = 0;
					for (; data.containsKey("@" + nextIndex); nextIndex++) {}
					data.put(MLArray.DEFAULT_NAME + nextIndex, element);
				}
			} else {
				int read = buf.position() - pos;
				int toRead = tag.size - read;
				buf.position(buf.position() + toRead);
			}
			int read = buf.position() - pos;
			int toRead = tag.size - read;
			if (toRead != 0) {
				throw new MatlabIOException("Matrix was not read fully! " + toRead + " remaining in the buffer.");
			}
			break;
		default:
			throw new MatlabIOException("Incorrect data tag: " + tag);
		}
	}

	/**
	 * Reads miMATRIX from from input stream.
	 * 
	 * If reading was not finished (which is normal for filtered results)
	 * returns <code>null</code>.
	 * 
	 * Modifies <code>buf</code> position to the position when reading
	 * finished.
	 * 
	 * Uses recursive processing for some ML**** data types.
	 * 
	 * @param buf -
	 *            input byte buffer
	 * @param isRoot -
	 *            when <code>true</code> informs that if this is a top level
	 *            matrix
	 * @return - <code>MLArray</code> or <code>null</code> if matrix does
	 *         not match <code>filter</code>
	 * @throws IOException when error occurs while reading the buffer.
	 */
	private MLArray readMatrix(ByteBuffer buf, boolean isRoot) throws IOException {
		//result
		MLArray mlArray;
		ISMatTag tag;

		//read flags
		int[] flags = readFlags(buf);
		int attributes = (flags.length != 0) ? flags[0] : 0;
		int nzmax = (flags.length != 0) ? flags[1] : 0;
		int type = attributes & 0xff;

		//read Array dimension
		int[] dims = readDimension(buf);

		//read array Name
		String name = readName(buf, matFileHeader);

		//if this array is filtered out return immediately
		if (isRoot && !filter.matches(name)) {
			return null;
		}

		//read data >> consider changing it to stategy pattern
		switch (type) {
		case MLArray.mxSTRUCT_CLASS:

			MLStructure struct = new MLStructure(name, dims, attributes);

			//field name lenght - this subelement always uses the compressed data element format
			new ISMatTag(buf);
			int maxlen = buf.getInt(); //maximum field length

			//////  read fields data as Int8
			tag = new ISMatTag(buf);
			//calculate number of fields
			int numOfFields = tag.size / maxlen;

			String[] fieldNames = new String[numOfFields];
			for (int i = 0; i < numOfFields; i++) {
				byte[] names = new byte[maxlen];
				buf.get(names);
				fieldNames[i] = zeroEndByteArrayToString(names);
			}
			buf.position(buf.position() + tag.padding);
			//read fields
			for (int index = 0; index < struct.getM() * struct.getN(); index++) {
				for (int i = 0; i < numOfFields; i++) {
					//read matrix recursively
					tag = new ISMatTag(buf);

					MLArray array;
					if (tag.size > 0) {
						array = readMatrix(buf, false);
					} else {
						array = new MLEmptyArray();
					}
					array.name = fieldNames[i];
					struct.setField(fieldNames[i], array, index);
				}
			}
			mlArray = struct;
			break;
		case MLArray.mxCELL_CLASS:
			MLCell cell = new MLCell(name, dims, type, attributes);
			for (int i = 0; i < cell.getM() * cell.getN(); i++) {
				tag = new ISMatTag(buf);
				if (tag.size > 0) {
					//read matrix recursively
					MLArray cellmatrix = readMatrix(buf, false);
					cell.set(cellmatrix, i);
				} else {
					cell.set(new MLEmptyArray(), i);
				}
			}
			mlArray = cell;
			break;
		case MLArray.mxDOUBLE_CLASS:
			mlArray = new MLDouble(name, dims, type, attributes);
			//read real
			tag = new ISMatTag(buf);
			tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getRealByteBuffer(), (MLNumericArray<?>) mlArray);
			//read complex
			if (mlArray.isComplex()) {
				tag = new ISMatTag(buf);
				tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getImaginaryByteBuffer(),
						(MLNumericArray<?>) mlArray);
			}
			break;
		case MLArray.mxSINGLE_CLASS:
			mlArray = new MLSingle(name, dims, type, attributes);
			//read real
			tag = new ISMatTag(buf);
			tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getRealByteBuffer(), (MLNumericArray<?>) mlArray);
			//read complex
			if (mlArray.isComplex()) {
				tag = new ISMatTag(buf);
				tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getImaginaryByteBuffer(),
						(MLNumericArray<?>) mlArray);
			}
			break;
		case MLArray.mxUINT8_CLASS:
			mlArray = new MLUInt8(name, dims, type, attributes);
			//read real
			tag = new ISMatTag(buf);
			tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getRealByteBuffer(), (MLNumericArray<?>) mlArray);
			//read complex
			if (mlArray.isComplex()) {
				tag = new ISMatTag(buf);
				tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getImaginaryByteBuffer(),
						(MLNumericArray<?>) mlArray);
			}

			// This might be the MCOS extra data.  If there is no name, set it as the current set of data.
			if (name.equals("")) {
				mcosData = (MLUInt8) mlArray;
			}

			break;
		case MLArray.mxINT8_CLASS:
			mlArray = new MLInt8(name, dims, type, attributes);
			//read real
			tag = new ISMatTag(buf);
			tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getRealByteBuffer(), (MLNumericArray<?>) mlArray);
			//read complex
			if (mlArray.isComplex()) {
				tag = new ISMatTag(buf);
				tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getImaginaryByteBuffer(),
						(MLNumericArray<?>) mlArray);
			}
			break;
		case MLArray.mxINT16_CLASS:
			mlArray = new MLInt16(name, dims, type, attributes);
			//read real
			tag = new ISMatTag(buf);
			tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getRealByteBuffer(), (MLNumericArray<?>) mlArray);
			//read complex
			if (mlArray.isComplex()) {
				tag = new ISMatTag(buf);
				tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getImaginaryByteBuffer(),
						(MLNumericArray<?>) mlArray);
			}
			break;
		case MLArray.mxUINT16_CLASS:
			mlArray = new MLUInt16(name, dims, type, attributes);
			//read real
			tag = new ISMatTag(buf);
			tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getRealByteBuffer(), (MLNumericArray<?>) mlArray);
			//read complex
			if (mlArray.isComplex()) {
				tag = new ISMatTag(buf);
				tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getImaginaryByteBuffer(),
						(MLNumericArray<?>) mlArray);
			}
			break;
		case MLArray.mxINT32_CLASS:
			mlArray = new MLInt32(name, dims, type, attributes);
			//read real
			tag = new ISMatTag(buf);
			tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getRealByteBuffer(), (MLNumericArray<?>) mlArray);
			//read complex
			if (mlArray.isComplex()) {
				tag = new ISMatTag(buf);
				tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getImaginaryByteBuffer(),
						(MLNumericArray<?>) mlArray);
			}
			break;
		case MLArray.mxUINT32_CLASS:
			mlArray = new MLUInt32(name, dims, type, attributes);
			//read real
			tag = new ISMatTag(buf);
			tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getRealByteBuffer(), (MLNumericArray<?>) mlArray);
			//read complex
			if (mlArray.isComplex()) {
				tag = new ISMatTag(buf);
				tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getImaginaryByteBuffer(),
						(MLNumericArray<?>) mlArray);
			}
			break;
		case MLArray.mxINT64_CLASS:
			mlArray = new MLInt64(name, dims, type, attributes);
			//read real
			tag = new ISMatTag(buf);
			tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getRealByteBuffer(), (MLNumericArray<?>) mlArray);
			//read complex
			if (mlArray.isComplex()) {
				tag = new ISMatTag(buf);
				tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getImaginaryByteBuffer(),
						(MLNumericArray<?>) mlArray);
			}
			break;
		case MLArray.mxUINT64_CLASS:
			mlArray = new MLUInt64(name, dims, type, attributes);
			//read real
			tag = new ISMatTag(buf);
			tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getRealByteBuffer(), (MLNumericArray<?>) mlArray);
			//read complex
			if (mlArray.isComplex()) {
				tag = new ISMatTag(buf);
				tag.readToByteBuffer(((MLNumericArray<?>) mlArray).getImaginaryByteBuffer(),
						(MLNumericArray<?>) mlArray);
			}
			break;
		case MLArray.mxCHAR_CLASS:
			MLChar mlchar = new MLChar(name, dims, type, attributes);

			//read real
			tag = new ISMatTag(buf);
			//                char[] ac = tag.readToCharArray();
			String str = tag.readToString(matFileHeader.getByteOrder());

			for (int i = 0; i < str.length(); i++) {
				mlchar.setChar(str.charAt(i), i);
			}
			mlArray = mlchar;
			break;
		case MLArray.mxSPARSE_CLASS:
			MLSparse sparse = new MLSparse(name, dims, attributes, nzmax);
			//read ir (row indices)
			tag = new ISMatTag(buf);
			int[] ir = tag.readToIntArray();
			//read jc (column count)
			tag = new ISMatTag(buf);
			int[] jc = tag.readToIntArray();

			if (sparse.isComplex()) {
				//read pr (real part)
				tag = new ISMatTag(buf);
				double[] ad1 = tag.readToDoubleArray();
				int count = 0;
				for (int column = 0; column < sparse.getN(); column++) {
					while (count < jc[column + 1]) {
						sparse.setReal(ad1[count], ir[count], column);
						count++;
					}
				}

				tag = new ISMatTag(buf);
				double[] ad2 = tag.readToDoubleArray();

				count = 0;
				for (int column = 0; column < sparse.getN(); column++) {
					while (count < jc[column + 1]) {
						sparse.setImaginary(ad2[count], ir[count], column);
						count++;
					}
				}
			} else {
				//read pi (real part)
				tag = new ISMatTag(buf);
				double[] ad1 = tag.readToDoubleArray();
				int count = 0;
				for (int column = 0; column < sparse.getN(); column++) {
					while (count < jc[column + 1]) {
						sparse.set(ad1[count], ir[count], column);
						count++;
					}
				}
			}
			mlArray = sparse;
			break;

		case MLArray.mxOPAQUE_CLASS:
			//read class name
			tag = new ISMatTag(buf);
			// class name
			String className = tag.readToString(matFileHeader.getByteOrder());

			// the stored array name 
			// read array name stored in dims (!)
			byte[] nn = new byte[dims.length];
			for (int i = 0; i < dims.length; i++) {
				nn[i] = (byte) dims[i];
			}
			String arrName = new String(nn, MatDataTypes.CHARSET);

			// next tag should be miMatrix
			ISMatTag contentTag = new ISMatTag(buf);

			if (contentTag.type == MatDataTypes.miMATRIX) {
				if (name.equals("java")) {
					//should return UInt8 or UInt32, but MLNumericArray is the LCD
					MLArray wrappedContent = readMatrix(buf, false);

					//our first job is to find the binary content
					MLNumericArray<?> binaryContent = null;
					if (wrappedContent instanceof MLCell) {
						//sometimes we'll get a cell array
						//in that case, we'll take the first NumericArray we can find
						MLCell cellContent = (MLCell) wrappedContent;
						for (MLArray candidate : cellContent.cells()) {
							if (candidate instanceof MLNumericArray) {
								binaryContent = (MLNumericArray<?>) candidate;
								break;
							}
						}
					} else if (wrappedContent instanceof MLNumericArray) {
						binaryContent = (MLNumericArray<?>) wrappedContent;
					} else if (wrappedContent instanceof MLStructure) {
						MLStructure structureContent = (MLStructure) wrappedContent;
						MLCell cellContent = (MLCell) structureContent.getField("Values", 0);
						binaryContent = (MLNumericArray<?>) cellContent.get(0);
					} else {
						throw new IOException("Unexpected array type: " + wrappedContent.name);
					}

					mlArray = new MLJavaObject(arrName, className, binaryContent);
				} else if (name.equals("MCOS")) {
					// FileWrapper__ is a special MATLAB internal name.  Should never appear from users.
					if (!className.equals("FileWrapper__")) {
						MLUInt32 content = (MLUInt32) readMatrix(buf, false);
						int[][] t = content.getArray();

						// Check that the first four numbers are the same, as expected.
						if (t[0][0] != 0xdd000000 || t[1][0] != 2) {
							throw new IOException("MCOS per-object header was different then expected!  Got: " + content.contentToString());
						}

						MLObjectPlaceholder placeholder = new MLObjectPlaceholder(arrName, className, t);
						mcosToFind.add(placeholder);
						mlArray = placeholder;
					} else {
						// This is where we get the useful MCOS data.  Only used on FileWrapper__ classes.
						mlArray = readMatrix(buf, false);
					}
				} else if (name.equals("handle")) {
					MLCell wrappedContent = (MLCell) readMatrix(buf, true);
					mlArray = new MLHandle(arrName, className, wrappedContent);
				} else {
					throw new IOException("Unknown object type (" + name + ") found.");
				}
			} else {
				throw new IOException("Unexpected object content");
			}
			break;
		case MLArray.mxOBJECT_CLASS:
			//read class name
			tag = new ISMatTag(buf);

			// class name
			className = tag.readToString(matFileHeader.getByteOrder());

			// TODO: currently copy pasted from structure

			mlArray = new MLObject(name, className, dims, attributes);

			//field name lenght - this subelement always uses the compressed data element format
			tag = new ISMatTag(buf);
			maxlen = buf.getInt(); //maximum field length

			//////  read fields data as Int8
			tag = new ISMatTag(buf);
			//calculate number of fields
			numOfFields = tag.size / maxlen;

			fieldNames = new String[numOfFields];
			for (int i = 0; i < numOfFields; i++) {
				byte[] names = new byte[maxlen];
				buf.get(names);
				fieldNames[i] = zeroEndByteArrayToString(names);
			}
			buf.position(buf.position() + tag.padding);
			//read fields
			for (int index = 0; index < mlArray.getM() * mlArray.getN(); index++) {
				for (int i = 0; i < numOfFields; i++) {
					//read matrix recursively
					tag = new ISMatTag(buf);

					MLArray array;
					if (tag.size > 0) {
						array = readMatrix(buf, false);
					} else {
						array = new MLEmptyArray();
					}
					array.name = fieldNames[i];
					((MLObject) mlArray).setField(fieldNames[i], array, index);
				}
			}
			break;
		default:
			throw new MatlabIOException("Incorrect matlab array class: " + MLArray.typeToString(type));
		}
		return mlArray;
	}

	/**
	 * Converts byte array to <code>String</code>. 
	 * 
	 * It assumes that String ends with \0 value.
	 * 
	 * @param bytes byte array containing the string.
	 * @return String retrieved from byte array.
	 * @throws IOException if reading error occurred.
	 */
	private static String zeroEndByteArrayToString(byte[] bytes) throws IOException {
		int i = 0;
		while (i < bytes.length && bytes[i] != 0) {
			++i;
		}
		return new String(bytes, 0, i, MatDataTypes.CHARSET);
	}

	/**
	 * Reads Matrix flags.
	 * 
	 * Modifies <code>buf</code> position.
	 * 
	 * @param buf <code>ByteBuffer</code>
	 * @return flags int array
	 * @throws IOException if reading from buffer fails
	 */
	private static int[] readFlags(ByteBuffer buf) throws IOException {
		ISMatTag tag = new ISMatTag(buf);
		int[] flags = tag.readToIntArray();
		return flags;
	}

	/**
	 * Reads Matrix dimensions.
	 * 
	 * Modifies <code>buf</code> position.
	 * 
	 * @param buf <code>ByteBuffer</code>
	 * @return dimensions int array
	 * @throws IOException if reading from buffer fails
	 */
	private static int[] readDimension(ByteBuffer buf) throws IOException {
		ISMatTag tag = new ISMatTag(buf);
		int[] dims = tag.readToIntArray();
		return dims;
	}

	/**
	 * Reads Matrix name.
	 * 
	 * Modifies <code>buf</code> position.
	 * 
	 * @param buf <code>ByteBuffer</code>
	 * @return name <code>String</code>
	 * @throws IOException if reading from buffer fails
	 */
	private static String readName(ByteBuffer buf, MatFileHeader header) throws IOException {
		ISMatTag tag = new ISMatTag(buf);
		return tag.readToString(header.getByteOrder());
	}

	/**
	 * Reads MAT-file header.
	 * 
	 * Modifies <code>buf</code> position.
	 * 
	 * @param buf
	 *            <code>ByteBuffer</code>
	 * @throws IOException
	 *             if reading from buffer fails or if this is not a valid
	 *             MAT-file
	 */
	void readHeader(ByteBuffer buf) throws IOException {
		//header values
		String description;
		byte[] endianIndicator = new byte[2];

		// This part of the header is missing if the file isn't a regular mat file.  So ignore.
		if (matType == MatFileType.Regular) {
			//descriptive text 116 bytes
			byte[] descriptionBuffer = new byte[116];
			buf.get(descriptionBuffer);

			description = zeroEndByteArrayToString(descriptionBuffer);

			if (!description.matches("MATLAB 5.0 MAT-file.*")) {
				throw new MatlabIOException("This is not a valid MATLAB 5.0 MAT-file.");
			}

			//subsyst data offset 8 bytes
			buf.position(buf.position() + 8);
		} else {
			description = "Simulink generated MATLAB 5.0 MAT-file"; // Default simulink description.
		}

		byte[] bversion = new byte[2];
		//version 2 bytes
		buf.get(bversion);

		//endian indicator 2 bytes
		buf.get(endianIndicator);

		matFileHeader = MatFileHeader.parseFrom(description, bversion, endianIndicator);
		buf.order(matFileHeader.getByteOrder());

		// After the header, the next read must be aligned.  Thus force the alignment.  Only matters with reduced header data,
		// but apply it regardless for safety.
		buf.position((buf.position() + 7) & 0xfffffff8);
	}

	/**
	 * TAG operator. Facilitates reading operations.
	 * 
	 * <i>Note: reading from buffer modifies it's position</i>
	 * 
	 * @author Wojciech Gradkowski (<a href="mailto:wgradkowski@gmail.com">wgradkowski@gmail.com</a>)
	 */
	private static class ISMatTag extends MatTag {
		private final MatFileInputStream mfis;
		private final int padding;
		private final boolean compressed;

		public ISMatTag(ByteBuffer buf) throws IOException {
			//must call parent constructor
			super(0, 0);
			int tmp = buf.getInt();

			//data not packed in the tag
			if (tmp >> 16 == 0) {
				type = tmp;
				size = buf.getInt();
				compressed = false;
			} else { //data _packed_ in the tag (compressed)
				size = tmp >> 16; // 2 more significant bytes
				type = tmp & 0xffff; // 2 less significant bytes;
				compressed = true;
			}
			padding = getPadding(size, compressed);
			mfis = new MatFileInputStream(buf, type);
		}

		public void readToByteBuffer(ByteBuffer buff, ByteStorageSupport<?> storage) throws IOException {
			int elements = size / sizeOf();
			mfis.readToByteBuffer(buff, elements, storage);
			mfis.skip(padding);
		}

		public byte[] readToByteArray() throws IOException {
			//allocate memory for array elements
			int elements = size / sizeOf();
			byte[] ab = new byte[elements];

			for (int i = 0; i < elements; i++) {
				ab[i] = mfis.readByte();
			}

			//skip padding
			mfis.skip(padding);

			return ab;
		}

		public double[] readToDoubleArray() throws IOException {
			//allocate memory for array elements
			int elements = size / sizeOf();
			double[] ad = new double[elements];

			for (int i = 0; i < elements; i++) {
				ad[i] = mfis.readDouble();
			}

			//skip padding

			mfis.skip(padding);
			return ad;
		}

		public int[] readToIntArray() throws IOException {
			//allocate memory for array elements
			int elements = size / sizeOf();
			int[] ai = new int[elements];

			for (int i = 0; i < elements; i++) {
				ai[i] = mfis.readInt();
			}

			//skip padding
			mfis.skip(padding);
			return ai;
		}

		private String charset(ByteOrder byteOrder) {
			switch (type) {
			case MatDataTypes.miUTF8:
				return "UTF-8";
			case MatDataTypes.miUTF16:
				return byteOrder == ByteOrder.BIG_ENDIAN ? "UTF-16BE" : "UTF-16LE";
			case MatDataTypes.miUTF32:
				return byteOrder == ByteOrder.BIG_ENDIAN ? "UTF-32BE" : "UTF-32LE";
			default:
				return "US-ASCII";
			}
		}

		public String readToString(ByteOrder byteOrder) throws IOException {
			byte[] bytes = readToByteArray();
			return new String(bytes, charset(byteOrder));
		}
	}
}
