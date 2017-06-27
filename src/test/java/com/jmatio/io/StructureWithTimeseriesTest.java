/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.io;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.jmatio.types.MLArray;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLInt8;
import com.jmatio.types.MLObject;
import com.jmatio.types.MLStructure;
import com.jmatio.types.MLStructureObjectBase;

/** 
 * This test verifies {@link <a href="https://github.com/diffplug/matfilerw/issues/13">issue #13</a>}.
 * <p>
 * During tests the introspection of data did not show essential vectors.
 * It comes out that the properties are propagated, but their corresponding
 * names are not propagated to {@link MLStructureObjectBase#getFieldNames()}.
 *
 * @author Piotr Smolinski <piotr.smolinski.77@gmail.com>
 */
@RunWith(JUnit4.class)
public class StructureWithTimeseriesTest {

	/**
	 * Tests reading of a timeseries object contained in a struct.
	 */
	@Test
	public void testReadingTimeSeries() throws Exception {

		MatFileReader reader = new MatFileReader(
				getClass().getResourceAsStream("/timeseries.mat"),
				MatFileType.Regular);

		Map<String, MLArray> content = reader.getContent();

		assertThat(content.get("s"), is(instanceOf(MLStructure.class)));
		MLStructure s = (MLStructure) content.get("s");

		assertThat(s.getField("test"), is(instanceOf(MLObject.class)));
		MLObject test = (MLObject) s.getField("test");
		assertThat(test.getClassName(), equalTo("timeseries"));

		assertThat(test.getField("Data_"), is(instanceOf(MLDouble.class)));
		MLDouble data = (MLDouble) test.getField("Data_");
		assertThat(data.getSize(), equalTo(5));

		assertThat(test.getField("Quality_"), is(instanceOf(MLInt8.class)));
		MLInt8 quality = (MLInt8) test.getField("Quality_");
		assertThat(quality.getSize(), equalTo(5));

		assertThat(test.getField("Time_"), isMatlabUndefined());

		assertThat(test.getField("DataInfo"), not(isMatlabUndefined()));
		assertThat(test.getField("DataInfo"), is(instanceOf(MLObject.class)));
		MLObject dataInfo = (MLObject) test.getField("DataInfo");
		assertThat(dataInfo.getClassName(), equalTo("datametadata"));

		assertThat(dataInfo.getField("Interpolation"), is(instanceOf(MLObject.class)));
		assertThat(dataInfo.getFieldNames(), hasItem("Interpolation"));

		assertThat(test.getField("QualityInfo"), not(isMatlabUndefined()));
		assertThat(test.getField("QualityInfo"), is(instanceOf(MLObject.class)));
		MLObject qualityInfo = (MLObject) test.getField("QualityInfo");
		assertThat(qualityInfo.getClassName(), equalTo("qualmetadata"));

		assertThat(qualityInfo.getField("Version"), is(instanceOf(MLDouble.class)));
		assertThat(qualityInfo.getFieldNames(), hasItem("Version"));

		assertThat(test.getField("TimeInfo"), not(isMatlabUndefined()));
		assertThat(test.getField("TimeInfo"), is(instanceOf(MLObject.class)));
		MLObject timeInfo = (MLObject) test.getField("TimeInfo");
		assertThat(timeInfo.getClassName(), equalTo("timemetadata"));

		// apparently this works even when #13 fix is not applied
		assertThat(timeInfo.getField("Time_"), is(instanceOf(MLDouble.class)));
		// but this fails
		assertThat(timeInfo.getFieldNames(), hasItem("Time_"));
		MLDouble timeInfoTime = (MLDouble) timeInfo.getField("Time_");
		assertThat(timeInfoTime.getSize(), equalTo(5));

		assertThat(timeInfo.getField("Units"), is(instanceOf(MLChar.class)));
		assertThat(timeInfo.getFieldNames(), hasItem("Units"));
		MLChar timeInfoUnits = (MLChar) timeInfo.getField("Units");
		assertThat(timeInfoUnits.getString(0), equalTo("seconds"));

	}

	/**
	 * Tests if given {@link MLArray} is undefined in sense of JMatIO.
	 * Actually this should just return null, but in the JMatIO empty
	 * properties are resolved to empty double vector
	 */
	private Matcher<MLArray> isMatlabUndefined() {
		return CoreMatchers.allOf(
				is(instanceOf(MLDouble.class)),
				new CustomMatcher<MLArray>("empty double vector") {
					@Override
					public boolean matches(Object item) {
						if (!(item instanceof MLDouble))
							return false;
						return ((MLDouble) item).getSize() == 0;
					}
				});
	}

}
