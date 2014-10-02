package com.mosaic.lang.system;

import com.mosaic.lang.QA;
import com.mosaic.lang.reflect.ReflectionException;
import com.mosaic.lang.time.Duration;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

import static com.mosaic.lang.system.SystemX.*;


/**
 * A utility wrapper around sun.misc.Unsafe.  Until such time that Unsafe
 * becomes standardised; Java 9?
 */
public class Backdoor {

    private static final Unsafe     unsafe        = fetchUnsafe();
    private static final AtomicLong mallocCounter = new AtomicLong(0);


    public static long alloc( long numBytes ) {
        QA.argIsGTZero( numBytes, "numBytes" );

        long ptr = unsafe.allocateMemory( numBytes );

        mallocCounter.incrementAndGet();

        return ptr;
    }

    public static void free( long address ) {
        unsafe.freeMemory(address);

        mallocCounter.decrementAndGet();
    }

    public static void storeFence() {
        unsafe.storeFence();
    }

    public static void loadFence() {
        unsafe.loadFence();
    }

    public static void fullFence() {
        unsafe.fullFence();
    }

    /**
     * Returns a count of how many more calls to allocOffHeap() than free().  The counter
     * is incremented when allocOffHeap() is called, and decremented when free() is
     * called.
     */
    public static long getActiveAllocCounter() {
        return mallocCounter.get();
    }

    public static <T> T throwException( Throwable ex ) {
        unsafe.throwException( ex );

        // returns null so that callers can write 'return Backdoor.throwException(ex)'
        // all to avoid compiler errors, bah.
        return null;
    }

    public static void sleep( Duration sleepFor ) {
        long millis = sleepFor.getMillis();

        QA.argIsGTEZero( millis, "millis" );

        try {
            Thread.sleep( millis );
        } catch ( InterruptedException ex ) {
            throwException( ex );
        }
    }



// PRIMITIVE VALUE GETTER/SETTERS


    public boolean getBoolean( long address ) {
        return unsafe.getByte( address ) > 0;
    }

    public static byte getByte( long address ) {
        return unsafe.getByte( address );
    }

    public static short getUnsignedByte( long address ) {
        return (short) (unsafe.getByte( address ) & UNSIGNED_BYTE_MASK);
    }

    public static int getUnsignedShort( long address ) {
        return (unsafe.getShort(address) & UNSIGNED_SHORT_MASK);
    }

    public static long getUnsignedInt( long address ) {
        return (unsafe.getInt(address) & UNSIGNED_INT_MASK);
    }

    public static char getCharacter( long address ) {
        return unsafe.getChar( address );
    }

    public static short getShort( long address ) {
        return unsafe.getShort( address );
    }

    public static int getInteger( long address ) {
        return unsafe.getInt( address );
    }

    public static long getLong( long address ) {
        return unsafe.getLong( address );
    }

    public static float getFloat( long address ) {
        return unsafe.getFloat( address );
    }

    public static double getDouble( long address ) {
        return unsafe.getDouble( address );
    }

    public static void setBoolean( long address, boolean v ) {
        debugAddress( address, SIZEOF_BOOLEAN );

        unsafe.putByte( address, (byte) (v ? 1 : 0) );
    }

    public static void setByte( long address, byte v ) {
        debugAddress( address, SIZEOF_BYTE );

        unsafe.putByte( address, v );
    }

    public static void setCharacter( long address, char v ) {
        debugAddress( address, SIZEOF_CHAR );

        unsafe.putChar( address, v );
    }

    public static void setShort( long address, short v ) {
        debugAddress( address, SIZEOF_SHORT );

        unsafe.putShort( address, v );
    }

    public static void setInteger( long address, int v ) {
        debugAddress( address, SIZEOF_INT );

        unsafe.putInt( address, v );
    }

    public static void setLong( long address, long v ) {
        debugAddress( address, SIZEOF_LONG );

        unsafe.putLong( address, v );
    }

    public static void setFloat( long address, float v ) {
        debugAddress( address, SIZEOF_FLOAT );

        unsafe.putFloat( address, v );
    }

    public static void setDouble( long address, double v ) {
        debugAddress( address, SIZEOF_DOUBLE );

        unsafe.putDouble( address, v );
    }

    public static void copyBytes( long fromAddress, long toAddress, long numBytes ) {
        debugAddress( toAddress, numBytes );

        unsafe.copyMemory( fromAddress, toAddress, numBytes );
    }

    public static void copyBytes( byte[] fromArray, int fromInc, long toAddress, long numBytes ) {
        if ( SystemX.isDebugRun() ) {
            QA.argIsBetweenInc( 0, fromInc, fromArray.length, "fromInc" );
            QA.argIsBetweenInc( 0, fromInc + numBytes, fromArray.length, "fromInc+numBytes" );

            debugAddress( toAddress, numBytes );
        }

        unsafe.copyMemory( fromArray, BYTE_ARRAY_BASE_OFFSET+fromInc, null, toAddress, numBytes );
    }

    public static void copyBytes( long fromAddress, byte[] toArray, int arrayIndex, int numBytes ) {
        if ( SystemX.isDebugRun() ) {
            QA.argIsBetween( 0, arrayIndex, toArray.length, "arrayIndex" );
            QA.argIsBetweenInc( 0, arrayIndex + numBytes, toArray.length, "arrayIndex+numBytes" );

            debugAddress( arrayIndex, numBytes );
        }

        unsafe.copyMemory( null, fromAddress, toArray, BYTE_ARRAY_BASE_OFFSET+arrayIndex, numBytes );
    }

    public static void copyBytes( byte[] fromArray, long fromArrayIndex, byte[] toArray, long toArrayIndex, long numBytes ) {
        copyBytes( fromArray, toInt(fromArrayIndex), toArray, toInt(toArrayIndex), numBytes);
    }

    public static void copyBytes( byte[] fromArray, int fromArrayIndex, byte[] toArray, int toArrayIndex, long numBytes ) {
        if ( SystemX.isDebugRun() ) {
            QA.argIsBetween( 0, fromArrayIndex, fromArray.length, "fromArrayIndex" );
            QA.argIsBetweenInc( 0, fromArrayIndex + numBytes, fromArray.length, "fromArrayIndex+numBytes" );

            QA.argIsBetween( 0, toArrayIndex, toArray.length, "toArrayIndex" );
            QA.argIsBetweenInc( 0, toArrayIndex + numBytes, toArray.length, "toArrayIndex+numBytes" );

            debugAddress( toArrayIndex, numBytes );
        }

        unsafe.copyMemory( fromArray, BYTE_ARRAY_BASE_OFFSET+fromArrayIndex, toArray, BYTE_ARRAY_BASE_OFFSET+toArrayIndex, numBytes );
    }



    private static Unsafe fetchUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField( "theUnsafe" );

            field.setAccessible(true);

            return (Unsafe) field.get(null);
        } catch ( Throwable e ) {
            throw new RuntimeException(e);
        }
    }

    public static long alignAddress( long ptr, int n ) {
        // commented out code has been left in to document what the maths
        // version does below;  the maths version is faster because it avoids
        // any conditional jumps that the if block can cause
        // NB it may be worth replacing the % operators with & operators..
        // after all, we are always going to be aligning to 2^n boundaries.

//        long remainder = ptr % n;
//
//        if ( remainder == 0 ) {
//            return ptr;
//        }
//
//        long padding = n - remainder;
//        return ptr + padding;

        return ptr + ((n-(ptr % n)) % n);
    }

    public static void fill( long ptr, long numBytes, byte v ) {
        debugAddress( ptr, numBytes );

        unsafe.setMemory( ptr, numBytes, v );
    }

    public static void fill( long ptr, long numBytes, int v ) {
        fill( ptr, numBytes, (byte) v );
    }

    public static void fillArray( byte[] array, long offset, long numBytes, byte v ) {
        debugAddress( offset, numBytes );

        unsafe.setMemory( array, BYTE_ARRAY_BASE_OFFSET +offset, numBytes, v );
    }


    private static final long BYTE_ARRAY_BASE_OFFSET = unsafe.arrayBaseOffset( byte[].class );
    private static final long BYTE_ARRAY_SCALE       = unsafe.arrayIndexScale( byte[].class );

    static {
        QA.isNotZero( BYTE_ARRAY_BASE_OFFSET, "BYTE_ARRAY_BASE_OFFSET" );
    }

    public static byte getByteFrom( byte[] array, long offset ) {
        return unsafe.getByte( array, BYTE_ARRAY_BASE_OFFSET + offset*BYTE_ARRAY_SCALE );
    }

    public static short getUnsignedByteFrom( byte[] array, long offset ) {
        return (short) (Backdoor.getByteFrom(array,offset) & UNSIGNED_BYTE_MASK);
    }

    public static short getShortFrom( byte[] array, long offset ) {
        return unsafe.getShort( array, BYTE_ARRAY_BASE_OFFSET + offset*BYTE_ARRAY_SCALE );
    }

    public static int getUnsignedShortFrom( byte[] array, long offset ) {
        return Backdoor.getShortFrom(array,offset) & UNSIGNED_SHORT_MASK;
    }

    public static char getCharacterFrom( byte[] array, long offset ) {
        return unsafe.getChar( array, BYTE_ARRAY_BASE_OFFSET + offset*BYTE_ARRAY_SCALE );
    }

    public static int getIntegerFrom( byte[] array, long offset ) {
        return unsafe.getInt( array, BYTE_ARRAY_BASE_OFFSET + offset * BYTE_ARRAY_SCALE );
    }

    public static long getUnsignedIntegerFrom( byte[] array, long offset ) {
        return Backdoor.getIntegerFrom(array,offset) & UNSIGNED_INT_MASK;
    }

    public static long getLongFrom( byte[] array, long offset ) {
        return unsafe.getLong( array, BYTE_ARRAY_BASE_OFFSET + offset*BYTE_ARRAY_SCALE );
    }

    public static float getFloatFrom( byte[] array, long offset ) {
        return unsafe.getFloat( array, BYTE_ARRAY_BASE_OFFSET + offset*BYTE_ARRAY_SCALE );
    }

    public static double getDoubleFrom( byte[] array, long offset ) {
        return unsafe.getDouble( array, BYTE_ARRAY_BASE_OFFSET + offset*BYTE_ARRAY_SCALE );
    }




    public static void setByteIn( byte[] array, long offset, byte v ) {
        debugAddress( offset, SIZEOF_BYTE );

        unsafe.putByte( array, BYTE_ARRAY_BASE_OFFSET + offset*BYTE_ARRAY_SCALE, v );
    }

    public static void setUnsignedByteIn( byte[] array, long offset, short v ) {
        setByteIn( array, offset, (byte) (v & UNSIGNED_BYTE_MASK) );
    }

    public static void setUnsignedByte( long offset, short v ) {
        setByte( offset, (byte) (v & UNSIGNED_BYTE_MASK) );
    }

    public static void setCharacterIn( byte[] array, long offset, char v ) {
        debugAddress( offset, SIZEOF_CHAR );

        unsafe.putChar( array, BYTE_ARRAY_BASE_OFFSET + offset * BYTE_ARRAY_SCALE, v );
    }

    public static void setShortIn( byte[] array, long offset, short v ) {
        debugAddress( offset, SIZEOF_SHORT );

        unsafe.putShort( array, BYTE_ARRAY_BASE_OFFSET + offset * BYTE_ARRAY_SCALE, v );
    }

    public static void setUnsignedShortIn( byte[] array, long offset, int v ) {
        setShortIn( array, offset, (short) (v & UNSIGNED_SHORT_MASK) );
    }

    public static void setUnsignedShort( long offset, int v ) {
        setShort( offset, (short) (v & UNSIGNED_SHORT_MASK) );
    }

    public static void setUnsignedInt( long offset, long v ) {
        setInteger( offset, (int) (v & UNSIGNED_INT_MASK) );
    }

    public static void setIntegerIn( byte[] array, long offset, int v ) {
        debugAddress( offset, SIZEOF_INT );

        unsafe.putInt( array, BYTE_ARRAY_BASE_OFFSET + offset * BYTE_ARRAY_SCALE, v );
    }

    public static void setUnsignedIntegerIn( byte[] array, long offset, long v ) {
        setIntegerIn( array, offset, (int) (v & UNSIGNED_INT_MASK) );
    }

    public static void setLongIn( byte[] array, long offset, long v ) {
        debugAddress( offset, SIZEOF_LONG );

        unsafe.putLong( array, BYTE_ARRAY_BASE_OFFSET + offset * BYTE_ARRAY_SCALE, v );
    }

    public static void setFloatIn( byte[] array, long offset, float v ) {
        debugAddress( offset, SIZEOF_FLOAT );

        unsafe.putFloat( array, BYTE_ARRAY_BASE_OFFSET + offset * BYTE_ARRAY_SCALE, v );
    }

    public static void setDoubleIn( byte[] array, long offset, double v ) {
        debugAddress( offset, SIZEOF_DOUBLE );

        unsafe.putDouble( array, BYTE_ARRAY_BASE_OFFSET + offset * BYTE_ARRAY_SCALE, v );
    }


    // NB I found setting a watch break point too slow; so to track down memory corrupts set this field instead
    public volatile static long targetAddress = 0;
    /**
     * This function is used as a place to capture writes to a memory address via a debugger.
     *
     *
     * offset <= 4681707554L && (offset+numBytes) >= 4681707554L
     */
    private static void debugAddress( long offset, long numBytes ) {
        if ( SystemX.isDebugRun() ) {
            // todo also consider verifying against a registry of allocated addresses

            if ( targetAddress > 0 && offset <= targetAddress && (offset+numBytes) > targetAddress ) {
                new RuntimeException( "wrote to "+offset+" "+numBytes+" bytes (was watching "+targetAddress+")" ).printStackTrace();
            }
        }
    }

    public static int toInt( long v ) {
        QA.argIsLTE( v, Integer.MAX_VALUE, "v" );

        return (int) v;
    }

    public static byte toByte( int v ) {
        QA.argIsLTE( v, Byte.MAX_VALUE, "v" );

        return (byte) v;
    }

    public static long calculateOffsetForField( Class clazz, String fieldName ) {
        try {
            return unsafe.objectFieldOffset( clazz.getDeclaredField( fieldName ) );
        } catch ( NoSuchFieldException ex ) {
            throwException( ex );

            throw ReflectionException.recast( ex ); // unreachable, but the compiler does not know it
        }
    }

    public static void setFloat( Object obj, long fieldOffset, float v ) {
        unsafe.putFloat( obj, fieldOffset, v );
    }

    public static float getFloat( Object obj, long fieldOffset ) {
        return unsafe.getFloat( obj, fieldOffset );
    }

    public static void setDouble( Object obj, long fieldOffset, double v ) {
        unsafe.putDouble( obj, fieldOffset, v );
    }

    public static double getDouble( Object obj, long fieldOffset ) {
        return unsafe.getDouble( obj, fieldOffset );
    }

    /**
     * Error if we loose data when down casting and assertions are enabled.
     */
    public static int safeDowncast( long v ) {
        QA.isInt( v, "v" );

        return (int) v;
    }
}
