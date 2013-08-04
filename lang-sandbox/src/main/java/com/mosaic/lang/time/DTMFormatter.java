package com.mosaic.lang.time;

import com.mosaic.lang.ThreadSafe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 */
@ThreadSafe
public class DTMFormatter {
    private SimpleDateFormat format;
    private TZ               targetTZ;

    public DTMFormatter( String pattern ) {
        this( pattern, TZ.UTC );
    }

    public DTMFormatter( String pattern, TZ defaultTZ ) {
        this.format = new SimpleDateFormat(pattern);

        this.targetTZ = defaultTZ;
        this.format.setTimeZone( defaultTZ.toJDKTimeZone() );
    }

    public String format( DTM dtm ) {
        format.setTimeZone( dtm.getTimeZone().toJDKTimeZone() );

        Date date = dtm.asJDKDate();

        synchronized (this) {
            return format.format(date);
        }
    }

    public String format( Day day ) {
        Date date = day.asJDKDate();

        synchronized (this) {
            return format.format(date);
        }
    }

    /**
     * Parses the specified string representation of a date and time.
     *
     * @return returns a dtm always specified in UTC even when the string was in another timezone 
     */
    public synchronized  DTM parseDTM( String s ) throws ParseException {
        Calendar c = format.getCalendar();
        c.set( Calendar.YEAR,         0 );
        c.set( Calendar.MONTH,        0 );
        c.set( Calendar.DAY_OF_MONTH, 0 );

        c.set( Calendar.HOUR_OF_DAY,  0 );
        c.set( Calendar.MINUTE,       0 );
        c.set( Calendar.SECOND,       0 );

        c.set( Calendar.MILLISECOND,  0 );

        // NB I have not been able to figure out how to find out what time zone the string
        // was originally in. However the JDK parser does know as it converts to the following
        // target timezone just fine.
//            c.setTimeZone(TZ.UTC.toJDKTimeZone());


        Date d = format.parse(s);

        return new DTM(d).withTimeZone( targetTZ );
    }

    public synchronized Day parseDay(String s) throws ParseException {
        Date d = format.parse(s);

        return new Day(d);
    }
}
