package com.mosaic.lang;

import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class BigCashTypeTest {

    private SystemX system = new DebugSystem();


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
        assertEquals( 421100, BigCashType.roundDown( 421133L ) );
        assertEquals( 421100, BigCashType.roundDown( 421100L ) );
        assertEquals( 421100, BigCashType.roundDown( 421101L ) );

        assertEquals( -421100, BigCashType.roundDown( -421133L ) );
        assertEquals( -421100, BigCashType.roundDown( -421100L ) );
        assertEquals( -421100, BigCashType.roundDown( -421101L ) );

        assertEquals( 1100, BigCashType.roundDown( 1133L ) );
        assertEquals( 1100, BigCashType.roundDown( 1100L ) );
        assertEquals( 1100, BigCashType.roundDown( 1101L ) );

        assertEquals( -1100, BigCashType.roundDown( -1133L ) );
        assertEquals( -1100, BigCashType.roundDown( -1100L ) );
        assertEquals( -1100, BigCashType.roundDown( -1101L ) );
    }

    @Test
    public void roundClosest() {
        assertEquals( 421100, BigCashType.roundClosest( 421149L ) );
        assertEquals( 421100, BigCashType.roundClosest( 421100L ) );
        assertEquals( 421200, BigCashType.roundClosest( 421150L ) );
        assertEquals( 421200, BigCashType.roundClosest( 421199L ) );


        assertEquals( -421100, BigCashType.roundClosest( -421149L ) );
        assertEquals( -421100, BigCashType.roundClosest( -421100L ) );
        assertEquals( -421200, BigCashType.roundClosest( -421150L ) );
        assertEquals( -421200, BigCashType.roundClosest( -421199L ) );

        assertEquals( 1100, BigCashType.roundClosest( 1149L ) );
        assertEquals( 1100, BigCashType.roundClosest(1100L) );
        assertEquals( 1200, BigCashType.roundClosest(1150L) );
        assertEquals( 1200, BigCashType.roundClosest(1199L) );

        assertEquals( -1100, BigCashType.roundClosest(-1149L) );
        assertEquals( -1100, BigCashType.roundClosest( -1100L ) );
        assertEquals( -1200, BigCashType.roundClosest( -1150L ) );
        assertEquals( -1200, BigCashType.roundClosest( -1199L ) );
    }

    @Test
    public void toStringTest() {
        assertEquals( "0.00", BigCashType.toString( 20 ) );
        assertEquals( "0.01", BigCashType.toString( 120 ) );
        assertEquals( "0.01", BigCashType.toString( 190 ) );
        assertEquals( "0.11", BigCashType.toString( 1190 ) );
        assertEquals( "1.11", BigCashType.toString( 11190 ) );
        assertEquals( "55.42", BigCashType.toString( 554290 ) );
        assertEquals( "922337203685477.58", BigCashType.toString( Long.MAX_VALUE ) );

        assertEquals( "0.00", BigCashType.toString( -20 ) );
        assertEquals( "-0.01", BigCashType.toString( -120 ) );
        assertEquals( "-0.01", BigCashType.toString( -190 ) );
        assertEquals( "-0.11", BigCashType.toString( -1190 ) );
        assertEquals( "-1.11", BigCashType.toString( -11190 ) );
        assertEquals( "-55.42", BigCashType.toString( -554290 ) );
        assertEquals( "-922337203685477.58", BigCashType.toString( Long.MIN_VALUE ) );
    }

    @Test
    public void toStringMinor() {
        assertEquals( "0.20", BigCashType.toStringMinor( 20 ) );
        assertEquals( "1.20", BigCashType.toStringMinor( 120 ) );
        assertEquals( "1.90", BigCashType.toStringMinor( 190 ) );
        assertEquals( "11.90", BigCashType.toStringMinor( 1190 ) );
        assertEquals( "111.90", BigCashType.toStringMinor( 11190 ) );
        assertEquals( "5542.90", BigCashType.toStringMinor( 554290 ) );
        assertEquals( "92233720368547758.07", BigCashType.toStringMinor( Long.MAX_VALUE ) );

        assertEquals( "-0.20", BigCashType.toStringMinor( -20 ) );
        assertEquals( "-1.20", BigCashType.toStringMinor( -120 ) );
        assertEquals( "-1.90", BigCashType.toStringMinor( -190 ) );
        assertEquals( "-11.90", BigCashType.toStringMinor( -1190 ) );
        assertEquals( "-111.90", BigCashType.toStringMinor( -11190 ) );
        assertEquals( "-5542.90", BigCashType.toStringMinor( -554290 ) );
        assertEquals( "-92233720368547758.08", BigCashType.toStringMinor( Long.MIN_VALUE ) );
    }

    @Test
    public void toSmallCashType() {
        assertEquals( SmallCashType.fromMajor(123), BigCashType.toSmallCashType(BigCashType.fromMajor(123)) );
    }

    @Test
    public void encodeUsingMajorCodec() {
        assertEquals( "0.00", encodeMajor(0) );
        assertEquals( "0.00", encodeMajor(-11) );
        assertEquals( "-0.01", encodeMajor(-51) );
        assertEquals( "0.01", encodeMajor(123) );
        assertEquals( "0.02", encodeMajor(153) );
        assertEquals( "0.99", encodeMajor(9900) );
        assertEquals( "1.01", encodeMajor(10100) );
        assertEquals( "1.02", encodeMajor(10153) );
        assertEquals( "-1.02", encodeMajor( -10153 ) );
    }

    @Test
    public void encodeUsingMinorCodec() {
        assertEquals( "0.00", encodeMinor( 0 ) );
        assertEquals( "-0.11", encodeMinor( -11 ) );
        assertEquals( "-0.51", encodeMinor( -51 ) );
        assertEquals( "1.23", encodeMinor( 123 ) );
        assertEquals( "1.53", encodeMinor( 153 ) );
        assertEquals( "99.00", encodeMinor( 9900 ) );
        assertEquals( "101.00", encodeMinor( 10100 ) );
        assertEquals( "101.53", encodeMinor( 10153 ) );
        assertEquals( "-101.53", encodeMinor( -10153 ) );
    }

    private String encodeMajor( long amt ) {
        UTF8Builder buf = new UTF8Builder(system);

        BigCashType.CODEC_MAJOR.encode( amt, buf );

        return buf.toString();
    }

    private String encodeMinor( long amt ) {
        UTF8Builder buf = new UTF8Builder(system);

        BigCashType.CODEC_MINOR.encode( amt, buf );

        return buf.toString();
    }
}
