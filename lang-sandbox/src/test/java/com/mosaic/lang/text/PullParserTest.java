package com.mosaic.lang.text;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.InputBytes;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class PullParserTest {

// Custom parser

    @Test
    public void given123_pullWithCustomParserThatMatchesAndReturnsAnObject_expectResult() {
        Bytes      source = Bytes.wrap("123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        Long v = parser.pullCustom( new NumberParser() );

        assertEquals( 123, v.longValue() );
        assertEquals( 3, parser.getPosition() );
    }

    @Test
    public void given123_pullWithCustomParserThatDoesNotMatch_expectException() {
        Bytes      source = Bytes.wrap("a");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        try {
            parser.pullCustom( new NumberParser() );
            fail("expected exception");
        } catch ( ParseException ex ) {
            assertEquals( "/tmp/file/x (1,1): Expected 'Number'", ex.getMessage() );
            assertEquals( 0L, ex.getOffset() );

            assertEquals( 0, parser.getPosition() );
        }
    }


// pullInt

    @Test
    public void given123_pullInt_expectResult() {
        Bytes      source = Bytes.wrap("123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        int v = parser.pullInt();

        assertEquals( 123, v );
        assertEquals( 3, parser.getPosition() );
    }

    @Test
    public void givenNeg123_pullInt_expectResult() {
        Bytes      source = Bytes.wrap("-123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        int v = parser.pullInt();

        assertEquals( -123, v );
        assertEquals( 4, parser.getPosition() );
    }

    @Test
    public void givenMaxInt_pullInt_expectResult() {
        Bytes      source = Bytes.wrap(Integer.MAX_VALUE+"");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        int v = parser.pullInt();

        assertEquals( Integer.MAX_VALUE, v );
        assertEquals( (Integer.MAX_VALUE+"").length(), parser.getPosition() );
    }

    @Test
    public void givenMinInt_pullInt_expectResult() {
        Bytes      source = Bytes.wrap(Integer.MIN_VALUE+"");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        int v = parser.pullInt();

        assertEquals( Integer.MIN_VALUE, v );
        assertEquals( (Integer.MIN_VALUE+"").length(), parser.getPosition() );
    }

    @Test
    public void givenIntFollowedByText_pullInt_expectResult() {
        Bytes      source = Bytes.wrap("1234abc");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        int v = parser.pullInt();

        assertEquals( 1234, v );
        assertEquals( 4, parser.getPosition() );
    }

    @Test
    public void givenNonInt_pullInt_expectError() {
        Bytes      source = Bytes.wrap("abc");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        try {
            parser.pullInt();
            fail("expected exception" );
        } catch ( ParseException ex ) {

        }
    }

    @Test
    public void givenDoubleNeg_pullInt_expectError() {
        Bytes      source = Bytes.wrap("--123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        try {
            parser.pullInt();
            fail("expected exception" );
        } catch ( ParseException ex ) {

        }
    }

    @Test
    public void givenMaxIntPlus1_pullInt_expectError() {
        Bytes      source = Bytes.wrap((((long) Integer.MAX_VALUE)+1)+"");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        try {
            parser.pullInt();
            fail("expected exception" );
        } catch ( ParseException ex ) {
            assertEquals( "/tmp/file/x (1,1): 'num' (-2147483648) must be >= 0", ex.getMessage() );
        }
    }

// pullLong

    @Test
    public void given123_pullLong_expectResult() {
        Bytes      source = Bytes.wrap("123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        long v = parser.pullLong();

        assertEquals( 123, v );
        assertEquals( 3, parser.getPosition() );
    }

    @Test
    public void givenNeg123_pullLong_expectResult() {
        Bytes      source = Bytes.wrap("-123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        long v = parser.pullLong();

        assertEquals( -123, v );
        assertEquals( 4, parser.getPosition() );
    }

    @Test
    public void givenMaxLong_pullLong_expectResult() {
        Bytes      source = Bytes.wrap(Long.MAX_VALUE+"");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        long v = parser.pullLong();

        assertEquals( Long.MAX_VALUE, v );
        assertEquals( (Long.MAX_VALUE+"").length(), parser.getPosition() );
    }

    @Test
    public void givenMinLong_pullLong_expectResult() {
        Bytes      source = Bytes.wrap(Long.MIN_VALUE+"");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        long v = parser.pullLong();

        assertEquals( Long.MIN_VALUE, v );
        assertEquals( (Long.MIN_VALUE+"").length(), parser.getPosition() );
    }

    @Test
    public void givenLongFollowedByText_pullLong_expectResult() {
        Bytes      source = Bytes.wrap("1234abc");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        long v = parser.pullLong();

        assertEquals( 1234, v );
        assertEquals( 4, parser.getPosition() );
    }

    @Test
    public void givenNonLong_pullLong_expectError() {
        Bytes      source = Bytes.wrap("abc");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        try {
            parser.pullLong();
            fail("expected exception" );
        } catch ( ParseException ex ) {

        }
    }

    @Test
    public void givenDoubleNeg_pullLong_expectError() {
        Bytes      source = Bytes.wrap("--123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        try {
            parser.pullLong();
            fail("expected exception" );
        } catch ( ParseException ex ) {

        }
    }

    @Test
    public void givenMaxLongPlus1_pullLong_expectError() {
        Bytes      source = Bytes.wrap((((long) Integer.MAX_VALUE))+"1");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        try {
            parser.pullInt();
            fail("expected exception" );
        } catch ( ParseException ex ) {
            assertEquals( "/tmp/file/x (1,1): 'num' (-9) must be >= 0", ex.getMessage() );
        }
    }

// pullFloat

    @Test
    public void given123_pullFloat_expectResult() {
        Bytes      source = Bytes.wrap("123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        float v = parser.pullFloat();

        assertEquals( 123, v, 1e9 );
        assertEquals( 3, parser.getPosition() );
    }

    @Test
    public void givenNeg123_pullFloat_expectResult() {
        Bytes      source = Bytes.wrap("-123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        float v = parser.pullFloat();

        assertEquals( -123, v, 1e9 );
        assertEquals( 4, parser.getPosition() );
    }

    @Test
    public void givenMaxFloat_pullFloat_expectResult() {
        String     str    = (Integer.MAX_VALUE/10) + "." + (Integer.MAX_VALUE/10);
        Bytes      source = Bytes.wrap( str );
        PullParser parser = new PullParser( "/tmp/file/x", source );

        float v = parser.pullFloat();

        assertEquals( Float.parseFloat(str), v, 1e9 );
        assertEquals( str.length(), parser.getPosition() );
    }

    @Test
    public void givenMinFloat_pullFloat_expectResult() {
        String     str    = "-"+(Integer.MAX_VALUE/10) + "." + (Integer.MAX_VALUE/10);
        PullParser parser = new PullParser( "/tmp/file/x", Bytes.wrap(str) );

        float v = parser.pullFloat();

        assertEquals( Float.parseFloat(str), v, 1e9 );
        assertEquals( str.length(), parser.getPosition() );
    }

    @Test
    public void givenFloatFollowedByText_pullFloat_expectResult() {
        Bytes      source = Bytes.wrap("1234.12abc");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        float v = parser.pullFloat();

        assertEquals( 1234.12, v, 1e9 );
        assertEquals( 7, parser.getPosition() );
    }

    @Test
    public void givenNonFloat_pullFloat_expectError() {
        Bytes      source = Bytes.wrap("abc");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        try {
            parser.pullFloat();
            fail("expected exception" );
        } catch ( ParseException ex ) {

        }
    }

    @Test
    public void givenDoubleNeg_pullFloat_expectError() {
        Bytes      source = Bytes.wrap("--123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        try {
            parser.pullFloat();
            fail("expected exception" );
        } catch ( ParseException ex ) {

        }
    }

// pullDouble

    @Test
    public void given123_pullDouble_expectResult() {
        Bytes      source = Bytes.wrap("123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        double v = parser.pullDouble();

        assertEquals( 123, v, 1e9 );
        assertEquals( 3, parser.getPosition() );
    }

    @Test
    public void givenNeg123_pullDouble_expectResult() {
        Bytes      source = Bytes.wrap("-123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        double v = parser.pullDouble();

        assertEquals( -123, v, 1e9 );
        assertEquals( 4, parser.getPosition() );
    }

    @Test
    public void givenMaxDouble_pullDouble_expectResult() {
        String     str    = (Integer.MAX_VALUE/10) + "." + (Integer.MAX_VALUE/10);
        Bytes      source = Bytes.wrap( str );
        PullParser parser = new PullParser( "/tmp/file/x", source );

        double v = parser.pullDouble();

        assertEquals( Double.parseDouble(str), v, 1e9 );
        assertEquals( str.length(), parser.getPosition() );
    }

    @Test
    public void givenMinDouble_pullDouble_expectResult() {
        String     str    = "-"+(Integer.MAX_VALUE/10) + "." + (Integer.MAX_VALUE/10);
        PullParser parser = new PullParser( "/tmp/file/x", Bytes.wrap(str) );

        double v = parser.pullDouble();

        assertEquals( Double.parseDouble(str), v, 1e9 );
        assertEquals( str.length(), parser.getPosition() );
    }

    @Test
    public void givenDoubleFollowedByText_pullDouble_expectResult() {
        Bytes      source = Bytes.wrap("1234.12abc");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        double v = parser.pullDouble();

        assertEquals( 1234.12, v, 1e9 );
        assertEquals( 7, parser.getPosition() );
    }

    @Test
    public void givenNonDouble_pullDouble_expectError() {
        Bytes      source = Bytes.wrap("abc");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        try {
            parser.pullDouble();
            fail("expected exception" );
        } catch ( ParseException ex ) {

        }
    }

    @Test
    public void givenDoubleNeg_pullDouble_expectError() {
        Bytes      source = Bytes.wrap("--123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        try {
            parser.pullDouble();
            fail("expected exception" );
        } catch ( ParseException ex ) {

        }
    }


// pullOptional

    @Test
    public void given123_optionallyParseValue_expectValue() {
        Bytes      source = Bytes.wrap("123");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        Long v = parser.pullCustom( new NumberParser() );

        assertEquals( 123, v.longValue() );
        assertEquals( 3, parser.getPosition() );
    }

    @Test
    public void givenABC_optionallyParseValue_expectNull() {
        Bytes      source = Bytes.wrap("abc");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        Long v = parser.optionallyPull( new NumberParser() );

        assertEquals( null, v );
        assertEquals( 0, parser.getPosition() );
    }

// AUTO SKIP

    @Test
    public void givenTwoNumbersSeparatedByWhitespace_autoSkipWhitspace_expectToParseBothValues() {
        Bytes      source = Bytes.wrap("123 456");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        parser.autoSkip( new WhitespaceParser() );

        long v1 = parser.pullInt();

        assertEquals( 123, v1 );
        assertEquals( 3, parser.getPosition() );

        long v2 = parser.pullInt();

        assertEquals( 456, v2 );
        assertEquals( 7, parser.getPosition() );
    }

// REWIND LINE

    @Test
    public void givenMultiLineText_startAt0_rewindLine_expectNoChange() {
        Bytes      source = Bytes.wrap("123\r\n456\r\n789\rabc def\naaa");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        assertEquals( 0, parser.getPosition() );
        assertEquals( 0, parser.rewindLine() );
        assertEquals( 0, parser.getPosition() );
    }

    @Test
    public void givenMultiLineText_startAtEndOfFirstLineButBeforeEOLMarker_rewindLine_expectRewindToStart() {
        Bytes      source = Bytes.wrap("123\r\n456\r\n789\rabc def\naaa");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        parser.setPosition( 3 );

        assertEquals( 3, parser.rewindLine() );
        assertEquals( 0, parser.getPosition() );
    }

    @Test
    public void givenMultiLineText_startAtEndOfFirstLine_rewindLine_expectRewindToStart() {
        Bytes      source = Bytes.wrap("123\r\n456\r\n789\rabc def\naaa");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        parser.setPosition( 5 );

        assertEquals( 5, parser.rewindLine() );
        assertEquals( 0, parser.getPosition() );
    }

    @Test
    public void givenMultiLineText_startAtEndOfSecondLineButBeforeEOLMarker_rewindLine_expectRewindToStart() {
        Bytes      source = Bytes.wrap("123\r\n456\r\n789\rabc def\naaa");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        parser.setPosition( 8 );

        assertEquals( 3, parser.rewindLine() );
        assertEquals( 5, parser.getPosition() );
    }

    @Test
    public void givenMultiLineText_startAtEndOfSecondLine_rewindLine_expectRewindToStart() {
        Bytes      source = Bytes.wrap("123\r\n456\r\n789\rabc def\naaa");
        PullParser parser = new PullParser( "/tmp/file/x", source );

        parser.setPosition( 10 );

        assertEquals( 5, parser.rewindLine() );
        assertEquals( 5, parser.getPosition() );
    }


    private static class NumberParser implements ByteMatcher<Long> {
        public void parse( InputBytes source, long fromInc, long toExc, ParserResult<Long> result ) {
            long num = 0;

            long i=fromInc;
            for ( ; i<toExc; i++ ) {
                byte v = source.readByte(i);

                if ( v < '0' || v > '9' ) {
                    break;
                } else {
                    num *= 10;
                    num += v - '0';
                }
            }

            if ( i == fromInc ) {
                result.resultNoMatch();
            } else {
                result.resultMatched( num, fromInc, i );
            }
        }

        public String toString() {
            return "Number";
        }
    }

    private static class WhitespaceParser implements ByteMatcher<Void> {
        public void parse( InputBytes source, long fromInc, long toExc, ParserResult<Void> result ) {
            long i=fromInc;
            for ( ; i<toExc; i++ ) {
                byte v = source.readByte(i);

                if ( v != ' ' && v != '\t' ) {
                    break;
                }
            }

            result.resultMatchedNoValue( fromInc, i );
        }

        public String toString() {
            return "Whitespace";
        }
    }

}
