package com.mosaic.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class BigCashTypeTest {

    @Test
    public void roundUp() {
        assertEquals( 421200, BigCashType.roundUp(421133L) );
        assertEquals( 421100, BigCashType.roundUp(421100L) );
        assertEquals( 421200, BigCashType.roundUp(421101L) );

        assertEquals( -421200, BigCashType.roundUp(-421133L) );
        assertEquals( -421100, BigCashType.roundUp(-421100L) );
        assertEquals( -421200, BigCashType.roundUp(-421101L) );

        assertEquals( 1200, BigCashType.roundUp(1133L) );
        assertEquals( 1100, BigCashType.roundUp(1100L) );
        assertEquals( 1200, BigCashType.roundUp(1101L) );

        assertEquals( -1200, BigCashType.roundUp(-1133L) );
        assertEquals( -1100, BigCashType.roundUp(-1100L) );
        assertEquals( -1200, BigCashType.roundUp(-1101L) );
    }

    @Test
    public void roundDown() {
        assertEquals( 421100, BigCashType.roundDown(421133L) );
        assertEquals( 421100, BigCashType.roundDown(421100L) );
        assertEquals( 421100, BigCashType.roundDown(421101L) );

        assertEquals( -421100, BigCashType.roundDown(-421133L) );
        assertEquals( -421100, BigCashType.roundDown(-421100L) );
        assertEquals( -421100, BigCashType.roundDown(-421101L) );

        assertEquals( 1100, BigCashType.roundDown(1133L) );
        assertEquals( 1100, BigCashType.roundDown(1100L) );
        assertEquals( 1100, BigCashType.roundDown(1101L) );

        assertEquals( -1100, BigCashType.roundDown(-1133L) );
        assertEquals( -1100, BigCashType.roundDown(-1100L) );
        assertEquals( -1100, BigCashType.roundDown(-1101L) );
    }

    @Test
    public void roundClosest() {
        assertEquals( 421100, BigCashType.roundClosest(421149L) );
        assertEquals( 421100, BigCashType.roundClosest(421100L) );
        assertEquals( 421200, BigCashType.roundClosest(421150L) );
        assertEquals( 421200, BigCashType.roundClosest(421199L) );


        assertEquals( -421100, BigCashType.roundClosest(-421149L) );
        assertEquals( -421100, BigCashType.roundClosest(-421100L) );
        assertEquals( -421200, BigCashType.roundClosest(-421150L) );
        assertEquals( -421200, BigCashType.roundClosest(-421199L) );

        assertEquals( 1100, BigCashType.roundClosest(1149L) );
        assertEquals( 1100, BigCashType.roundClosest(1100L) );
        assertEquals( 1200, BigCashType.roundClosest(1150L) );
        assertEquals( 1200, BigCashType.roundClosest(1199L) );

        assertEquals( -1100, BigCashType.roundClosest(-1149L) );
        assertEquals( -1100, BigCashType.roundClosest(-1100L) );
        assertEquals( -1200, BigCashType.roundClosest(-1150L) );
        assertEquals( -1200, BigCashType.roundClosest(-1199L) );
    }

    @Test
    public void toStringTest() {
        assertEquals( "0.00", BigCashType.toString(20) );
        assertEquals( "0.01", BigCashType.toString(120) );
        assertEquals( "0.01", BigCashType.toString(190) );
        assertEquals( "0.11", BigCashType.toString(1190) );
        assertEquals( "1.11", BigCashType.toString(11190) );
        assertEquals( "55.42", BigCashType.toString(554290) );
        assertEquals( "922337203685477.58", BigCashType.toString(Long.MAX_VALUE) );

        assertEquals( "0.00", BigCashType.toString(-20) );
        assertEquals( "-0.01", BigCashType.toString(-120) );
        assertEquals( "-0.01", BigCashType.toString(-190) );
        assertEquals( "-0.11", BigCashType.toString(-1190) );
        assertEquals( "-1.11", BigCashType.toString(-11190) );
        assertEquals( "-55.42", BigCashType.toString(-554290) );
        assertEquals( "-922337203685477.58", BigCashType.toString(Long.MIN_VALUE) );
    }

}
