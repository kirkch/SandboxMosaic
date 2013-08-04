package com.mosaic.lang.time;


/**
 *
 */
public class SystemClock {

    public DTM getCurrentDTM() {
        return new DTM( getCurrentMillis() );
    }

    public long getCurrentMillis() {
        return System.currentTimeMillis();
    }

}
