package com.mosaic.lang.time;

import com.mosaic.lang.ThreadSafe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
@ThreadSafe
public class UTCFormatter {
    private SimpleDateFormat format;


    public UTCFormatter(String pattern) {
        this( pattern, TZ.UTC );
    }

    public UTCFormatter(String pattern, TZ defaultTZ) {
        this.format = new SimpleDateFormat(pattern);

        this.format.setTimeZone( defaultTZ.toJDKTimeZone() );
    }


    public String format( DTM dtm ) {
        Date date = dtm.asJDKDate();

        synchronized (format) {
            return format.format(date);
        }
    }


    /**
     * Parses the specified string representation of a date and time.
     *
     * @return returns a dtm always specified in UTC even when the string was in another timezone 
     */
    public DTM parseDTM( String s ) throws ParseException {
        Date d;

        synchronized (format) {
            d = format.parse(s);
        }

        return new DTM(d.getTime());
    }

}
