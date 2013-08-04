package com.mosaic.lang.math;

import com.mosaic.lang.Cloner;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class IntRangeTest {
    @Test
    public void testContains() {
        IntRange r = new IntRange( 1, 3 );

        assertFalse( r.contains(-1) );
        assertFalse( r.contains(0) );
        assertTrue( r.contains(1) );
        assertTrue( r.contains(2) );
        assertFalse( r.contains(3) );
        assertFalse( r.contains(4) );
    }

    @Test
    public void testIsSerializable() {
        new Cloner().deepCopy( new IntRange(1,3) );
    }
}
