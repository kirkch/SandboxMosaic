package com.mosaic.lang.system;

import com.mosaic.lang.system.Backdoor;


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
