package com.mosaic.lang.time;

import com.mosaic.lang.StartStoppable;
import com.mosaic.lang.system.Backdoor;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class SystemClockTest {

    private SystemClock systemClock = new SystemClock();


    @Test
    public void realTimeClock() throws InterruptedException {
        long systemMillis = System.currentTimeMillis();

        Thread.sleep(50);

        long clockMillis  = systemClock.getCurrentMillis();

        long delta = clockMillis - systemMillis;


        assertTrue( delta >= 50 && delta < 1000*60*5 );
    }

    @Test
    public void fixTime() {
        DTM fixedDTM = new DTM(2010, 3, 20, 13, 35, 11);

        systemClock.fixCurrentDTM(fixedDTM);

        assertEquals(fixedDTM, systemClock.getCurrentDTM());
    }

    @Test
    public void fixTimeThenReset() {
        DTM fixedDTM = new DTM(2010, 3, 20, 13, 35, 11);

        systemClock.fixCurrentDTM(fixedDTM);
        systemClock.reset();

        assertNotSame(fixedDTM, systemClock.getCurrentDTM());
    }

    @Test
    public void flowCurrentDTMFrom() throws InterruptedException {
        DTM fixedDTM = new DTM(2010, 3, 20, 13, 35, 11);

        systemClock.flowCurrentDTMFrom(fixedDTM);


        assertNotSame(fixedDTM, systemClock.getCurrentDTM());

        Thread.sleep(50);

        long clockMillis  = systemClock.getCurrentMillis();

        long delta = clockMillis - fixedDTM.getMillisSinceEpoch();


        assertTrue(delta >= 50 && delta < 1000 * 60 * 5);

        DTM nowDTM = systemClock.getCurrentDTM();

        assertEquals( 2010, nowDTM.getYear() );
        assertEquals( 3, nowDTM.getMonth() );
        assertEquals( 20, nowDTM.getDayOfMonth() );
        assertEquals( 13, nowDTM.getHour() );
    }

    @Test
    public void mmMappedClock() throws IOException {
        SystemClock systemClock2 = new SystemClock();

        File tmpFile =  File.createTempFile( "mmclock", ".dat" );

        try {
            systemClock.memoryMapClock( tmpFile );

            // ensure that a fresh mmClock returns System.currentTimeMillis
            long t0 = System.currentTimeMillis();

            assertTrue( systemClock.getCurrentMillis() >= t0 );

            // override the time on mmClock -- expect the time to change
            systemClock.set( 1234 );
            assertEquals( 1234, systemClock.getCurrentMillis() );

            // systemClock2 has not been memory mapped, so it will not have changed
            assertTrue( systemClock2.getCurrentMillis() != 1234 );

            // memory map systemClock2 and expect it to get the same value as systemClock1
            systemClock2.memoryMapClock( tmpFile );
            assertEquals( 1234, systemClock2.getCurrentMillis() );

            systemClock2.add(Duration.millis(2));
            assertEquals( 1236, systemClock.getCurrentMillis() );
            assertEquals( 1236, systemClock2.getCurrentMillis() );
        } finally {
            systemClock.stop();
            systemClock2.stop();

            tmpFile.delete();
        }
    }
}
