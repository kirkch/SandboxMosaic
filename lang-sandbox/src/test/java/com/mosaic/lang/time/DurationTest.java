package com.mosaic.lang.time;

import org.junit.Test;

import static org.junit.Assert.*;
import static com.mosaic.lang.time.Duration.*;


/**
 *
 */
public class DurationTest {

    @Test
    public void testToString() {
        assertEquals( "1ms", Duration.millis(1).toString() );
        assertEquals( "1s 10ms", Duration.millis(1010).toString() );
        assertEquals( "2m", Duration.minutes(2).toString() );
    }

    @Test
    public void toDays() {
        assertEquals( 4, Duration.days(4).getDays() );
        assertEquals( 1, Duration.days(1).getDays() );
        assertEquals( 0, Duration.hours(1).getDays() );
        assertEquals( 1, Duration.hours(30).getDays() );
    }

    @Test
    public void toHours() {
        assertEquals( 4, Duration.hours(4).getHours() );
        assertEquals( 1, Duration.hours(1).getHours() );
        assertEquals( 0, Duration.minutes(1).getHours() );
        assertEquals( 1, Duration.minutes(63).getHours() );
        assertEquals( 1, Duration.minutes(101).getHours() );
    }

    @Test
    public void toMinutes() {
        assertEquals( 4, Duration.minutes(4).getMinutes() );
        assertEquals( 1, Duration.minutes(1).getMinutes() );
        assertEquals( 0, Duration.seconds(1).getMinutes() );
        assertEquals( 1, Duration.seconds(63).getMinutes() );
        assertEquals( 1, Duration.seconds(101).getMinutes() );
    }

    @Test
    public void toSeconds() {
        assertEquals( 4, Duration.seconds(4).getSeconds() );
        assertEquals( 1, Duration.seconds(1).getSeconds() );
        assertEquals( 0, Duration.millis( 1 ).getSeconds() );
        assertEquals( 1, Duration.millis(1000).getSeconds() );
        assertEquals( 1, Duration.millis( 1001 ).getSeconds() );
    }

    @Test
    public void isGTE() {
        assertTrue( seconds(100).isGTE(seconds(100)) );
        assertTrue( seconds(101).isGTE(seconds(100)) );
        assertTrue( seconds(110).isGTE(seconds(100)) );
        assertFalse( seconds(99).isGTE(seconds(100)) );
    }

    @Test
    public void isGT() {
        assertFalse( seconds(100).isGT(seconds(100)) );
        assertTrue( seconds(101).isGT(seconds(100)) );
        assertTrue( seconds(110).isGT(seconds(100)) );
        assertFalse( seconds(99).isGT(seconds(100)) );
    }

    @Test
    public void isLTE() {
        assertTrue( seconds(100).isLTE(seconds(100)) );
        assertFalse( seconds(101).isLTE(seconds(100)) );
        assertFalse( seconds(110).isLTE(seconds(100)) );
        assertTrue( seconds(99).isLTE(seconds(100)) );
    }

    @Test
    public void isLT() {
        assertFalse( seconds(100).isLT(seconds(100)) );
        assertFalse( seconds(101).isLT(seconds(100)) );
        assertFalse( seconds(110).isLT(seconds(100)) );
        assertTrue( seconds(99).isLT(seconds(100)) );
    }

}
