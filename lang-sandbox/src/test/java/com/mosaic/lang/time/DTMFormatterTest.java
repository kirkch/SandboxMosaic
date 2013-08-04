package com.mosaic.lang.time;

import org.junit.Test;

import java.text.ParseException;

import static junit.framework.Assert.assertEquals;

/**
 *
 */
public class DTMFormatterTest {
    @Test
    public void formatDTM() {
        DTMFormatter dayFormatter = new DTMFormatter( "yyyyMMdd" );
        DTMFormatter dtmFormatter = new DTMFormatter( "yyyyMMdd HH:mm:ss z" );

        assertEquals( "20070620", dayFormatter.format(new DTM(2007,6,20, 11,0,0)) );
        assertEquals( "20070620 11:00:00 UTC", dtmFormatter.format(new DTM(2007,6,20, 11,0,0)) );
        assertEquals( "20070620 14:00:00 UTC", dtmFormatter.format(new DTM(2007,6,20, 14,0,0)) );
    }

    @Test
    public void formatDTMEST() {
        DTMFormatter dtmFormatter = new DTMFormatter( "yyyyMMdd HH:mm:ss z" );

        assertEquals( "20070620 11:00:00 EST", dtmFormatter.format(new DTM(2007,6,20, 11,0,0,TZ.EST)) );
        assertEquals( "20070620 06:00:00 EST", dtmFormatter.format(new DTM(2007,6,20, 11,0,0).withTimeZone(TZ.EST)) );
        assertEquals( "20070620 16:00:00 CEST", dtmFormatter.format(new DTM(2007,6,20, 14,0,0).withTimeZone(TZ.CET)) );
        assertEquals( "20071120 15:00:00 CET", dtmFormatter.format(new DTM(2007,11,20, 14,0,0).withTimeZone(TZ.CET)) );
    }

    @Test
    public void formatDay() {
        DTMFormatter f = new DTMFormatter( "yyyyMMdd" );

        assertEquals( "20070620", f.format(new Day(2007,6,20)) );
    }

    @Test
    public void parseDTM() throws ParseException {
        DTMFormatter f = new DTMFormatter( "yyyyMMdd HH:mm:ss z" );

        DTM a = f.parseDTM( "20070620 16:33:22 EST" );

        assertEquals( new Day(2007,6,20), a.getDate() );

        assertEquals( 2007, a.getYear() );
        assertEquals(    6, a.getMonth() );
        assertEquals(   20, a.getDayOfMonth() );
        assertEquals(   21, a.getHour() );
        assertEquals(   33, a.getMinutes() );
        assertEquals(   22, a.getSeconds() );
        assertEquals(    0, a.getMillis() );

        assertEquals( TZ.UTC, a.getTimeZone() );


        Day d = f.parseDay( "20070620 16:33:22 EST" );
        assertEquals( 2007, d.getYear() );
        assertEquals(    6, d.getMonthOfYear() );
        assertEquals(   20, d.getDayOfMonth() );
    }

    @Test
    public void parseDTM_withNonDefaultSourceTimeZone() throws ParseException {
        DTMFormatter f = new DTMFormatter( "yyyyMMdd HH:mm:ss", TZ.EST );

        DTM a = f.parseDTM( "20070620 16:33:22" );

        assertEquals( new Day(2007,6,20), a.getDate() );

        assertEquals( 2007, a.getYear() );
        assertEquals(    6, a.getMonth() );
        assertEquals(   20, a.getDayOfMonth() );
        assertEquals(   16, a.getHour() );
        assertEquals(   33, a.getMinutes() );
        assertEquals(   22, a.getSeconds() );
        assertEquals(    0, a.getMillis() );

        assertEquals( TZ.EST, a.getTimeZone() );


        Day d = f.parseDay( "20070620 16:33:22" );
        assertEquals( 2007, d.getYear() );
        assertEquals(    6, d.getMonthOfYear() );
        assertEquals(   20, d.getDayOfMonth() );
    }

    @Test
    public void parseDTM_withDefaultTimeZoneOfUTC() throws ParseException {
        DTMFormatter f = new DTMFormatter( "yyyyMMdd HH:mm:ss" );

        DTM a = f.parseDTM( "20070620 16:33:22" );

        assertEquals( new Day(2007,6,20), a.getDate() );

        assertEquals( 2007, a.getYear() );
        assertEquals(    6, a.getMonth() );
        assertEquals(   20, a.getDayOfMonth() );
        assertEquals(   16, a.getHour() );
        assertEquals(   33, a.getMinutes() );
        assertEquals(   22, a.getSeconds() );
        assertEquals(    0, a.getMillis() );

        assertEquals( TZ.UTC, a.getTimeZone() );


        Day d = f.parseDay( "20070620 16:33:22" );
        assertEquals( 2007, d.getYear() );
        assertEquals(    6, d.getMonthOfYear() );
        assertEquals(   20, d.getDayOfMonth() );
    }

    @Test
    public void parseDay() throws ParseException {
        DTMFormatter f = new DTMFormatter( "yyyyMMdd" );

        DTM a = f.parseDTM( "20070620" );

        assertEquals( new Day(2007,6,20), a.getDate() );

        assertEquals( 2007, a.getYear() );
        assertEquals(    6, a.getMonth() );
        assertEquals(   20, a.getDayOfMonth() );
        assertEquals(    0, a.getHour() );
        assertEquals(    0, a.getMinutes() );
        assertEquals(    0, a.getSeconds() );
        assertEquals(    0, a.getMillis() );

        assertEquals( TZ.UTC, a.getTimeZone() );


        Day d = f.parseDay( "20070620 16:33:22 EST" );
        assertEquals( 2007, d.getYear() );
        assertEquals(    6, d.getMonthOfYear() );
        assertEquals(   20, d.getDayOfMonth() );
    }
}
