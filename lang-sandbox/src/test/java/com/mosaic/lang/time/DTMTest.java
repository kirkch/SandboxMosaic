package com.mosaic.lang.time;

import org.junit.Test;

import java.util.Calendar;

import static junit.framework.Assert.assertEquals;


/**
 *
 */
public class DTMTest {

    @Test
    public void testGetYear() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals( 2010, dtm.getYear() );
    }

    @Test
    public void testGetMonth() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals( 10, dtm.getMonth() );
    }

    @Test
    public void testGetDay() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals( 25, dtm.getDayOfMonth() );
    }

    @Test
    public void testGetHour() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals(23, dtm.getHour());
    }

    @Test
    public void testGetMinutes() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals( 35, dtm.getMinutes() );
    }

    @Test
    public void testGetSeconds() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals( 22, dtm.getSeconds() );
    }

    @Test
    public void testGetMillis() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22, 854 );

        assertEquals( 854, dtm.getMillis() );
    }

    @Test
    public void testCompareTo() throws Exception {
        DTM tMinus1 = new DTM( 2010,10,25, 23,35,22, 853 );
        DTM t       = new DTM( 2010,10,25, 23,35,22, 854 );
        DTM tPlus1  = new DTM( 2010,10,25, 23,35,22, 855 );

        assertEquals(  1, t.compareTo(tMinus1) );
        assertEquals(  0, t.compareTo(new DTM(2010,10,25, 23,35,22, 854)) );
        assertEquals( -1, t.compareTo(tPlus1) );
    }


    @Test
    public void testPlusDays() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals( 25, dtm.add(Duration.days(0)).asJDKCalendar().get(Calendar.DAY_OF_MONTH) );
        assertEquals( 26, dtm.add(Duration.days(1)).asJDKCalendar().get(Calendar.DAY_OF_MONTH) );
        assertEquals( 27, dtm.add(Duration.days(2)).asJDKCalendar().get(Calendar.DAY_OF_MONTH) );
        assertEquals( 24, dtm.add(Duration.days(-1)).asJDKCalendar().get(Calendar.DAY_OF_MONTH) );
    }

    @Test
    public void testPlusMinutes() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals( 35, dtm.add(Duration.minutes(0)).getMinutes() );
        assertEquals( 36, dtm.add(Duration.minutes(1)).getMinutes() );
        assertEquals( 37, dtm.add(Duration.minutes(2)).getMinutes() );
        assertEquals(34, dtm.add(Duration.minutes(-1)).getMinutes());
    }

    @Test
    public void testPlusMillis() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals( 0, dtm.add(Duration.millis(0)).asJDKCalendar().get(Calendar.MILLISECOND) );
        assertEquals( 1, dtm.add(Duration.millis(1)).asJDKCalendar().get(Calendar.MILLISECOND) );
        assertEquals( 2, dtm.add(Duration.millis(2)).asJDKCalendar().get(Calendar.MILLISECOND) );
        assertEquals( 999, dtm.add(Duration.millis(-1)).asJDKCalendar().get(Calendar.MILLISECOND) );
    }


    @Test
    public void testCreateWithMillis() {
        DTM dtm = new DTM(100000);

        assertEquals( 100000, dtm.getMillisSinceEpoch() );
    }

}
