package com.jmatio.common.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jmatio.types.MLCell;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLStructure;

public class MLArrayQueryTest
{
    
    @Test
    public void testStruct()
    {
        MLStructure struct = new MLStructure( "test", new int[] {1,2} );
        
        struct.setField( "d1", new MLDouble( "@", new double[] { 1.0, 2.0, 3.0}, 3 ), 0);
        struct.setField( "d1", new MLDouble( "@", new double[] { 4.0, 5.0, 6.0}, 3 ), 1 );
        
        struct.setField( "s1", new MLChar( "@", new String[] { "test1", "test2" } ), 0);
        struct.setField( "s1", new MLChar( "@", new String[] { "test4", "test5" } ), 1 );

        struct.setField( "c1", new MLCell( "@", new int[] { 1, 2} ), 0);
        struct.setField( "c1", new MLCell( "@", new int[] { 1, 2} ), 1 );
        
        
    }
    
    @Test
    public void test()
    {
        fail( "Not yet implemented" );
    }

}
