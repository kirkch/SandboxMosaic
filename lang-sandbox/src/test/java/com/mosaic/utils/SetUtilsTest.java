package com.mosaic.utils;

import org.junit.Test;

import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 *
 */
public class SetUtilsTest {
    @Test
    public void testAsSet() throws Exception {
        Set<Integer> emptySet = SetUtils.asSet();

        assertEquals( 0, emptySet.size() );


        Set<Integer> singleSet = SetUtils.asSet(10);
        assertEquals( 1, singleSet.size() );

        Set<Integer> doubleSet = SetUtils.asSet( 2, 3 );
        assertEquals( 2, doubleSet.size() );

        // check is mutable
        doubleSet.add(42);
        assertEquals( 3, doubleSet.size() );

        // todo port asserters into a reusable library and use here
    }

    @Test
    public void testAsImmutableSet() throws Exception {
        Set<Integer> doubleSet = SetUtils.asImmutableSet(2,3);
        assertEquals( 2, doubleSet.size() );

        try {
            doubleSet.add(42);
            fail( "expected UnsupportedOperationException" );
        } catch ( UnsupportedOperationException e ) {
            // expected
        }
    }
}
