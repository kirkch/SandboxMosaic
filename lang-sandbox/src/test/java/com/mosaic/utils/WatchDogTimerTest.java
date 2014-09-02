package com.mosaic.utils;

import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.time.DTM;
import com.mosaic.lang.time.Duration;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicLong;

import static com.mosaic.lang.time.Duration.millis;
import static com.softwaremosaic.junit.JUnitMosaic.spinUntilTrue;
import static org.junit.Assert.*;


@RunWith(JUnitMosaicRunner.class)
public class WatchDogTimerTest {

    private DebugSystem system  = new DebugSystem();
    private AtomicLong  counter = new AtomicLong(0);

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
    public void create100msWatchDog_doNotTouch_wait100ms_expectNoTrigger() {
        system.scheduleWatchDog( 100, counter::incrementAndGet );

        system.incClock( millis(100) );

        assertNoTriggers();
    }

    @Test(threadCheck=true)
    public void create100msWatchDog_doNotTouch_wait101ms_expectItToTrigger() {
        system.scheduleWatchDog( 100, counter::incrementAndGet );

        system.incClock( millis(101) );

        spinUntilTrue( () -> counter.get() == 1 );
    }

    @Test(threadCheck=true)
    public void create100MsWatchDog_doNotTouch_wait30Ms_expectNoTrigger() {
        system.scheduleWatchDog( 100, counter::incrementAndGet );

        system.incClock( millis( 30 ) );

        assertNoTriggers();
    }

    @Test(threadCheck=true)
    public void create100MsWatchDog_cancelAt30ms_wait200ms_expectNoTrigger() {
        WatchDogTimer timer = system.scheduleWatchDog( 100, counter::incrementAndGet );

        system.incClock( millis(30) );
        timer.cancel();

        system.incClock( millis(200) );
        assertNoTriggers();
    }

    @Test(threadCheck=true)
    public void create100msWatchDog_doNotTouch_wait400ms_expectItToTriggerOnce() {
        system.scheduleWatchDog( 100, counter::incrementAndGet );

        system.incClock( millis(400) );

        assertTriggeredCountEquals( 1 );
    }

    @Test(threadCheck=true)
    public void create100MsWatchDog_touchAt30ms_wait100Ms_expectNoTrigger() {
        WatchDogTimer timer = system.scheduleWatchDog( 100, counter::incrementAndGet );

        system.incClock( millis(30) );
        timer.touch();

        system.incClock( millis(100) );

        assertNoTriggers();
    }

    @Test(threadCheck=true)
    public void create100msWatchDog_touchAt30msAnd80ms_wait100ms_expectNoTrigger() {
        WatchDogTimer timer   = system.scheduleWatchDog( 100, counter::incrementAndGet );

        system.incClock( millis(30) );
        timer.touch();

        system.incClock( millis(80) );
        timer.touch();

        system.incClock( millis(100) );

        assertNoTriggers();
    }

    @Test(threadCheck=true)
    public void create100msWatchDog_touchAt30msAnd80ms_wait101Ms_expectTrigger() {
        WatchDogTimer timer   = system.scheduleWatchDog( 100, counter::incrementAndGet );

        system.incClock( millis(30) );
        timer.touch();

        system.incClock( millis(80) );
        timer.touch();

        system.incClock( millis(101) );

        assertTriggeredCountEquals( 1 );
    }


    private void assertTriggeredCountEquals( int expectedCount ) {
        Backdoor.sleep( millis( 100 ) );

        spinUntilTrue( () -> counter.get() == expectedCount );
    }

    private void assertNoTriggers() {
        Backdoor.sleep( millis(200) );
        assertEquals( 0, counter.get() );
    }

}