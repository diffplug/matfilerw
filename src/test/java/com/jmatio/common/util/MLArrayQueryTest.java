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

public class MLArrayQueryTest
{
    private File spmFile = new File("src/test/resources/SPM.mat" );
    private MLArray array;
    
    @Before
    public void setUp() throws IOException
    {
        MatFileReader reader = new MatFileReader();
        Map<String, MLArray> content = reader.read( spmFile );
        array = content.get( "SPM" );
    }
    @Test
    public void testStruct() 
    {
        MLStructure actual = (MLStructure) MLArrayQuery.q( array, "SPM" );
        assertEquals( "SPM", actual.getName() );
        assertEquals( 1, actual.getSize() );
    }
    @Test
    public void testStruct2() 
    {
        MLStructure actual = (MLStructure) MLArrayQuery.q( array, "SPM(1,1)" );
        assertEquals( "SPM", actual.getName() );
        assertEquals( 1, actual.getSize() );
    }
    @Test
    public void testStruct3() 
    {
        MLStructure actual = (MLStructure) MLArrayQuery.q( array, "SPM(1)" );
        assertEquals( "SPM", actual.getName() );
        assertEquals( 1, actual.getSize() );
    }
    @Test
    public void testCharArray1() 
    {
        String actual = (String) MLArrayQuery.q( array, "SPM.xCon(1,2).name(1)" );
        assertEquals( "bike", actual );
    }
    @Test
    public void testCharArray2() 
    {
        MLChar actual = (MLChar) MLArrayQuery.q( array, "SPM.xCon(1,2).name" );
        assertEquals( "bike", actual.getString( 0 ) );
    }
    @Test
    public void testCharArray3() 
    {
        char actual = (Character) MLArrayQuery.q( array, "SPM.xCon(1,2).name(1,2)" );
        assertEquals( 'i', actual );
    }
    @Test
    public void testCell()
    {
        MLChar actual = (MLChar) MLArrayQuery.q( array, "SPM.Sess(1,1).U(1,1).name");
        assertEquals( "aquarium", actual.getString(0) );
        
        String str = (String) MLArrayQuery.q( array, "SPM.Sess(1,1).U(1,2).name(1)");
        assertEquals( "bike", str );
    }
    
    
    @Ignore @Test
    public void test() throws IOException
    {
        System.out.println(MLArrayQuery.q( array, "SPM.xCon(1,2).c" ) );
        System.out.println(MLArrayQuery.q( array, "SPM.xCon(1,2).c(2,1)" ) );
        System.out.println(MLArrayQuery.q( array, "SPM.xCon(1,2).name(1)" ) );
        System.out.println(MLArrayQuery.q( array, "SPM.Sess(1,1).U(1,1).name") );
        System.out.println(MLArrayQuery.q( array, "SPM.xY.RT(1)") );
        System.out.println(MLArrayQuery.q( (MLArray) MLArrayQuery.q( array, "SPM.xCon(1,2).c" ),( "c(1,1)" )));
    }

}
