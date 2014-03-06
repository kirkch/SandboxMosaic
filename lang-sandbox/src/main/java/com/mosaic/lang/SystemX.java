package com.mosaic.lang;

import java.nio.charset.Charset;
import java.util.Random;


/**
 *
 */
public class SystemX {

    public static final int BYTE_SIZE   = 1;
    public static final int SHORT_SIZE  = 2;
    public static final int CHAR_SIZE   = 2;
    public static final int INT_SIZE    = 4;
    public static final int LONG_SIZE   = 8;
    public static final int FLOAT_SIZE  = 4;
    public static final int DOUBLE_SIZE = 8;


    public static final int  UNSIGNED_BYTE_MASK  = 0xFF;
    public static final int  UNSIGNED_SHORT_MASK = 0xFFFF;
    public static final long UNSIGNED_INT_MASK   = 0xFFFFFFFF;


    public static Charset UTF8  = Charset.forName( "UTF8" );
    public static Charset ASCII = Charset.forName( "ASCII" );


    private static final Random RND = new Random();

    private static final boolean areAssertionsEnabled = detectWhetherAssertionsAreEnabled();


    public static int getCacheLineLengthBytes() {
        return 64;  // todo detect or configure this
    }

    /**
     * Returns true when the JVM is in debug mode.  Toggled via the assertions
     * flag -ea.  This flag can be used to strip out debug checks that slow
     * the JVM down.
     */
    public static boolean isDebugRun() {
        return areAssertionsEnabled;
    }

    /**
     * Returns true if we are willing to sacrifice important safety checks for
     * extra speed.
     */
    public static boolean isRecklessRun() {
        return !areAssertionsEnabled;
    }

    public static String getTempDirectory() {
        return System.getProperty( "java.io.tmpdir" );
    }

    public static long nextRandomLong() {
        return RND.nextLong();
    }



    // The latest Intel processors have 3 layers (L1D, L2, and L3); with
    // sizes 32KB, 256KB, and 4-30MB; and ~1ns, ~4ns, and ~15ns latency respectively for a 3.0GHz CPU.


    @SuppressWarnings({"AssertWithSideEffects", "UnusedAssignment"})
    private static boolean detectWhetherAssertionsAreEnabled() {
        boolean flag = false;

        assert (flag = true);

        return flag;
    }

}
