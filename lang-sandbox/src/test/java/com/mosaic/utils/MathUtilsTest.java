package com.mosaic.utils;

import org.junit.Test;

import static com.mosaic.utils.MathUtils.*;
import static org.junit.Assert.*;

/**
 *
 */
public class MathUtilsTest {

    @Test
    public void testRoundUpToClosestPowerOf2() {
        assertEquals( 2, roundUpToClosestPowerOf2( -1 ) );
        assertEquals( 2, roundUpToClosestPowerOf2( 0 ) );
        assertEquals( 2, roundUpToClosestPowerOf2( 1 ) );
        assertEquals( 2, roundUpToClosestPowerOf2( 2 ) );
        assertEquals( 4, roundUpToClosestPowerOf2( 3 ) );
        assertEquals( 4, roundUpToClosestPowerOf2( 4 ) );
        assertEquals( 8, roundUpToClosestPowerOf2( 5 ) );
        assertEquals( 8, roundUpToClosestPowerOf2( 6 ) );
        assertEquals( 8, roundUpToClosestPowerOf2( 7 ) );
        assertEquals( 8, roundUpToClosestPowerOf2( 8 ) );
        assertEquals( 16, roundUpToClosestPowerOf2( 9 ) );
        assertEquals( 32, roundUpToClosestPowerOf2( 18 ) );
    }

    @Test
    public void testRoundDownToClosestPowerOf2() {
        assertEquals( 2, roundDownToClosestPowerOf2( -1 ) );
        assertEquals( 2, roundDownToClosestPowerOf2( 0 ) );
        assertEquals( 2, roundDownToClosestPowerOf2( 1 ) );
        assertEquals( 2, roundDownToClosestPowerOf2( 2 ) );
        assertEquals( 2, roundDownToClosestPowerOf2( 3 ) );
        assertEquals( 4, roundDownToClosestPowerOf2( 4 ) );
        assertEquals( 4, roundDownToClosestPowerOf2( 5 ) );
        assertEquals( 4, roundDownToClosestPowerOf2( 6 ) );
        assertEquals( 4, roundDownToClosestPowerOf2( 7 ) );
        assertEquals( 8, roundDownToClosestPowerOf2( 8 ) );
        assertEquals( 8, roundDownToClosestPowerOf2( 9 ) );
        assertEquals( 16, roundDownToClosestPowerOf2( 18 ) );
    }

    @Test
    public void testIsPowerOf2() {
        assertFalse( isPowerOf2( -1 ) );
        assertFalse( isPowerOf2( 0 ) );
        assertFalse( isPowerOf2( 1 ) );
        assertTrue( isPowerOf2( 2 ) );
        assertFalse( isPowerOf2( 3 ) );
        assertTrue( isPowerOf2( 4 ) );
        assertFalse( isPowerOf2( 5 ) );
        assertFalse( isPowerOf2( 6 ) );
        assertFalse( isPowerOf2( 7 ) );
        assertTrue( isPowerOf2( 8 ) );
        assertFalse( isPowerOf2( 9 ) );
        assertFalse( isPowerOf2( 18 ) );
    }

    @Test
    public void testCharactersLengthOfBytes() {
        assertEquals( 1, charactersLengthOf( (byte) 0 ) );
        assertEquals( 1, charactersLengthOf( (byte) 1 ) );
        assertEquals( 1, charactersLengthOf( (byte) 2 ) );

        for ( byte i=0; i<10; i++ ) {
            assertEquals( Integer.toString(i), 1, charactersLengthOf( i ) );
        }
        for ( byte i=10; i<100; i++ ) {
            assertEquals( 2, charactersLengthOf( i ) );
        }
        for ( byte i=100; i<Byte.MAX_VALUE; i++ ) {
            assertEquals( 3, charactersLengthOf( i ) );
        }

        assertEquals( 2, charactersLengthOf( (byte) -1 ) );

        for ( byte i=-1; i>-10; i-- ) {
            assertEquals( 2, charactersLengthOf( i ) );
        }

        for ( byte i=-10; i>-100; i-- ) {
            assertEquals( 3, charactersLengthOf( i ) );
        }

        assertEquals( 4, charactersLengthOf( Byte.MIN_VALUE ) );
    }

    @Test
    public void testCharactersLengthOfLong() {
        assertEquals( 1, charactersLengthOf( 0 ) );
        assertEquals( 1, charactersLengthOf( 1 ) );
        assertEquals( 1, charactersLengthOf( 2 ) );

        for ( int i=0; i<10; i++ ) {
            assertEquals( Integer.toString(i), 1, charactersLengthOf( i ) );
        }
        for ( int i=10; i<100; i++ ) {
            assertEquals( 2, charactersLengthOf( i ) );
        }
        for ( int i=100; i<1000; i++ ) {
            assertEquals( 3, charactersLengthOf( i ) );
        }

        assertEquals( 19, charactersLengthOf( Long.MAX_VALUE ) );

        assertEquals( 2, charactersLengthOf( -1 ) );

        for ( int i=-1; i>-10; i-- ) {
            assertEquals( 2, charactersLengthOf( i ) );
        }

        for ( int i=-10; i>-100; i-- ) {
            assertEquals( 3, charactersLengthOf( i ) );
        }

        assertEquals( 20, charactersLengthOf( Long.MIN_VALUE ) );
    }
}
