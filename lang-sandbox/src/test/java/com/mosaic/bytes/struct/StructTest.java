package com.mosaic.bytes.struct;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes.Bytes;
import com.mosaic.lang.system.SystemX;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.mosaic.lang.system.Backdoor.toByte;
import static com.mosaic.lang.system.Backdoor.toInt;
import static org.junit.Assert.*;


public class StructTest {

    private Bytes  bytes;
    private Struct struct;


    @Before
    public void setup() {
        bytes  = new ArrayBytes( 32 );
        struct = new Struct( 8 );

        struct.setBytes( bytes, 3, 32 );
    }

// BOOLEANS

    @Test
    public void givenBlankBytes_readBoolean_expectDefaultValue() {
        assertEquals( false, struct.readBoolean(0) );

        assertEmptyBytes(bytes);
    }

    @Test
    public void writeThenReadBackABoolean_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeBoolean( 0, true );

        assertEquals( true, struct.readBoolean(0) );
        assertEquals( false, struct.readBoolean(1) );

        assertBytes( bytes, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 );
    }

    @Test
    public void writeABooleanBeforeTheStartOfTheStruct_expectException() {
        try {
            struct.writeBoolean( -1, true );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'offset' (-1) must be >= 0 and < 8", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }

    @Test
    public void writeABooleanAfterTheEndOfTheStruct_expectException() {
        if ( !SystemX.isDebugRun() ) {
            return;  // skip the test as -ea is required; we are testing an assertion
        }

        try {
            struct.writeBoolean( 8, true );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'offset' (8) must be >= 0 and < 8", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }


// BYTES

    @Test
    public void givenBlankBytes_readByte_expectDefaultValue() {
        assertEquals( 0, struct.readByte(0) );

        assertEmptyBytes(bytes);
    }

    @Test
    public void writeThenReadBackMaxByte_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeByte( 0, Byte.MAX_VALUE );

        assertEquals( Byte.MAX_VALUE, struct.readByte(0) );
        assertEquals( 0, struct.readByte( 1 ) );

        assertBytes( bytes, 0, 0, 0, 127, 0, 0, 0, 0, 0, 0 );
    }

    @Test
    public void writeThenReadBackMinByte_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeByte( 0, Byte.MIN_VALUE );

        assertEquals( Byte.MIN_VALUE, struct.readByte(0) );
        assertEquals( 0, struct.readByte( 1 ) );

        assertBytes( bytes, 0, 0, 0, -128, 0, 0, 0, 0, 0, 0 );
    }

    @Test
    public void writeAByteBeforeTheStartOfTheStruct_expectException() {
        try {
            struct.writeByte( -1, Byte.MAX_VALUE );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'offset' (-1) must be >= 0 and < 8", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }

    @Test
    public void writeAByteAfterTheEndOfTheStruct_expectException() {
        if ( !SystemX.isDebugRun() ) {
            return;  // skip the test as -ea is required; we are testing an assertion
        }

        try {
            struct.writeByte( 8, Byte.MAX_VALUE );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'offset' (8) must be >= 0 and < 8", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }


// SHORTS

    @Test
    public void givenBlankShorts_readShort_expectDefaultValue() {
        assertEquals( 0, struct.readShort(0) );

        assertEmptyBytes(bytes);
    }

    @Test
    public void writeThenReadBackMaxShort_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeShort( 0, Short.MAX_VALUE );

        assertEquals( Short.MAX_VALUE, struct.readShort(0) );
        assertEquals( 0, struct.readShort(2) );

        assertBytes( bytes, 0, 0, 0, -1, 127, 0, 0, 0, 0, 0 );
    }

    @Test
    public void writeThenReadBackMinShort_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeShort( 0, Short.MIN_VALUE );

        assertEquals( Short.MIN_VALUE, struct.readShort(0) );

        assertBytes( bytes, 0, 0, 0, 0, -128, 0, 0, 0, 0, 0 );
    }

    @Test
    public void writeAShortBeforeTheStartOfTheStruct_expectException() {
        try {
            struct.writeShort( -1, Short.MAX_VALUE );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'offset' (-1) must be >= 0 and < 8", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }

    @Test
    public void writeAShortAfterTheEndOfTheStruct_expectException() {
        if ( !SystemX.isDebugRun() ) {
            return;  // skip the test as -ea is required; we are testing an assertion
        }

        try {
            struct.writeShort( 7, Short.MAX_VALUE );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'maxExc' (9) must be >= 0 and < 9", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }


// INTS

    @Test
    public void givenBlankInts_readInt_expectDefaultValue() {
        assertEquals( 0, struct.readInt(0) );

        assertEmptyBytes(bytes);
    }

    @Test
    public void writeThenReadBackMaxInt_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeInt( 0, Integer.MAX_VALUE );

        assertEquals( Integer.MAX_VALUE, struct.readInt(0) );

        assertBytes( bytes, 0, 0, 0, -1, -1, -1, 127, 0, 0, 0 );
    }

    @Test
    public void writeThenReadBackMinInt_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeInt( 0, Integer.MIN_VALUE );

        assertEquals( Integer.MIN_VALUE, struct.readInt(0) );

        assertBytes( bytes, 0, 0, 0, 0, 0, 0, -128, 0, 0, 0 );
    }

    @Test
    public void writeAIntBeforeTheStartOfTheStruct_expectException() {
        try {
            struct.writeInt( -1, Integer.MAX_VALUE );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'offset' (-1) must be >= 0 and < 8", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }

    @Test
    public void writeAIntAfterTheEndOfTheStruct_expectException() {
        if ( !SystemX.isDebugRun() ) {
            return;  // skip the test as -ea is required; we are testing an assertion
        }

        try {
            struct.writeInt( 5, Integer.MAX_VALUE );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'maxExc' (9) must be >= 0 and < 9", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }


// LONGS

    @Test
    public void givenBlankLongs_readLong_expectDefaultValue() {
        assertEquals( 0, struct.readLong(0) );

        assertEmptyBytes(bytes);
    }

    @Test
    public void writeThenReadBackMaxLong_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeLong( 0, Long.MAX_VALUE );

        assertEquals( Long.MAX_VALUE, struct.readLong(0) );

        assertBytes( bytes, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 127 );
    }

    @Test
    public void writeThenReadBackMinLong_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeLong( 0, Long.MIN_VALUE );

        assertEquals( Long.MIN_VALUE, struct.readLong(0) );

        assertBytes( bytes, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128 );
    }

    @Test
    public void writeALongBeforeTheStartOfTheStruct_expectException() {
        try {
            struct.writeLong( -1, Long.MAX_VALUE );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'offset' (-1) must be >= 0 and < 8", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }

    @Test
    public void writeALongAfterTheEndOfTheStruct_expectException() {
        if ( !SystemX.isDebugRun() ) {
            return;  // skip the test as -ea is required; we are testing an assertion
        }

        try {
            struct.writeLong( 3, Long.MAX_VALUE );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'maxExc' (11) must be >= 0 and < 9", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }


// FLOATS

    @Test
    public void givenBlankFloats_readFloat_expectDefaultValue() {
        assertEquals( 0, struct.readFloat(0), 0.0001 );

        assertEmptyBytes(bytes);
    }

    @Test
    public void writeThenReadBackMaxFloat_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeFloat( 0, Float.MAX_VALUE );

        assertEquals( Float.MAX_VALUE, struct.readFloat(0), 0.0001 );

        assertBytes( bytes, 0, 0, 0, -1, -1, 127, 127, 0, 0, 0, 0 );
    }

    @Test
    public void writeThenReadBackMinFloat_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeFloat( 0, Float.MIN_VALUE );

        assertEquals( Float.MIN_VALUE, struct.readFloat(0), 0.0001 );

        assertBytes( bytes, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 );
    }

    @Test
    public void writeAFloatBeforeTheStartOfTheStruct_expectException() {
        try {
            struct.writeFloat( -1, Float.MAX_VALUE );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'offset' (-1) must be >= 0 and < 8", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }

    @Test
    public void writeAFloatAfterTheEndOfTheStruct_expectException() {
        if ( !SystemX.isDebugRun() ) {
            return;  // skip the test as -ea is required; we are testing an assertion
        }

        try {
            struct.writeFloat( 5, Float.MAX_VALUE );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'maxExc' (9) must be >= 0 and < 9", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }


// DOUBLES

    @Test
    public void givenBlankDoubles_readDouble_expectDefaultValue() {
        assertEquals( 0, struct.readDouble(0), 0.0001 );

        assertEmptyBytes(bytes);
    }

    @Test
    public void writeThenReadBackMaxDouble_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeDouble( 0, Double.MAX_VALUE );

        assertEquals( Double.MAX_VALUE, struct.readDouble(0), 0.0001 );

        assertBytes( bytes, 0, 0, 0, -1, -1, -1, -1, -1, -1, -17, 127 );
    }

    @Test
    public void writeThenReadBackMinDouble_expectThatTheWriteWillBeLocalisedToTheSpecifiedSpot() {
        struct.writeDouble( 0, Double.MIN_VALUE );

        assertEquals( Double.MIN_VALUE, struct.readDouble(0), 0.0001 );

        assertBytes( bytes, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 );
    }

    @Test
    public void writeADoubleBeforeTheStartOfTheStruct_expectException() {
        try {
            struct.writeDouble( -1, Double.MAX_VALUE );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'offset' (-1) must be >= 0 and < 8", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }

    @Test
    public void writeADoubleAfterTheEndOfTheStruct_expectException() {
        if ( !SystemX.isDebugRun() ) {
            return;  // skip the test as -ea is required; we are testing an assertion
        }

        try {
            struct.writeDouble( 3, Double.MAX_VALUE );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "'maxExc' (11) must be >= 0 and < 9", ex.getMessage() );

            assertEmptyBytes(bytes);
        }
    }




    private void assertEmptyBytes( Bytes b ) {
        assertBytes( b, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 );
    }


    private void assertBytes( Bytes bytes, int...expectedByteValues ) {
        int    n   = expectedByteValues.length;
        byte[] out = new byte[n];

        for ( int i=0; i<n; i++ ) {
            out[i] = toByte(expectedByteValues[i]);
        }

        assertBytes( bytes, out );
    }

    private void assertBytes( Bytes bytes, byte...expectedByteValues ) {
        for ( int i=0; i<expectedByteValues.length; i++ ) {
            byte expected = toByte( expectedByteValues[i] );
            byte actual   = bytes.readByte( i, expectedByteValues.length );

            if ( actual != expected ) {
                throw new ComparisonFailure( "Mismatch at index "+i, toString(expectedByteValues), toString(bytes) );
            }
        }
    }

    private String toString( Bytes b ) {
        return toString(b.toArray());
    }

    private String toString( byte[] b ) {
        StringBuilder buf = new StringBuilder(toInt(b.length*2));

        boolean incComma = false;
        for ( int i=0; i<b.length; i++ ) {
            if ( incComma ) {
                buf.append(",");
            } else {
                incComma = true;
            }

            buf.append( b[i] );
        }

        return buf.toString();
    }

}