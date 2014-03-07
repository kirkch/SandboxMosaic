package com.mosaic.lang;

/**
 *
 */
public class ThreadX {

    public static void sleep( long millis ) {
        try {
            Thread.sleep( millis );
        } catch ( InterruptedException ex ) {
            Backdoor.throwException( ex );
        }
    }

}
