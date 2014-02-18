package com.mosaic.lang;

import com.mosaic.lang.time.Duration;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A utility wrapper around sun.misc.Unsafe.  Until such time that Unsafe
 * becomes standardised; Java 9?
 */
public class Backdoor {

    private static final Unsafe unsafe = fetchUnsafe();


    private static final AtomicLong mallocCounter = new AtomicLong(0);


    public static long alloc( long numBytes ) {
        long ptr = unsafe.allocateMemory( numBytes );

        mallocCounter.incrementAndGet();

        return ptr;
    }

    public static void free( long address ) {
        unsafe.freeMemory(address);

        mallocCounter.decrementAndGet();
    }

    /**
     * Returns a count of how many more calls to alloc() than free().  The counter
     * is incremented when alloc() is called, and decremented when free() is
     * called.
     */
    public static long getActiveAllocCounter() {
        return mallocCounter.get();
    }

    public static void throwException( Throwable ex ) {
        unsafe.throwException( ex );
    }

    public static void sleep( Duration sleepFor ) {
        long millis = sleepFor.getMillis();

        Validate.argIsGTEZero( millis, "millis" );

        try {
            Thread.sleep( millis );
        } catch ( InterruptedException ex ) {
            throwException( ex );
        }
    }



// PRIMITIVE VALUE GETTER/SETTERS


//    public boolean getBoolean( long address ) {
//        return unsafe.getBoolean( address );
//    }

    public static byte getByte( long address ) {
        return unsafe.getByte( address );
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

//    public static boolean setBoolean( long address, boolean v ) {
//        return unsafe.setBoolean( address, v );
//    }

    public static void setByte( long address, byte v ) {
        unsafe.putByte( address, v );
    }

    public static void setCharacter( long address, char v ) {
        unsafe.putChar( address, v );
    }

    public static void setShort( long address, short v ) {
        unsafe.putShort( address, v );
    }

    public static void setInteger( long address, int v ) {
        unsafe.putInt( address, v );
    }

    public static void setLong( long address, long v ) {
        unsafe.putLong( address, v );
    }

    public static void setFloat( long address, float v ) {
        unsafe.putFloat( address, v );
    }

    public static void setDouble( long address, double v ) {
        unsafe.putDouble( address, v );
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
        unsafe.setMemory( ptr, numBytes, v );
    }

    public static void fillArray( byte[] array, long offset, long numBytes, byte v ) {
        unsafe.setMemory( array, B+offset, numBytes, v );
    }


    private static final long B = unsafe.arrayBaseOffset( byte[].class ) * unsafe.arrayIndexScale( byte[].class );

    static {
        Validate.isNotZero( B, "B" );
    }

    public static byte getByteFrom( byte[] array, long offset ) {
        return unsafe.getByte( array, B + offset );
    }

    public static short getShortFrom( byte[] array, long offset ) {
        return unsafe.getShort( array, B + offset );
    }

    public static char getCharacterFrom( byte[] array, long offset ) {
        return unsafe.getChar( array, B + offset );
    }

    public static int getIntegerFrom( byte[] array, long offset ) {
        return unsafe.getInt( array, B + offset );
    }

    public static long getLongFrom( byte[] array, long offset ) {
        return unsafe.getLong( array, B + offset );
    }

    public static float getFloatFrom( byte[] array, long offset ) {
        return unsafe.getFloat( array, B + offset );
    }

    public static double getDoubleFrom( byte[] array, long offset ) {
        return unsafe.getDouble( array, B + offset );
    }




    public static void setByteIn( byte[] array, long offset, byte v ) {
        unsafe.putByte( array, B + offset, v );
    }

    public static void setCharacterIn( byte[] array, long offset, char v ) {
        unsafe.putChar( array, B + offset, v );
    }

    public static void setShortIn( byte[] array, long offset, short v ) {
        unsafe.putShort( array, B + offset, v );
    }

    public static void setIntegerIn( byte[] array, long offset, int v ) {
        unsafe.putInt( array, B + offset, v );
    }

    public static void setLongIn( byte[] array, long offset, long v ) {
        unsafe.putLong( array, B + offset, v );
    }

    public static void setFloatIn( byte[] array, long offset, float v ) {
        unsafe.putFloat( array, B + offset, v );
    }

    public static void setDoubleIn( byte[] array, long offset, double v ) {
        unsafe.putDouble( array, B + offset, v );
    }

}
