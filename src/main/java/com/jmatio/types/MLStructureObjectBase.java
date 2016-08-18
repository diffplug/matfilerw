/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.jmatio.common.DeterministicKeyMap;

/**
 * Base class for MLStructure and MLObject.
 * 
 * Note: array of structures can contain only structures of the same type
 * , that means structures must have the same field names.
 */
public abstract class MLStructureObjectBase extends MLArray {
	/** A Set that keeps structure field names */
	protected Set<String> keys = new LinkedHashSet<String>();
	/** Array of structures */
	protected SortedMap<Integer, DeterministicKeyMap<String, MLArray>> mlStructArray = new TreeMap<Integer, DeterministicKeyMap<String, MLArray>>();
	/** Current structure pointer for bulk insert */
	protected int currentIndex = 0;

	protected MLStructureObjectBase(String name, int[] dims, int type, int attributes) {
		super(name, dims, type, attributes);
	}

	/**
	 * Sets field for current structure
	 * 
	 * @param name - name of the field
	 * @param value - <code>MLArray</code> field value
	 */
	public void setField(String name, MLArray value) {
		setField(name, value, currentIndex);
	}

	/**
	 * Sets field for (m,n)'th structure in struct array
	 * 
	 * @param name - name of the field
	 * @param value - <code>MLArray</code> field value
	 * @param m
	 * @param n
	 */
	public void setField(String name, MLArray value, int m, int n) {
		setField(name, value, getIndex(m, n));
	}

	/**
	 * Sets filed for structure described by index in struct array
	 * 
	 * @param name - name of the field
	 * @param value - <code>MLArray</code> field value
	 * @param index
	 */
	public void setField(String name, MLArray value, int index) {
		keys.add(name);
		currentIndex = index;

		DeterministicKeyMap<String, MLArray> map = mlStructArray.get(index);
		if (map == null) {
			map = new DeterministicKeyMap<String, MLArray>(keys, new HashMap<String, MLArray>(keys.size()));
			mlStructArray.put(index, map);
		}
		map.put(name, value);
	}

	/**
	 * Gets the maximum length of field descriptor
	 * 
	 * @return
	 */
	public int getMaxFieldLenth() {
		//get max field name
		int maxLen = 0;
		for (String s : keys) {
			maxLen = Math.max(maxLen, s.length());
		}
		return maxLen + 1;
	}

	/**
	 * Dumps field names to byte array. Field names are written as Zero End Strings
	 * 
	 * @return
	 */
	public byte[] getKeySetToByteArray() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		char[] buffer = new char[getMaxFieldLenth()];

		try {
			for (String s : keys) {
				Arrays.fill(buffer, (char) 0);
				System.arraycopy(s.toCharArray(), 0, buffer, 0, s.length());
				dos.writeBytes(new String(buffer));
			}
		} catch (IOException e) {
			System.err.println("Could not write Structure key set to byte array: " + e);
			return new byte[0];
		}
		return baos.toByteArray();

	}

	/**
	 * Gets all field from sruct array as flat list of fields.
	 * 
	 * @return
	 */
	public Collection<MLArray> getAllFields() {
		ArrayList<MLArray> fields = new ArrayList<MLArray>();

		for (Map<String, MLArray> struct : mlStructArray.values()) {
			fields.addAll(struct.values());
		}
		return fields;
	}

	/**
	 * Returns the {@link Collection} of keys for this structure.
	 * @return the {@link Collection} of keys for this structure
	 */
	public Collection<String> getFieldNames() {
		return new LinkedHashSet<String>(keys);
	}

	/**
	 * Gets a value of the field described by name from current struct
	 * in struct array or null if the field doesn't exist.
	 * 
	 * @param name
	 * @return
	 */
	public MLArray getField(String name) {
		return getField(name, currentIndex);
	}

	/** Returns all the fields for the given index. */
	public Map<String, MLArray> getFields(int i) {
		return mlStructArray.get(i);
	}

	/**
	 * Gets a value of the field described by name from (m,n)'th struct
	 * in struct array or null if the field doesn't exist.
	 * 
	 * @param name
	 * @param m
	 * @param n
	 * @return
	 */
	public MLArray getField(String name, int m, int n) {
		return getField(name, getIndex(m, n));
	}

	/**
	 * Gets a value of the field described by name from index'th struct
	 * in struct array or null if the field doesn't exist.
	 * 
	 * @param name
	 * @param index
	 * @return value of the field or null if the field doesn't exist
	 */
	public MLArray getField(String name, int index) {
		if (mlStructArray.isEmpty()) {
			return null;
		}
		return mlStructArray.get(index).get(name);
	}

	/* (non-Javadoc)
	 * @see com.paradigmdesigner.matlab.types.MLArray#contentToString()
	 */
	public String contentToString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name + " = \n");

		if (getM() * getN() == 1) {
			for (String key : keys) {
				sb.append("\t" + key + " : " + getField(key) + "\n");
			}
		} else {
			sb.append("\n");
			sb.append(getM() + "x" + getN());
			sb.append(" struct array with fields: \n");
			for (String key : keys) {
				sb.append("\t" + key + "\n");
			}
		}
		return sb.toString();
	}

}
