package com.mosaic.utils;

import com.mosaic.lang.QA;
import com.mosaic.lang.functional.VoidFunction0;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.time.Duration;


/**
 * Invokes a callback after a specified period of inactivity.  Inactivity is measured from the last
 * time touch() was invoked.
 */
public class WatchDogTimer {

    private final SystemX       system;
    private final Duration      alertAfter;
    private final VoidFunction0 alertCallback;

    private long    lastTouched;
    private boolean isCancelled;


    public WatchDogTimer( SystemX system, Duration alertAfter, VoidFunction0 alertCallback ) {
        this.system        = system;
        this.alertAfter    = alertAfter;
        this.alertCallback = alertCallback;

        touch();
        scheduleCheck( alertAfter.getMillis() );
    }


    /**
     * Reset the inactivity timer.
     */
    public synchronized void touch() {
        this.lastTouched = system.getCurrentMillis();
    }

    public synchronized void cancel() {
        this.isCancelled = true;
    }

    private synchronized void checkTimer() {
        if ( isCancelled ) {
            return;
        }


        long nowMillis            = system.getCurrentMillis();
        long sinceLastTouchMillis = nowMillis - lastTouched;
        long alertAfterMillis     = alertAfter.getMillis();


        if ( sinceLastTouchMillis > alertAfterMillis ) {
            alertCallback.invoke();
        } else {
            scheduleCheck( alertAfterMillis-sinceLastTouchMillis );
        }
    }

    private synchronized void scheduleCheck( long millis ) {
        QA.argIsGTZero( millis, "millis" );

        system.scheduleCallback( millis, this::checkTimer );
    }

}
