package com.mosaic.collections;

import com.mosaic.junitpro.JUnitPro;
import com.mosaic.junitpro.Test;
import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.CombinedGenerators;
import net.java.quickcheck.generator.PrimitiveGenerators;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 *
 */
@RunWith(JUnitPro.class)
@SuppressWarnings("UnusedDeclaration")
public class FastStackTest {


    private final Generator arraySizeGenerator = PrimitiveGenerators.integers(1,100);
    private final Generator stringGenerator    = CombinedGenerators.arrays(PrimitiveGenerators.strings(), arraySizeGenerator, String.class );

    private FastStack stack = new FastStack();

//    @Before
//    public void resetStack() {
//        stack.clear();
//    }

    @Test
    public void emptyStack_size_expectZero() {
        assertEquals( 0, stack.size() );
    }


    // two issues
    // 1) should use of generators call setup/tear down methods between each run?
    // 2) generator of collections do not have their linked objects checked yet

    @Test( memCheck=true, generators={"stringGenerator"} )
    public void pushPeek_expectPeekToReturnLastValue( String[] values ) {
//        assertTrue( stack.isEmpty() );
//        assertEquals( 0, stack.size() );
//
        pushAll( values );
//
//        assertEquals( values.length, stack.size() );
//
//        assertSame( values[values.length-1], stack.peek() );
//        assertEquals( values.length, stack.size() );
//
        stack.clear();
    }

    // popTest

    private void pushAll(String[] values) {
        for ( String v : values ) {
            stack.push(v);
        }
    }

}
