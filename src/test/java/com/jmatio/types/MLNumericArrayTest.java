/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.types;

import org.junit.Assert;
import org.junit.Test;

public class MLNumericArrayTest {
	@Test
	public void testMultipleDimArray() {
		int[] dims = new int[]{3, 4, 5};
		MLInt32 multidim = new MLInt32("multiDimTest", dims);
		for (int i = 0; i < dims[0]; ++i) {
			for (int j = 0; j < dims[1]; ++j) {
				for (int k = 0; k < dims[2]; ++k) {
					int value = i * j * k;
					multidim.set(value, i, j, k);
					Assert.assertEquals(value, multidim.get(i, j, k).intValue());
				}
			}
		}
	}
}
