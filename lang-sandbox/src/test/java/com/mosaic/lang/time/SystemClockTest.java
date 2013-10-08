package com.mosaic.lang.time;

import org.junit.Test;

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

}
