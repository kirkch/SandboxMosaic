package com.mosaic.bytes;

import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8;
import com.softwaremosaic.junit.JUnitMosaic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


@SuppressWarnings("unchecked")
public abstract class BaseBytesTest {

    private long         activeAllocCountBeforeTestStarted;
    private List<Bytes2> allocatedBytes = new ArrayList();


    protected abstract Bytes2 _createBytes( long numBytes ) throws IOException;


    protected Bytes2 createBytes( long numBytes ) {
        try {
            Bytes2 b = _createBytes( numBytes );

            allocatedBytes.add( b );

            return b;
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Before
    public void setup() {
        this.activeAllocCountBeforeTestStarted = Backdoor.getActiveAllocCounter();
    }

    @After
    public void tearDown() {
        for ( Bytes2 b : allocatedBytes ) {
            b.release();
        }

        allocatedBytes.clear();

        // using a spin lock incase the tests are run in parallel.  If there
        // is a test that does not free their memory then one of the tests
        // will fail.. rerunning the tests in serial will then identify which
        // of the classes under test had been naughty.
        JUnitMosaic.spinUntilTrue( new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return Backdoor.getActiveAllocCounter() <= activeAllocCountBeforeTestStarted;
            }
        } );
    }


    @Test
    public void tryAllocatingNegativeNumberOfBytes2() {
        try {
            createBytes( -2 );

            fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'numBytes' (-2) must be > 0", e.getMessage() );
        } catch ( NegativeArraySizeException e ) {

        }
    }

    @Test
    public void allocateBytes2_callSize_expectLengthToMatchRequestedNumberOfBytes2() {
        Bytes2 b = createBytes( 5 );

        assertEquals( 5, b.sizeBytes() );
    }

    @Test
    public void writeOneOfEachTypeAndReadBack() {
        long size = 100;

        Bytes2 b = createBytes( size );

        b.writeByte( 0, size, (byte) 1 );
        b.writeCharacter( 1, size, (char) 2 );
        b.writeShort( 3, size, (short) 3 );
        b.writeInt( 5, size, 4 );
        b.writeLong( 9, size, 5 );
        b.writeFloat( 17, size, 6.1f );
        b.writeDouble( 21, size, 7.1 );


        assertEquals( 1, b.readByte(0, size) );
        assertEquals( 2, b.readCharacter(1, size) );
        assertEquals( 3, b.readShort(3, size) );
        assertEquals( 4, b.readInt(5, size) );
        assertEquals( 5, b.readLong(9, size) );
        assertEquals( 6.1, b.readFloat(17, size), 0.0001 );
        assertEquals( 7.1, b.readDouble(21, size), 0.0001 );
    }

    @Test
    public void writeByte_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes2 b = initBytes();

        b.writeByte( 5, 100, Byte.MAX_VALUE );

        assertAllBytes2AreZero( b, 0, 4 );
        assertEquals( Byte.MAX_VALUE, b.readByte(5, 100) );
        assertAllBytes2AreZero( b, 6, b.sizeBytes() );
    }

    @Test
    public void writeCharacter_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes2 b = initBytes();

        b.writeCharacter( 5, 100, Character.MAX_VALUE );

        assertAllBytes2AreZero( b, 0, 4 );
        assertEquals( Character.MAX_VALUE, b.readCharacter(5, 100) );
        assertAllBytes2AreZero( b, 7, b.sizeBytes() );
    }

    @Test
    public void writeShort_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes2 b = initBytes();

        b.writeShort( 5, 100, Short.MAX_VALUE );

        assertAllBytes2AreZero( b, 0, 4 );
        assertEquals( Short.MAX_VALUE, b.readShort(5, 100) );
        assertAllBytes2AreZero( b, 7, b.sizeBytes() );
    }

    @Test
    public void writeInt_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes2 b = initBytes();

        b.writeInt( 5, 100, Integer.MAX_VALUE );

        assertAllBytes2AreZero( b, 0, 4 );
        assertEquals( Integer.MAX_VALUE, b.readInt(5, 100) );
        assertAllBytes2AreZero( b, 9, b.sizeBytes() );
    }

    @Test
    public void writeLong_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes2 b = initBytes();

        b.writeLong( 5, 100, Long.MAX_VALUE );

        assertAllBytes2AreZero( b, 0, 4 );
        assertEquals( Long.MAX_VALUE, b.readLong(5, 100) );
        assertAllBytes2AreZero( b, 13, b.sizeBytes() );
    }

    @Test
    public void writeFloat_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes2 b = initBytes();

        b.writeFloat( 5, 100, Float.MAX_VALUE );

        assertAllBytes2AreZero( b, 0, 4 );
        assertEquals( Float.MAX_VALUE, b.readFloat(5, 100), 0.001 );
        assertAllBytes2AreZero( b, 9, b.sizeBytes() );
    }

    @Test
    public void writeDouble_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes2 b = initBytes();

        b.writeDouble( 5, 100, Double.MAX_VALUE );

        assertAllBytes2AreZero( b, 0, 4 );
        assertEquals( Double.MAX_VALUE, b.readDouble(5, 100), 0.001 );
        assertAllBytes2AreZero( b, 13, b.sizeBytes() );
    }

// WRITE UTF8

    @Test
    public void writeUTF8CharSingleByte_thenReadItBack() {
        Bytes2            b   = initBytes();
        DecodedCharacter buf = new DecodedCharacter();

        assertEquals( 1, b.writeUTF8Character(3, 100, 'e') );

        assertAllBytes2AreZero( b, 0, 3 );

        b.readUTF8Character( 3, 100, buf );
        assertEquals( 'e', buf.c );
        assertEquals( 1, buf.numBytesConsumed );

        assertAllBytes2AreZero( b, 4, b.sizeBytes() );
    }

    @Test
    public void writeStringAsUTF8_thenReadItBack() {
        Bytes2         b   = initBytes();
        StringBuilder buf = new StringBuilder();


        assertEquals( 7, b.writeUTF8String( 2, 100, "hello" ) );

        assertAllBytes2AreZero( b, 0, 2 );


        b.readUTF8String( 2, 100, buf );

        assertAllBytes2AreZero( b, 0, 2 );
        assertEquals( "hello", buf.toString() );
        assertAllBytes2AreZero( b, 9, b.sizeBytes() );
    }

    @Test
    public void writeUTF8String_thenReadItBack() {
        Bytes2         b   = initBytes();
        StringBuilder buf = new StringBuilder();


        assertEquals( 7, b.writeUTF8String( 2, 100, new UTF8("hello") ) );

        assertAllBytes2AreZero( b, 0, 2 );


        b.readUTF8String( 2, 100, buf );

        assertAllBytes2AreZero( b, 0, 2 );
        assertEquals( "hello", buf.toString() );
        assertAllBytes2AreZero( b, 9, b.sizeBytes() );
    }

    @Test
    public void writeUTF8StringVariableByteLengths_thenReadItBack() {
        Bytes2         b   = initBytes();
        StringBuilder buf = new StringBuilder();


        assertEquals( 12, b.writeUTF8String(2, 100, "h£グoグ") );

        assertAllBytes2AreZero( b, 0, 2 );

        b.readUTF8String( 2, 100, buf );

        assertAllBytes2AreZero( b, 0, 2 );
        assertEquals( "h£グoグ", buf.toString() );
        assertAllBytes2AreZero( b, 16, b.sizeBytes() );
    }

    @Test
    public void writeAllBytes2FromAnArrayAndReadyBack() {
        Bytes2 b = initBytes();


        b.writeBytes( 2, 100, new byte[]{'a', 'b', 'c'} );

        assertAllBytes2AreZero( b, 0, 2 );
        assertEquals( 'a', b.readByte(2, 100) );
        assertEquals( 'b', b.readByte(3, 100) );
        assertEquals( 'c', b.readByte(4, 100) );
        assertAllBytes2AreZero( b, 5, b.sizeBytes() );

        byte[] buf = new byte[3];
        b.readBytes( 2, 100, buf );

        assertEquals( 'a', buf[0] );
        assertEquals( 'b', buf[1] );
        assertEquals( 'c', buf[2] );
    }

    @Test
    public void writeBytes2FromOneBytes2ClassToAnother() {
        Bytes2 source = initBytes();
        source.writeBytes( 0, 100, new byte[]{'a', 'b', 'c'} );


        Bytes2 sink = initBytes();
        sink.writeBytes(2, 100, source, 0, 3);


        assertAllBytes2AreZero( sink, 0, 2 );
        assertEquals( 'a', sink.readByte(2, 100) );
        assertEquals( 'b', sink.readByte(3, 100) );
        assertEquals( 'c', sink.readByte(4, 100) );
        assertAllBytes2AreZero( sink, 5, sink.sizeBytes() );
    }

    @Test
    public void writeBytes2PositionalFromOneBytes2ClassToAnother() {
        Bytes2 source = initBytes();
        source.writeBytes( 0, 100, new byte[]{'a', 'b', 'c'} );


        Bytes2 sink = initBytes();
        sink.writeBytes(3, 100,source);


        assertAllBytes2AreZero( sink, 0, 3 );
        assertEquals( 'a', sink.readByte(3, 100) );
        assertEquals( 'b', sink.readByte(4, 100) );
        assertEquals( 'c', sink.readByte(5, 100) );
        assertAllBytes2AreZero( sink, 6, sink.sizeBytes() );
    }


// RESIZE

    @Test
    public void increaseSizeOfBytes2_expectContentsToBeCopiedOver() {
        Bytes2 b = initBytes();

        b.fill( 0, b.sizeBytes(), (byte) 7 );

        b.resize( 200 );

        assertEquals( 200, b.sizeBytes() );

        for ( int i=0; i<100; i++ ) {
            assertEquals( i+"", 7, b.readByte(i, 100) );
        }
    }

    @Test
    public void reduceSizeOfBytes2_expectContentsToBeTruncated() {
        Bytes2 b = initBytes();

        b.fill( 0, b.sizeBytes(), (byte) 7 );

        b.resize( 51 );

        assertEquals( 51, b.sizeBytes() );

        for ( int i=0; i<51; i++ ) {
            assertEquals( i+"", 7, b.readByte(i, 100) );
        }
    }


// NARROW

//    @Test
//    public void narrow_checkLengthHasBeenReduced() {
//        Bytes2 b = initBytes();
//        Bytes2 n = b.narrow( 2, 4 );
//
//        assertEquals( 2, n.sizeBytes() );
//    }
//
//    @Test
//    public void narrow_checkThatWritingToTheOriginalIsVisibleInTheView() {
//        Bytes2 b = initBytes();
//        Bytes2 n = b.narrow( 2, 4 );
//
//
//        b.writeShort( 2, (short) 42 );
//
//        assertEquals( 42, n.readShort( 0 ) );
//    }
//
//    @Test
//    public void narrow_checkThatWritingToTheViewIsVisibleInTheOriginal() {
//        Bytes2 b = initBytes();
//        Bytes2 n = b.narrow( 2, 4 );
//
//
//        n.writeShort( 0, (short) 42 );
//
//        assertEquals( 42, n.readShort( 0 ) );
//        assertEquals( 42, b.readShort( 2 ) );
//    }






    private Bytes2 initBytes() {
        Bytes2 b = createBytes( 100 );

        b.fill(0, b.sizeBytes(), (byte) 0);
        assertAllBytes2AreZero( b, 0, b.sizeBytes() );

        return b;
    }


    private void assertAllBytes2AreZero( Bytes2 b, long from, long toExc ) {
        for ( long i=from; i<toExc; i++ ) {
            assertEquals( 0, b.readByte(i, 100) );
        }
    }
}