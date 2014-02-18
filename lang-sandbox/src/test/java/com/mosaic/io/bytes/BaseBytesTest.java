package com.mosaic.io.bytes;

import com.mosaic.lang.Backdoor;
import com.softwaremosaic.junit.JUnitMosaic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


@SuppressWarnings("unchecked")
public abstract class BaseBytesTest {

    private long        activeAllocCountBeforeTestStarted;
    private List<Bytes> allocatedBytes = new ArrayList();


    protected abstract Bytes doCreateBytes( long numBytes );


    protected Bytes createBytes( long numBytes ) {
        Bytes b = doCreateBytes( numBytes );

        allocatedBytes.add(b);

        return b;
    }

    @Before
    public void writeup() {
        this.activeAllocCountBeforeTestStarted = Backdoor.getActiveAllocCounter();
    }

    @After
    public void tearDown() {
        for ( Bytes b : allocatedBytes ) {
            b.release();
        }

        allocatedBytes.clear();

        // using a spin lock incase the tests are run in parallel.  If there
        // is a test that does not release their memory then one of the tests
        // will fail.. rerunning the tests in serial will then identify which
        // of the classes under test had been naughty.
        JUnitMosaic.spinUntilTrue( new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return Backdoor.getActiveAllocCounter() <= activeAllocCountBeforeTestStarted;
            }
        } );
    }


    @Test
    public void tryAllocatingNegativeNumberOfBytes() {
        try {
            createBytes( -2 );

            fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'numBytes' (-2) must be > 0", e.getMessage() );
        } catch ( NegativeArraySizeException e ) {

        }
    }

    @Test
    public void allocateBytes_callSize_expectLengthToMatchRequestedNumberOfBytes() {
        Bytes b = createBytes( 5 );

        assertEquals( 5, b.size() );
    }

    @Test
    public void writeOneOfEachTypeAndReadBack() {
        Bytes b = createBytes( 100 );

        b.writeByte( 0, (byte) 1 );
        b.writeCharacter( 1, (char) 2 );
        b.writeShort( 3, (short) 3 );
        b.writeInteger( 5, 4 );
        b.writeLong( 9, 5 );
        b.writeFloat( 17, 6.1f );
        b.writeDouble( 21, 7.1 );


        assertEquals( 1, b.readByte( 0 ) );
        assertEquals( 2, b.readCharacter( 1 ) );
        assertEquals( 3, b.readShort( 3 ) );
        assertEquals( 4, b.readInteger( 5 ) );
        assertEquals( 5, b.readLong( 9 ) );
        assertEquals( 6.1, b.readFloat( 17 ), 0.0001 );
        assertEquals( 7.1, b.readDouble( 21 ), 0.0001 );
    }

    @Test
    public void writeByte_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes b = initBytes();

        b.writeByte( 5, Byte.MAX_VALUE );

        assertAllBytesAreZero( b, 0, 4 );
        assertEquals( Byte.MAX_VALUE, b.readByte( 5 ) );
        assertAllBytesAreZero( b, 6, b.size() );
    }

    @Test
    public void writeCharacter_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes b = initBytes();

        b.writeCharacter( 5, Character.MAX_VALUE );

        assertAllBytesAreZero( b, 0, 4 );
        assertEquals( Character.MAX_VALUE, b.readCharacter( 5 ) );
        assertAllBytesAreZero( b, 7, b.size() );
    }

    @Test
    public void writeShort_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes b = initBytes();

        b.writeShort( 5, Short.MAX_VALUE );

        assertAllBytesAreZero( b, 0, 4 );
        assertEquals( Short.MAX_VALUE, b.readShort( 5 ) );
        assertAllBytesAreZero( b, 7, b.size() );
    }

    @Test
    public void writeInt_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes b = initBytes();

        b.writeInteger( 5, Integer.MAX_VALUE );

        assertAllBytesAreZero( b, 0, 4 );
        assertEquals( Integer.MAX_VALUE, b.readInteger( 5 ) );
        assertAllBytesAreZero( b, 9, b.size() );
    }

    @Test
    public void writeLong_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes b = initBytes();

        b.writeLong( 5, Long.MAX_VALUE );

        assertAllBytesAreZero( b, 0, 4 );
        assertEquals( Long.MAX_VALUE, b.readLong( 5 ) );
        assertAllBytesAreZero( b, 13, b.size() );
    }

    @Test
    public void writeFloat_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes b = initBytes();

        b.writeFloat( 5, Float.MAX_VALUE );

        assertAllBytesAreZero( b, 0, 4 );
        assertEquals( Float.MAX_VALUE, b.readFloat( 5 ), 0.001 );
        assertAllBytesAreZero( b, 9, b.size() );
    }

    @Test
    public void writeDouble_ensureThatItCanBeReadBackAndThatNoOtherByteIsChangedUnintentionally() {
        Bytes b = initBytes();

        b.writeDouble( 5, Double.MAX_VALUE );

        assertAllBytesAreZero( b, 0, 4 );
        assertEquals( Double.MAX_VALUE, b.readDouble( 5 ), 0.001 );
        assertAllBytesAreZero( b, 13, b.size() );
    }






    private Bytes initBytes() {
        Bytes b = createBytes( 100 );

        b.fill(0, b.size(), (byte) 0);
        assertAllBytesAreZero( b, 0, b.size() );

        return b;
    }


    private void assertAllBytesAreZero( Bytes b, long from, long toExc ) {
        for ( long i=from; i<toExc; i++ ) {
            assertEquals( 0, b.readByte( i ) );
        }
    }
}