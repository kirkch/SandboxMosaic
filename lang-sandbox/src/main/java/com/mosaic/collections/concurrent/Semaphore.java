package com.mosaic.collections.concurrent;

import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.time.Duration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * A wrapper to the JDK Semaphore that does not throw checked exceptions.
 */
public class Semaphore {
    private java.util.concurrent.Semaphore semaphore;

    public Semaphore( int numPermits ) {
        this.semaphore = new java.util.concurrent.Semaphore(numPermits);
    }

    public void acquire() {
        acquire( 1 );
    }

    public void acquire( int numPermits ) {
        try {
            semaphore.acquire();
        } catch ( InterruptedException ex ) {
            Backdoor.throwException(ex);
        }
    }

    public void acquireOrError( Duration timeout ) {
        acquireOrError( 1, timeout );
    }

    public void acquireOrError( int numPermits, Duration timeout ) {
        try {
            boolean wasSuccessful = this.semaphore.tryAcquire( numPermits, timeout.getMillis(), TimeUnit.MILLISECONDS );

            if ( !wasSuccessful ) {
                Backdoor.throwException( new TimeoutException("Semaphore timed out after " + timeout) );
            }
        } catch ( InterruptedException ex ) {
            Backdoor.throwException(ex);
        }
    }

    public void release() {
        semaphore.release();
    }

    public void release( int numPermits ) {
        semaphore.release(numPermits);
    }


    private static final Duration SPIN_INTERVAL = new Duration(10);

    public void awaitPermitCountToReachOrError( int targetPermitCount, Duration timeout ) {
        long startMillis = System.currentTimeMillis();
        long maxMillis   = startMillis + timeout.getMillis();

        while ( semaphore.availablePermits() != targetPermitCount ) {
            if ( System.currentTimeMillis() > maxMillis ) {
                Backdoor.throwException( new TimeoutException("Semaphore timed out after " + timeout) );
            }

            Backdoor.sleep( SPIN_INTERVAL );
        }
    }
}
