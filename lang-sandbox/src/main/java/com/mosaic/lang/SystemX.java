package com.mosaic.lang;

/**
 *
 */
public class SystemX {

    public static int getCacheLineLengthBytes() {
        return 64;  // todo detect or configure this
    }

    public static boolean isDebugRun() {
        return true;
    }



    // The latest Intel processors have 3 layers (L1D, L2, and L3); with
    // sizes 32KB, 256KB, and 4-30MB; and ~1ns, ~4ns, and ~15ns latency respectively for a 3.0GHz CPU.
}
