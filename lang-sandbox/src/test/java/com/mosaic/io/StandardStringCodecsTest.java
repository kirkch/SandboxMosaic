package com.mosaic.io;

import com.mosaic.lang.functional.Try;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.mosaic.io.StandardStringCodecs.*;

/**
 *
 */
public class StandardStringCodecsTest {

    @Test
    public void encodeBooleans() {
        assertTrySuccess("true", BOOLEAN_CODEC.encode(true));
        assertTrySuccess("false", BOOLEAN_CODEC.encode(false));
    }

    @Test
    public void decodeBooleans() {
        assertTrySuccess(true, BOOLEAN_CODEC.decode("true"));
        assertTrySuccess(true, BOOLEAN_CODEC.decode("TruE"));
        assertTrySuccess(true, BOOLEAN_CODEC.decode("t"));
        assertTrySuccess(true, BOOLEAN_CODEC.decode("y"));
        assertTrySuccess(true, BOOLEAN_CODEC.decode("yes"));
        assertTrySuccess(true, BOOLEAN_CODEC.decode("1"));

        assertTrySuccess(false, BOOLEAN_CODEC.decode("false"));
        assertTrySuccess(false, BOOLEAN_CODEC.decode("0"));
        assertTrySuccess(false, BOOLEAN_CODEC.decode("2"));
        assertTrySuccess(false, BOOLEAN_CODEC.decode("f"));
        assertTrySuccess(false, BOOLEAN_CODEC.decode("no"));
        assertTrySuccess(false, BOOLEAN_CODEC.decode("foo"));
    }



    @Test
    public void encodeIntegers() {
        assertTrySuccess("0", INTEGER_CODEC.encode(0));
        assertTrySuccess("1", INTEGER_CODEC.encode(1));
        assertTrySuccess("-1", INTEGER_CODEC.encode(-1));
        assertTrySuccess("-12345", INTEGER_CODEC.encode(-12345));
        assertTrySuccess(Integer.toString(Integer.MAX_VALUE), INTEGER_CODEC.encode(Integer.MAX_VALUE));
        assertTrySuccess(Integer.toString(Integer.MIN_VALUE), INTEGER_CODEC.encode(Integer.MIN_VALUE));
    }

    @Test
    public void decodeIntegers() {
        assertTrySuccess(0, INTEGER_CODEC.decode("0"));
        assertTrySuccess(12345, INTEGER_CODEC.decode("12345"));
        assertTrySuccess(-12345, INTEGER_CODEC.decode("-12345"));
        assertTrySuccess(Integer.MAX_VALUE, INTEGER_CODEC.decode(Integer.toString(Integer.MAX_VALUE)));
        assertTrySuccess(Integer.MIN_VALUE, INTEGER_CODEC.decode(Integer.toString(Integer.MIN_VALUE)));


        assertTryFailure( INTEGER_CODEC.decode("-1234a5"), "'-1234a5' is not a valid number" );
    }



    @Test
    public void encodeLongs() {
        assertTrySuccess("0", LONG_CODEC.encode(0L));
        assertTrySuccess("1", LONG_CODEC.encode(1L));
        assertTrySuccess("-1", LONG_CODEC.encode(-1L));
        assertTrySuccess("-12345", LONG_CODEC.encode(-12345L));
        assertTrySuccess(Long.toString(Long.MAX_VALUE), LONG_CODEC.encode(Long.MAX_VALUE));
        assertTrySuccess(Long.toString(Long.MIN_VALUE), LONG_CODEC.encode(Long.MIN_VALUE));
    }

    @Test
    public void decodeLongs() {
        assertTrySuccess(0L, LONG_CODEC.decode("0"));
        assertTrySuccess(12345L, LONG_CODEC.decode("12345"));
        assertTrySuccess(-12345L, LONG_CODEC.decode("-12345"));
        assertTrySuccess(Long.MAX_VALUE, LONG_CODEC.decode(Long.toString(Long.MAX_VALUE)));
        assertTrySuccess(Long.MIN_VALUE, LONG_CODEC.decode(Long.toString(Long.MIN_VALUE)));


        assertTryFailure( INTEGER_CODEC.decode("-1234a5"), "'-1234a5' is not a valid number" );
    }



    @Test
    public void encodeFloats() {
        assertTrySuccess("0.0", FLOAT_CODEC.encode(0.0f));
        assertTrySuccess("1.0", FLOAT_CODEC.encode(1.0f));
        assertTrySuccess("1.5", FLOAT_CODEC.encode(1.5f));
        assertTrySuccess("-1.5", FLOAT_CODEC.encode(-1.5f));
        assertTrySuccess(Float.toString(Float.MAX_VALUE), FLOAT_CODEC.encode(Float.MAX_VALUE));
        assertTrySuccess(Float.toString(Float.MIN_VALUE), FLOAT_CODEC.encode(Float.MIN_VALUE));
    }

    @Test
    public void decodeFloats() {
        assertTrySuccess(-0.0f, FLOAT_CODEC.decode("-0"));
        assertTrySuccess(0.0f, FLOAT_CODEC.decode("0"));
        assertTrySuccess(0.0f, FLOAT_CODEC.decode("0.0"));
        assertTrySuccess(12.34f, FLOAT_CODEC.decode("12.34"));
        assertTrySuccess(-12.34f, FLOAT_CODEC.decode("-12.34"));
        assertTrySuccess(Float.MAX_VALUE, FLOAT_CODEC.decode(Float.toString(Float.MAX_VALUE)));
        assertTrySuccess(Float.MIN_VALUE, FLOAT_CODEC.decode(Float.toString(Float.MIN_VALUE)));

        assertTryFailure( FLOAT_CODEC.decode("0.12A"), "'0.12A' is not a valid number" );
    }



    @Test
    public void encodeDoubles() {
        assertTrySuccess("0.0", DOUBLE_CODEC.encode(0.0));
        assertTrySuccess("1.0", DOUBLE_CODEC.encode(1.0));
        assertTrySuccess("1.5", DOUBLE_CODEC.encode(1.5));
        assertTrySuccess("-1.5", DOUBLE_CODEC.encode(-1.5));
        assertTrySuccess(Double.toString(Double.MAX_VALUE), DOUBLE_CODEC.encode(Double.MAX_VALUE));
        assertTrySuccess(Double.toString(Double.MIN_VALUE), DOUBLE_CODEC.encode(Double.MIN_VALUE));
    }

    @Test
    public void decodeDoubles() {
        assertTrySuccess(-0.0, DOUBLE_CODEC.decode("-0"));
        assertTrySuccess(0.0, DOUBLE_CODEC.decode("0"));
        assertTrySuccess(0.0, DOUBLE_CODEC.decode("0.0"));
        assertTrySuccess(12.34, DOUBLE_CODEC.decode("12.34"));
        assertTrySuccess(-12.34, DOUBLE_CODEC.decode("-12.34"));
        assertTrySuccess(Double.MAX_VALUE, DOUBLE_CODEC.decode(Double.toString(Double.MAX_VALUE)));
        assertTrySuccess(Double.MIN_VALUE, DOUBLE_CODEC.decode(Double.toString(Double.MIN_VALUE)));

        assertTryFailure( DOUBLE_CODEC.decode("0.12A"), "'0.12A' is not a valid number" );
    }


    private void assertTrySuccess(Object expectedResult, Try actualTry) {
        assertEquals( expectedResult, actualTry.getResultNoBlock() );
    }

    private void assertTryFailure( Try actualTry, String expectedFailureMessage ) {
        assertTrue( actualTry.hasFailure() );
        assertEquals( expectedFailureMessage, actualTry.getFailureNoBlock().getMessage() );
    }

}
