package com.mosaic.lang.time;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


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

}
