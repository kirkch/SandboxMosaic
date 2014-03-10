package com.mosaic.lang.time;


import com.mosaic.lang.QA;
import com.mosaic.lang.ThreadSafe;
import com.mosaic.lang.QA;

/**
 * Access the current date and time.  Use in preference to System.currentTimeMillis()
 * because this class can be adjusted for unit and system testing purposes.
 */
@ThreadSafe
public final class SystemClock {

    private volatile Clock clock;


    public SystemClock() {
        reset();
    }

    public SystemClock( DTM currentDTM ) {
        fixCurrentDTM(currentDTM);
    }

    public SystemClock( int year, int month, int day ) {
        this(year, month, day, 0, 0, 0);
    }

    public SystemClock( int year, int month, int day, int hour, int minute, int seconds ) {
        this( new DTM(year,month,day, hour,minute,seconds) );
    }


    /**
     * Returns the current date and time.
     */
    public DTM getCurrentDTM() {
        return clock.getCurrentDTM();
    }

    /**
     * Returns the current time in millis from since the first of Jan 1970.
     */
    public long getCurrentMillis() {
        return getCurrentDTM().getMillisSinceEpoch();
    }


    /**
     * Fix the current date and time to the specified point in time.  Time will
     * not move forward.  Use during unit tests.  This will not affect timeOperationNanos.
     */
    public void fixCurrentDTM(DTM dtm) {
        this.clock = new FixedTimeClock(dtm);
    }

    /**
     * Set the current date and time to the specified point in time and allow
     * time to then move forward at the normal rate.  Use during acceptance tests.
     */
    public void flowCurrentDTMFrom( DTM dtm ) {
        this.clock = new OffsetClock(dtm);
    }


    /**
     * Return this system clock to calling System.currentTimeMillis.
     */
    public void reset() {
        this.clock = new RealTimeClock();
    }

    /**
     * A convenience method for timing how long an operation takes in nanoseconds.
     * If runnable throws an exception then it will percolate up and abort the
     * measurement.
     */
    public long timeOperationNanos( Runnable f ) {
        long startNanos = System.nanoTime();

        f.run();  // NB: no exception handling, an exception is considered as aborting the measurement

        return System.nanoTime() - startNanos;
    }



    private interface Clock {
        public DTM getCurrentDTM();
    }

    private class RealTimeClock implements Clock {
        public DTM getCurrentDTM() {
            return new DTM(System.currentTimeMillis());
        }
    }

    private class FixedTimeClock implements Clock {
        private DTM fixedDTM;

        private FixedTimeClock( DTM fixedDTM ) {
            QA.notNull( fixedDTM, "fixedDTM" );

            this.fixedDTM = fixedDTM;
        }

        public DTM getCurrentDTM() {
            return fixedDTM;
        }
    }

    private class OffsetClock implements Clock {
        private long offset;
        private long startMillis;

        public OffsetClock( DTM fromDTM ) {
            startMillis = System.currentTimeMillis();
            offset      = fromDTM.getMillisSinceEpoch();
        }

        public DTM getCurrentDTM() {
            long nowMillis = System.currentTimeMillis();
            long delta     = nowMillis - startMillis;

            return new DTM( offset+delta );
        }
    }

}
