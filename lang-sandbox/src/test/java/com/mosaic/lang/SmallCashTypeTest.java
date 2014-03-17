package com.mosaic.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class SmallCashTypeTest {

    @Test
    public void roundUp() {
        assertEquals( 421140, SmallCashType.roundUp(421133) );
        assertEquals( 421100, SmallCashType.roundUp(421100) );
        assertEquals( 421110, SmallCashType.roundUp(421101) );

        assertEquals( -421140, SmallCashType.roundUp(-421133) );
        assertEquals( -421100, SmallCashType.roundUp(-421100) );
        assertEquals( -421110, SmallCashType.roundUp(-421101) );

        assertEquals( 1140, SmallCashType.roundUp(1133) );
        assertEquals( 1100, SmallCashType.roundUp(1100) );
        assertEquals( 1110, SmallCashType.roundUp(1101) );

        assertEquals( -1140, SmallCashType.roundUp(-1133) );
        assertEquals( -1100, SmallCashType.roundUp(-1100) );
        assertEquals( -1110, SmallCashType.roundUp(-1101) );
    }

    @Test
    public void roundDown() {
        assertEquals( 421130, SmallCashType.roundDown(421133) );
        assertEquals( 421100, SmallCashType.roundDown(421100) );
        assertEquals( 421100, SmallCashType.roundDown(421101) );

        assertEquals( -421130, SmallCashType.roundDown(-421133) );
        assertEquals( -421100, SmallCashType.roundDown(-421100) );
        assertEquals( -421100, SmallCashType.roundDown(-421101) );

        assertEquals( 1130, SmallCashType.roundDown(1133) );
        assertEquals( 1100, SmallCashType.roundDown(1100) );
        assertEquals( 1100, SmallCashType.roundDown(1101) );

        assertEquals( -1130, SmallCashType.roundDown(-1133) );
        assertEquals( -1100, SmallCashType.roundDown(-1100) );
        assertEquals( -1100, SmallCashType.roundDown(-1101) );
    }

    @Test
    public void roundClosest() {
        assertEquals( 421150, SmallCashType.roundClosest(421149) );
        assertEquals( 421100, SmallCashType.roundClosest(421100) );
        assertEquals( 421150, SmallCashType.roundClosest(421150) );
        assertEquals( 421190, SmallCashType.roundClosest(421194) );


        assertEquals( -421150, SmallCashType.roundClosest(-421149) );
        assertEquals( -421100, SmallCashType.roundClosest(-421100) );
        assertEquals( -421150, SmallCashType.roundClosest(-421150) );
        assertEquals( -421190, SmallCashType.roundClosest(-421194) );

        assertEquals( 1150, SmallCashType.roundClosest(1149) );
        assertEquals( 1100, SmallCashType.roundClosest(1100) );
        assertEquals( 1150, SmallCashType.roundClosest(1150) );
        assertEquals( 1190, SmallCashType.roundClosest(1194) );

        assertEquals( -1150, SmallCashType.roundClosest(-1149) );
        assertEquals( -1100, SmallCashType.roundClosest(-1100) );
        assertEquals( -1150, SmallCashType.roundClosest(-1150) );
        assertEquals( -1190, SmallCashType.roundClosest(-1194) );
    }

    @Test
    public void toStringTest() {
        assertEquals( "0.00", SmallCashType.toString(2) );
        assertEquals( "0.01", SmallCashType.toString(12) );
        assertEquals( "0.01", SmallCashType.toString(19) );
        assertEquals( "0.11", SmallCashType.toString(119) );
        assertEquals( "1.11", SmallCashType.toString(1119) );
        assertEquals( "55.42", SmallCashType.toString(55429) );
        assertEquals( "2147483.64", SmallCashType.toString(Integer.MAX_VALUE) );

        assertEquals( "0.00", SmallCashType.toString(-2) );
        assertEquals( "-0.01", SmallCashType.toString(-12) );
        assertEquals( "-0.01", SmallCashType.toString(-19) );
        assertEquals( "-0.11", SmallCashType.toString(-119) );
        assertEquals( "-1.11", SmallCashType.toString(-1119) );
        assertEquals( "-55.42", SmallCashType.toString(-55429) );
        assertEquals( "-2147483.64", SmallCashType.toString(Integer.MIN_VALUE) );
    }

}
