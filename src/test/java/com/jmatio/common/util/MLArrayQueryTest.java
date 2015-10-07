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
package com.jmatio.common.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLStructure;

public class MLArrayQueryTest {
	private File spmFile = new File("src/test/resources/SPM.mat");
	private MLArray array;

	@Before
	public void setUp() throws IOException {
		MatFileReader reader = new MatFileReader();
		Map<String, MLArray> content = reader.read(spmFile);
		array = content.get("SPM");
	}

	@Test
	public void testStruct() {
		MLStructure actual = (MLStructure) MLArrayQuery.q(array, "SPM");
		assertEquals("SPM", actual.getName());
		assertEquals(1, actual.getSize());
	}

	@Test
	public void testStruct2() {
		MLStructure actual = (MLStructure) MLArrayQuery.q(array, "SPM(1,1)");
		assertEquals("SPM", actual.getName());
		assertEquals(1, actual.getSize());
	}

	@Test
	public void testStruct3() {
		MLStructure actual = (MLStructure) MLArrayQuery.q(array, "SPM(1)");
		assertEquals("SPM", actual.getName());
		assertEquals(1, actual.getSize());
	}

	@Test
	public void testCharArray1() {
		String actual = (String) MLArrayQuery.q(array, "SPM.xCon(1,2).name(1)");
		assertEquals("bike", actual);
	}

	@Test
	public void testCharArray2() {
		MLChar actual = (MLChar) MLArrayQuery.q(array, "SPM.xCon(1,2).name");
		assertEquals("bike", actual.getString(0));
	}

	@Test
	public void testCharArray3() {
		char actual = (Character) MLArrayQuery.q(array, "SPM.xCon(1,2).name(1,2)");
		assertEquals('i', actual);
	}

	@Test
	public void testCell() {
		MLChar actual = (MLChar) MLArrayQuery.q(array, "SPM.Sess(1,1).U(1,1).name");
		assertEquals("aquarium", actual.getString(0));

		String str = (String) MLArrayQuery.q(array, "SPM.Sess(1,1).U(1,2).name(1)");
		assertEquals("bike", str);
	}

	@Ignore
	@Test
	public void test() throws IOException {
		System.out.println(MLArrayQuery.q(array, "SPM.xCon(1,2).c"));
		System.out.println(MLArrayQuery.q(array, "SPM.xCon(1,2).c(2,1)"));
		System.out.println(MLArrayQuery.q(array, "SPM.xCon(1,2).name(1)"));
		System.out.println(MLArrayQuery.q(array, "SPM.Sess(1,1).U(1,1).name"));
		System.out.println(MLArrayQuery.q(array, "SPM.xY.RT(1)"));
		System.out.println(MLArrayQuery.q((MLArray) MLArrayQuery.q(array, "SPM.xCon(1,2).c"), ("c(1,1)")));
	}

}
