package com.mosaic.collections;

import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Test;
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
@RunWith(JUnitMosaicRunner.class)
@SuppressWarnings({"UnusedDeclaration","unchecked"})
public class FastStackTest {


    private final Generator arraySizeGenerator = PrimitiveGenerators.integers(1,100);
    private final Generator stringGenerator    = CombinedGenerators.arrays(PrimitiveGenerators.strings(), arraySizeGenerator, String.class );

    private FastStack stack = new FastStack();

    @Before
    public void resetStack() {
        stack.clear();
    }

    @Test
    public void emptyStack_size_expectZero() {
        assertEquals( 0, stack.size() );
    }


//    @Test( generators={"stringGenerator"} )  // todo currently fails as method is called n times in a row without calling before/after methods
    public void pushPeek_expectPeekToReturnLastValue( String[] values ) {
        assertEquals( 0, stack.size() );
        assertTrue( stack.isEmpty() );

        pushAll( values );

        assertEquals( values.length, stack.size() );

        assertSame( values[values.length-1], stack.peek() );
        assertEquals( values.length, stack.size() );
    }

    @Test( memCheck=true, generators={"stringGenerator"} )
    public void pushValues_clearThem_expectValuesToBecomeGCable( String[] values ) {
        pushAll( values );

        stack.clear();
    }

    // popTest

    private void pushAll(String[] values) {
        for ( String v : values ) {
            stack.push(v);
        }
    }

}
