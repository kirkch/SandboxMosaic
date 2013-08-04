package com.mosaic.lang.time;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 *
 */
public class DayTest {
    @Test
    public void testToString() {
        assertEquals("2010/3/20", new Day(2010,3,20).toString() );
    }

    @Test
    public void testEquals() {
        assertEquals(new Day("2010/3/20"), new Day(2010,3,20) );
        assertEquals(new Day(new DTM(2010,3,20)), new Day(2010,3,20) );
        assertEquals(new Day(new DTM(2010,3,20, 3,30,22,0)), new Day(2010,3,20) );
        assertEquals(new Day(new DTM(2010,3,20, 3,30,22,0)), new Day(new DTM(2010,3,20, 18,10,22,10)) );
        
        assertFalse(new Day(new DTM(2010,3,20, 3,30,22,0)).equals( new Day(new DTM(2010,3,21, 18,10,22,10))) );
    }
}
