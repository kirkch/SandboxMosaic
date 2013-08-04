package com.mosaic.lang.time;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void testGetDayOfWeek() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals( DayOfWeek.MONDAY, dtm.getDayOfWeek() );
        assertEquals( DayOfWeek.TUESDAY, dtm.plusDays(1).getDayOfWeek() );
        assertEquals( DayOfWeek.WEDNESDAY, dtm.plusDays(2).getDayOfWeek() );
        assertEquals( DayOfWeek.THURSDAY, dtm.plusDays(3).getDayOfWeek() );
        assertEquals( DayOfWeek.FRIDAY, dtm.plusDays(4).getDayOfWeek() );
        assertEquals( DayOfWeek.SATURDAY, dtm.plusDays(5).getDayOfWeek() );
        assertEquals( DayOfWeek.SUNDAY, dtm.plusDays(6).getDayOfWeek() );
    }

    @Test
    public void testGetHour() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals( 23, dtm.getHour() );
    }

    @Test
    public void testGetHourEST() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 15,35,22, DTM.getTimeZone("EST") );

        assertEquals( 15, dtm.getHour() );

        DTM utcDTM = dtm.withTimeZone( TZ.UTC );
        assertEquals( 15+5, utcDTM.getHour() );
    }

    @Test
    public void testGetHourUTC2EST() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 15,35,22, DTM.getTimeZone("UTC") );

        assertEquals( 15, dtm.getHour() );

        DTM estDTM = dtm.withTimeZone( TZ.EST );
        assertEquals( 15-5, estDTM.getHour() );
    }

    @Test
    public void testGetHour2EST() throws Exception {
        DTM dtm = new DTM(2010,10,25, 15,35,22).withTimeZone(TZ.EST);

        assertEquals( 15-5, dtm.getHour() );
    }

    @Test
    public void testDefaultTimeZoneIsUTC() throws Exception {
        DTM dtm = new DTM(2010,10,25, 15,35,22);

        assertEquals( TZ.UTC, dtm.getTimeZone()  );
    }

    @Test
    public void testChangingTimeZoneTwiceHasNoEffect() throws Exception {
        DTM dtm1 = new DTM(2010,10,25, 15,35,22);
        DTM dtm2 = dtm1.withTimeZone(TZ.EST);
        DTM dtm3 = dtm2.withTimeZone(TZ.EST);

        assertTrue( dtm1 != dtm2 );
        assertTrue( dtm2 == dtm3 );
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

        assertEquals( 25, dtm.plusDays(0).getDayOfMonth() );
        assertEquals( 26, dtm.plusDays(1).getDayOfMonth() );
        assertEquals( 27, dtm.plusDays(2).getDayOfMonth() );
        assertEquals( 24, dtm.plusDays(-1).getDayOfMonth() );
    }

    @Test
    public void testPlusMinutes() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals( 35, dtm.plusMinutes(0).getMinutes() );
        assertEquals( 36, dtm.plusMinutes(1).getMinutes() );
        assertEquals( 37, dtm.plusMinutes(2).getMinutes() );
        assertEquals( 34, dtm.plusMinutes(-1).getMinutes() );
    }

    @Test
    public void testPlusMillis() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        assertEquals( 0, dtm.plusMillis(0).getMillis() );
        assertEquals( 1, dtm.plusMillis(1).getMillis() );
        assertEquals( 2, dtm.plusMillis(2).getMillis() );
        assertEquals( 999, dtm.plusMillis(-1).getMillis() );
    }

//    @Test
//    public void testPlus() throws Exception {
//        DTM dtm = new DTM( 2010,10,25, 23,35,22 );
//
//        assertEquals( 0, dtm.plus(millis(0)).getMillis() );
//        assertEquals( 1, dtm.plus(millis(1)).getMillis() );
//        assertEquals( 2, dtm.plus(millis(2)).getMillis() );
//        assertEquals( 999, dtm.plus(millis(-1)).getMillis() );
//    }

    @Test
    public void testWithTimeDefaultTimeZone() throws Exception {
        DTM dtm = new DTM( 2010,8,25, 23,35,22 );

        dtm = dtm.withTime( 13,10,11, TZ.UTC );

        assertEquals( 13, dtm.getHour() );
        assertEquals( 10, dtm.getMinutes() );
        assertEquals( 11, dtm.getSeconds() );
        assertEquals(  0, dtm.getMillis() );
    }

    @Test
    public void testWithTimeChangeTimeZone() throws Exception {
        DTM dtm = new DTM( 2010,10,25, 23,35,22 );

        dtm = dtm.withTime( 13,10,11, TZ.CET );

        assertEquals( 13, dtm.getHour() );
        assertEquals( 10, dtm.getMinutes() );
        assertEquals( 11, dtm.getSeconds() );
        assertEquals(  0, dtm.getMillis() );

        assertEquals( 11, dtm.withTimeZone( TZ.UTC).getHour() );
    }
}
