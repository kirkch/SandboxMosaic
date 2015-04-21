package com.mosaic.lang.time;


import com.mosaic.lang.QA;
import com.mosaic.lang.Service;
import com.mosaic.lang.ServiceMixin;
import com.mosaic.lang.ThreadSafe;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 * Access the current date and time.  Use in preference to System.currentTimeMillis()
 * because this class can be adjusted for unit and system testing purposes.
 */
@ThreadSafe
public final class SystemClock extends ServiceMixin<SystemClock> {

    private volatile Clock clock;


    public SystemClock() {
        super("systemClock");

        reset();
    }

    public SystemClock( DTM currentDTM ) {
        super("systemClock");

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
        changeClockTo( new FixedTimeClock(dtm) );
    }

    /**
     * Set the current date and time to the specified point in time and allow
     * time to then move forward at the normal rate.  Use during acceptance tests.
     */
    public void flowCurrentDTMFrom( DTM dtm ) {
        changeClockTo( new OffsetClock(dtm) );
    }


    /**
     * Link this clock to a region of memory that may be shared with other processes.  Any 'long'
     * written to this file will be readable from all SystemClocks that have been linked to that
     * file.  Because the contents of the file is mapped into the memory space of the running
     * processes, this inter-process communication does not require disk i/o and actually occurs
     * at the CPU cache cohesion level.  Thus it is exceptionally fast.
     */
    public void memoryMapClock( File f ) {
        changeClockTo( new MMClock(f) );
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

    public void add( Duration duration ) {
        clock.add( duration );
    }

    public void set( DTM newDTM ) {
        set( newDTM.getMillisSinceEpoch() );
    }

    public void set( long millisSinceEpoch ) {
        clock.set( millisSinceEpoch );
    }


    protected void doStop() throws Exception {
        Service.stopService( clock );
    }

    private void changeClockTo( Clock newClock ) {
        Service.stopService( this.clock );

        this.clock = Service.startService( newClock );
    }

    private interface Clock {
        public DTM getCurrentDTM();

        public void add( Duration duration );

        public void set( long millisSinceEpoch );
    }

    private class RealTimeClock implements Clock {
        public DTM getCurrentDTM() {
            return new DTM(System.currentTimeMillis());
        }

        public void add( Duration duration ) {
            throw new UnsupportedOperationException();
        }

        public void set( long millisSinceEpoch ) {
            throw new UnsupportedOperationException();
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

        public void add( Duration duration ) {
            fixedDTM = fixedDTM.add( duration );
        }

        public void set( long millisSinceEpoch ) {
            fixedDTM = new DTM( millisSinceEpoch );
        }
    }


    /**
     * A clock backed by a memory mapped file.  The use of a memory mapped file lets the clock
     * share its time with other processes.
     *
     * It is suggested to only have one process writing to the memory mapped file, everybody else
     * can just read from it.  This keeps contention on the memory address low.
     */
    private class MMClock extends ServiceMixin<MMClock> implements Clock {
        private final File file;

        private long             currentMillisPtr;
        private RandomAccessFile randomAccessFile;
        private DirectBuffer     directBuffer;

        private MappedByteBuffer buf;

        private MMClock( File file ) {
            super( "MMClock("+file.getAbsolutePath()+")");

            this.file = file;
        }

        public DTM getCurrentDTM() {
            long millis = 0;
            if ( currentMillisPtr != 0 ) {
                millis = Backdoor.getLong( currentMillisPtr );
            }

            if ( millis == 0 ) {
                millis = System.currentTimeMillis();
            }

            return new DTM(millis);
        }

        public void add( Duration duration ) {
            if ( currentMillisPtr == 0 ) {
                throw new IllegalStateException( "File is not mapped into memory" );
            }

            long deltaMillis    = duration.getMillis();
            long originalMillis = Backdoor.getLong( currentMillisPtr );

            Backdoor.setLong( currentMillisPtr, originalMillis+deltaMillis );
        }

        public void set( long millisSinceEpoch ) {
            if ( currentMillisPtr == 0 ) {
                throw new IllegalStateException( "File is not mapped into memory" );
            }

            Backdoor.setLong( currentMillisPtr, millisSinceEpoch );
        }

        protected void doStart() throws Exception {
            super.doStart();

            randomAccessFile = new RandomAccessFile( file, "rw" );

            if ( !file.getParentFile().exists() ) {
                file.getParentFile().mkdirs();
            }

            if ( !file.exists() || file.length() < 8) {
                randomAccessFile.setLength( SystemX.SIZEOF_LONG );
            }

            FileChannel channel = randomAccessFile.getChannel();
            buf = channel.map( FileChannel.MapMode.READ_WRITE, 0, SystemX.SIZEOF_LONG );

            directBuffer = (DirectBuffer) buf;

            currentMillisPtr = directBuffer.address();
        }

        protected void doStop() throws Exception {
            super.doStop();

            try {
                this.currentMillisPtr = 0;

                this.randomAccessFile.close();
            } catch ( IOException ex ) {
                Backdoor.throwException( ex );
            } finally {
                Cleaner cleaner = this.directBuffer.cleaner();
                if ( cleaner != null ) {
                    cleaner.clean();
                }

                this.randomAccessFile = null;
                this.directBuffer     = null;
            }
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

        public void add( Duration duration ) {
            throw new UnsupportedOperationException();
        }

        public void set( long millisSinceEpoch ) {
            startMillis = millisSinceEpoch;
        }
    }

}
