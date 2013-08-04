package com.mosaic.lang.math;

import org.junit.Test;

import static junit.framework.Assert.*;

/**
 *
 */
public class MoneyTest {
    @Test
    public void testIsGTZero() throws Exception {
        assertTrue( new Money(10).isGTZero() );
    }

    @Test
    public void getDouble() throws Exception {
        assertEquals( 10.0,  new Money("10").asDouble(), 0.002 );
        assertEquals( 10.0,  new Money(10).asDouble(), 0.002 );
        assertEquals( 10.0, new Money( 10.0 ).asDouble(), 0.002 );
        assertEquals( 10.0,  Money.gbp( 10 ).asDouble(), 0.002 );
        assertEquals(  0.10, Money.pence( 10 ).asDouble(), 0.002 );
    }

    @Test
    public void getInt() throws Exception {
        assertEquals( 10, new Money("10").asInt() );
        assertEquals( 10, new Money(10).asInt() );
        assertEquals( 10, new Money(10.0).asInt() );
        assertEquals( 10, Money.gbp(10).asInt() );
        assertEquals(  0, Money.pence(10).asInt() );
    }
}
