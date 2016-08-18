/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.jmatio.common.DeterministicKeyMap;

public class DeterministicKeyMapTest {
	@Test
	public void testOrdering() {
		Set<Integer> ordering = new LinkedHashSet<Integer>();
		DeterministicKeyMap<Integer, String> map = new DeterministicKeyMap<Integer, String>(ordering, new LinkedHashMap<Integer, String>());
		Assert.assertEquals(true, map.keySet().isEmpty());
		Assert.assertEquals(true, map.values().isEmpty());
		Assert.assertEquals(true, map.entrySet().isEmpty());

		// add them to the map in one order
		map.put(1, "1");
		map.put(2, "2");
		map.put(3, "3");

		{
			// and pull them out in another
			ordering.add(3);
			ordering.add(2);
			ordering.add(1);

			List<Integer> keys = new ArrayList<Integer>(map.keySet());
			List<String> values = new ArrayList<String>(map.values());
			Assert.assertEquals(Arrays.asList(3, 2, 1), keys);
			Assert.assertEquals(Arrays.asList("3", "2", "1"), values);
			List<Map.Entry<Integer, String>> entries = new ArrayList<Map.Entry<Integer, String>>(map.entrySet());
			Assert.assertEquals(keys.size(), entries.size());
			for (int i = 0; i < keys.size(); ++i) {
				Assert.assertEquals(keys.get(i), entries.get(i).getKey());
				Assert.assertEquals(values.get(i), entries.get(i).getValue());
			}
		}

		{
			// now pull them out in a different order
			ordering.clear();
			ordering.add(2);
			ordering.add(3);
			ordering.add(1);

			List<Integer> keys = new ArrayList<Integer>(map.keySet());
			List<String> values = new ArrayList<String>(map.values());
			Assert.assertEquals(Arrays.asList(2, 3, 1), keys);
			Assert.assertEquals(Arrays.asList("2", "3", "1"), values);
			List<Map.Entry<Integer, String>> entries = new ArrayList<Map.Entry<Integer, String>>(map.entrySet());
			Assert.assertEquals(keys.size(), entries.size());
			for (int i = 0; i < keys.size(); ++i) {
				Assert.assertEquals(keys.get(i), entries.get(i).getKey());
				Assert.assertEquals(values.get(i), entries.get(i).getValue());
			}
		}

		{
			// now remove an object from the map
			map.remove(3);

			List<Integer> keys = new ArrayList<Integer>(map.keySet());
			List<String> values = new ArrayList<String>(map.values());
			Assert.assertEquals(Arrays.asList(2, 1), keys);
			Assert.assertEquals(Arrays.asList("2", "1"), values);
			List<Map.Entry<Integer, String>> entries = new ArrayList<Map.Entry<Integer, String>>(map.entrySet());
			Assert.assertEquals(keys.size(), entries.size());
			for (int i = 0; i < keys.size(); ++i) {
				Assert.assertEquals(keys.get(i), entries.get(i).getKey());
				Assert.assertEquals(values.get(i), entries.get(i).getValue());
			}
		}
	}
}
