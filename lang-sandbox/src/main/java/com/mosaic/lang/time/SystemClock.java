package com.mosaic.lang.time;


/**
 *
 */
public class SystemClock {

    private volatile DTM currentDTMNbl;


    public SystemClock() {}

    public SystemClock( DTM currentDTM ) {
        this.currentDTMNbl = currentDTM;
    }


    public DTM getCurrentDTM() {
        DTM now = currentDTMNbl;
        if ( now == null ) {
            now = new DTM( System.currentTimeMillis() );
        }

        return now;
    }

    public long getCurrentMillis() {
        return getCurrentDTM().getMillisSinceEpoch();
    }

    public void setCurrentDTM( DTM dtm ) {
        this.currentDTMNbl = dtm;
    }

    /**
     * Return this system clock to System.currentTimeMillis.
     */
    public void reset() {
        this.currentDTMNbl = null;
    }

}
