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
    public void encodeNoOp() {
        assertTrySuccess("abc", NO_OP_CODEC.tryEncode( "abc" ));
        assertTrySuccess("123", NO_OP_CODEC.tryEncode( "123" ));
    }

    @Test
    public void decodeNoOp() {
        assertTrySuccess("abc", NO_OP_CODEC.tryDecode( "abc" ));
        assertTrySuccess("123", NO_OP_CODEC.tryDecode( "123" ));
    }



    @Test
    public void encodeBooleans() {
        assertTrySuccess("true", BOOLEAN_CODEC.tryEncode( true ));
        assertTrySuccess("false", BOOLEAN_CODEC.tryEncode( false ));
    }

    @Test
    public void decodeBooleans() {
        assertTrySuccess(true, BOOLEAN_CODEC.tryDecode( "true" ));
        assertTrySuccess(true, BOOLEAN_CODEC.tryDecode( "TruE" ));
        assertTrySuccess(true, BOOLEAN_CODEC.tryDecode( "t" ));
        assertTrySuccess(true, BOOLEAN_CODEC.tryDecode( "y" ));
        assertTrySuccess(true, BOOLEAN_CODEC.tryDecode( "yes" ));
        assertTrySuccess(true, BOOLEAN_CODEC.tryDecode( "1" ));

        assertTrySuccess(false, BOOLEAN_CODEC.tryDecode( "false" ));
        assertTrySuccess(false, BOOLEAN_CODEC.tryDecode( "0" ));
        assertTrySuccess(false, BOOLEAN_CODEC.tryDecode( "2" ));
        assertTrySuccess(false, BOOLEAN_CODEC.tryDecode( "f" ));
        assertTrySuccess(false, BOOLEAN_CODEC.tryDecode( "no" ));
        assertTrySuccess(false, BOOLEAN_CODEC.tryDecode( "foo" ));
    }



    @Test
    public void encodeIntegers() {
        assertTrySuccess("0", INTEGER_CODEC.tryEncode( 0 ));
        assertTrySuccess("1", INTEGER_CODEC.tryEncode( 1 ));
        assertTrySuccess("-1", INTEGER_CODEC.tryEncode( -1 ));
        assertTrySuccess("-12345", INTEGER_CODEC.tryEncode( -12345 ));
        assertTrySuccess(Integer.toString(Integer.MAX_VALUE), INTEGER_CODEC.tryEncode( Integer.MAX_VALUE ));
        assertTrySuccess(Integer.toString(Integer.MIN_VALUE), INTEGER_CODEC.tryEncode( Integer.MIN_VALUE ));
    }

    @Test
    public void decodeIntegers() {
        assertTrySuccess(0, INTEGER_CODEC.tryDecode( "0" ));
        assertTrySuccess(12345, INTEGER_CODEC.tryDecode( "12345" ));
        assertTrySuccess(-12345, INTEGER_CODEC.tryDecode( "-12345" ));
        assertTrySuccess(Integer.MAX_VALUE, INTEGER_CODEC.tryDecode( Integer.toString( Integer.MAX_VALUE ) ));
        assertTrySuccess(Integer.MIN_VALUE, INTEGER_CODEC.tryDecode( Integer.toString( Integer.MIN_VALUE ) ));


        assertTryFailure( INTEGER_CODEC.tryDecode("-1234a5"), "'-1234a5' is not a valid number" );
    }



    @Test
    public void encodeLongs() {
        assertTrySuccess("0", LONG_CODEC.tryEncode( 0L ));
        assertTrySuccess("1", LONG_CODEC.tryEncode( 1L ));
        assertTrySuccess("-1", LONG_CODEC.tryEncode( -1L ));
        assertTrySuccess("-12345", LONG_CODEC.tryEncode( -12345L ));
        assertTrySuccess(Long.toString(Long.MAX_VALUE), LONG_CODEC.tryEncode( Long.MAX_VALUE ));
        assertTrySuccess(Long.toString(Long.MIN_VALUE), LONG_CODEC.tryEncode( Long.MIN_VALUE ));
    }

    @Test
    public void decodeLongs() {
        assertTrySuccess(0L, LONG_CODEC.tryDecode( "0" ));
        assertTrySuccess(12345L, LONG_CODEC.tryDecode( "12345" ));
        assertTrySuccess(-12345L, LONG_CODEC.tryDecode( "-12345" ));
        assertTrySuccess(Long.MAX_VALUE, LONG_CODEC.tryDecode( Long.toString( Long.MAX_VALUE ) ));
        assertTrySuccess(Long.MIN_VALUE, LONG_CODEC.tryDecode( Long.toString( Long.MIN_VALUE ) ));


        assertTryFailure( INTEGER_CODEC.tryDecode( "-1234a5" ), "'-1234a5' is not a valid number" );
    }



    @Test
    public void encodeFloats() {
        assertTrySuccess("0.0", FLOAT_CODEC.tryEncode( 0.0f ));
        assertTrySuccess("1.0", FLOAT_CODEC.tryEncode( 1.0f ));
        assertTrySuccess("1.5", FLOAT_CODEC.tryEncode( 1.5f ));
        assertTrySuccess("-1.5", FLOAT_CODEC.tryEncode( -1.5f ));
        assertTrySuccess(Float.toString(Float.MAX_VALUE), FLOAT_CODEC.tryEncode( Float.MAX_VALUE ));
        assertTrySuccess(Float.toString(Float.MIN_VALUE), FLOAT_CODEC.tryEncode( Float.MIN_VALUE ));
    }

    @Test
    public void decodeFloats() {
        assertTrySuccess(-0.0f, FLOAT_CODEC.tryDecode( "-0" ));
        assertTrySuccess(0.0f, FLOAT_CODEC.tryDecode( "0" ));
        assertTrySuccess(0.0f, FLOAT_CODEC.tryDecode( "0.0" ));
        assertTrySuccess(12.34f, FLOAT_CODEC.tryDecode( "12.34" ));
        assertTrySuccess(-12.34f, FLOAT_CODEC.tryDecode( "-12.34" ));
        assertTrySuccess(Float.MAX_VALUE, FLOAT_CODEC.tryDecode( Float.toString( Float.MAX_VALUE ) ));
        assertTrySuccess(Float.MIN_VALUE, FLOAT_CODEC.tryDecode( Float.toString( Float.MIN_VALUE ) ));

        assertTryFailure( FLOAT_CODEC.tryDecode( "0.12A" ), "'0.12A' is not a valid number" );
    }



    @Test
    public void encodeDoubles() {
        assertTrySuccess("0.0", DOUBLE_CODEC.tryEncode( 0.0 ));
        assertTrySuccess("1.0", DOUBLE_CODEC.tryEncode( 1.0 ));
        assertTrySuccess("1.5", DOUBLE_CODEC.tryEncode( 1.5 ));
        assertTrySuccess("-1.5", DOUBLE_CODEC.tryEncode( -1.5 ));
        assertTrySuccess(Double.toString(Double.MAX_VALUE), DOUBLE_CODEC.tryEncode( Double.MAX_VALUE ));
        assertTrySuccess(Double.toString(Double.MIN_VALUE), DOUBLE_CODEC.tryEncode( Double.MIN_VALUE ));
    }

    @Test
    public void decodeDoubles() {
        assertTrySuccess(-0.0, DOUBLE_CODEC.tryDecode( "-0" ));
        assertTrySuccess(0.0, DOUBLE_CODEC.tryDecode( "0" ));
        assertTrySuccess(0.0, DOUBLE_CODEC.tryDecode( "0.0" ));
        assertTrySuccess(12.34, DOUBLE_CODEC.tryDecode( "12.34" ));
        assertTrySuccess(-12.34, DOUBLE_CODEC.tryDecode( "-12.34" ));
        assertTrySuccess(Double.MAX_VALUE, DOUBLE_CODEC.tryDecode( Double.toString( Double.MAX_VALUE ) ));
        assertTrySuccess(Double.MIN_VALUE, DOUBLE_CODEC.tryDecode( Double.toString( Double.MIN_VALUE ) ));

        assertTryFailure( DOUBLE_CODEC.tryDecode( "0.12A" ), "'0.12A' is not a valid number" );
    }


    private void assertTrySuccess(Object expectedResult, Try actualTry) {
        assertEquals( expectedResult, actualTry.getResultNoBlock() );
    }

    private void assertTryFailure( Try actualTry, String expectedFailureMessage ) {
        assertTrue( actualTry.hasFailure() );
        assertEquals( expectedFailureMessage, actualTry.getFailureNoBlock().getMessage() );
    }

}
