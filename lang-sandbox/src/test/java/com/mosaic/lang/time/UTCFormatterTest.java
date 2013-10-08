package com.mosaic.lang.time;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 *
 */
public class UTCFormatterTest {
    @Test
    public void formatDTM() {
        UTCFormatter dayFormatter = new UTCFormatter( "yyyyMMdd" );
        UTCFormatter UTCFormatter = new UTCFormatter( "yyyyMMdd HH:mm:ss z" );

        assertEquals( "20070620", dayFormatter.format(new DTM(2007,6,20, 11,0,0)) );
        assertEquals( "20070620 11:00:00 UTC", UTCFormatter.format(new DTM(2007,6,20, 11,0,0)) );
        assertEquals( "20070620 14:00:00 UTC", UTCFormatter.format(new DTM(2007,6,20, 14,0,0)) );
    }

//    @Test
//    public void formatDTMEST() {
//        UTCFormatter dtmFormatter = new UTCFormatter( "yyyyMMdd HH:mm:ss z" );
//
//        assertEquals( "20070620 11:00:00 EST", dtmFormatter.format(new DTM(2007,6,20, 11,0,0,TZ.EST)) );
//        assertEquals( "20070620 06:00:00 EST", dtmFormatter.format(new DTM(2007,6,20, 11,0,0).withTimeZone(TZ.EST)) );
//        assertEquals( "20070620 16:00:00 CEST", dtmFormatter.format(new DTM(2007,6,20, 14,0,0).withTimeZone(TZ.CET)) );
//        assertEquals( "20071120 15:00:00 CET", dtmFormatter.format(new DTM(2007,11,20, 14,0,0).withTimeZone(TZ.CET)) );
//    }

}
