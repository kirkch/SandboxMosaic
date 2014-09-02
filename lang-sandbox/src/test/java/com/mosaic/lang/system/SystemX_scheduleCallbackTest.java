package com.mosaic.lang.system;

import com.mosaic.lang.Cancelable;
import com.mosaic.lang.time.DTM;
import com.mosaic.lang.time.Duration;
import com.softwaremosaic.junit.JUnitMosaic;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mosaic.lang.time.Duration.millis;
import static com.softwaremosaic.junit.JUnitMosaic.spinUntilTrue;
import static org.junit.Assert.*;


@RunWith(JUnitMosaicRunner.class)
public class SystemX_scheduleCallbackTest {
    private DebugSystem system = new DebugSystem();

    @Before
    public void setup() {
        system.clock.fixCurrentDTM( new DTM(2014,10,1, 14,0,0) );
        system.start();
    }

    @After
    public void tearDown() {
        system.stop();
    }


    @Test(threadCheck=true)
    public void scheduleCallbackInNegativeMS_expectException() {
        AtomicBoolean flag = new AtomicBoolean( false );

        try {
            system.scheduleCallback( -1, () -> flag.set( true ) );
            fail("Expected IllegalArgumentException");
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "'delay' (-1) must be >= 0", ex.getMessage() );
        }
    }

    @Test(threadCheck=true)
    public void scheduleCallbackIn100ms_checkIn60ms_expectNoCall() {
        AtomicBoolean flag = new AtomicBoolean( false );


        system.scheduleCallback( 100, () -> flag.set( true ) );

        system.incClock( millis(60) );

        Backdoor.sleep( Duration.millis(100) );

        assertFalse( flag.get() );
    }

    @Test(threadCheck=true)
    public void scheduleCallbackIn100ms_checkIn100ms_expectCall() {
        AtomicBoolean flag = new AtomicBoolean( false );


        system.scheduleCallback( 100, () -> flag.set(true) );
        system.incClock( millis(100) );


        spinUntilTrue( flag::get );
    }

    @Test(threadCheck=true)
    public void scheduleCallbackIn100ms_checkIn300ms_expectOnlyOneCall() {
        AtomicInteger flag = new AtomicInteger(0 );


        system.scheduleCallback( 100, flag::incrementAndGet );
        system.incClock( millis(300) );


        spinUntilTrue( () -> flag.get() == 1);

        Backdoor.sleep( millis(30) );
        assertEquals( 1, flag.get() );
    }

    @Test(threadCheck=true)
    public void scheduleCallbackIn100ms_cancelRequest_checkIn300ms_expectNoCall() {
        AtomicInteger flag = new AtomicInteger(0 );


        Cancelable ticket = system.scheduleCallback( 100, flag::incrementAndGet );

        ticket.cancel();
        system.incClock( millis( 300 ) );

        Backdoor.sleep( millis(60) );

        assertEquals( 0, flag.get() );
    }

    @Test(threadCheck=true)
    public void scheduleCallbackIn100ms_cancelRequestAfter200ms_expectCancelToReportThatItHadAlreadyFired() {
        AtomicInteger flag = new AtomicInteger(0 );


        Cancelable ticket = system.scheduleCallback( 100, flag::incrementAndGet );

        system.incClock( millis( 200 ) );

        spinUntilTrue( () -> flag.get() == 1);
        ticket.cancel();

        Backdoor.sleep( millis(60) );

        assertEquals( 1, flag.get() );
    }

}