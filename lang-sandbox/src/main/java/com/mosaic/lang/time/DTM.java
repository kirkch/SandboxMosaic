package com.mosaic.lang.time;

import com.mosaic.lang.QA;
import com.mosaic.lang.QA;
import com.mosaic.lang.math.Orderable;
import com.mosaic.utils.ComparatorUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 */
public class DTM extends Orderable<DTM> {
    private static final long serialVersionUID = 1289422175102L;

    public static final UTCFormatter DEFAULT_FORMATTER = new UTCFormatter( "yyyy-MM-dd HH:mm:ss z" );

    static final TZ UTC = getTimeZone("UTC");

    public static TZ getTimeZone( String zoneId ) {
        return TZ.getTimeZone( zoneId );
    }



    private long millis;

    public DTM(long millis) {
        this.millis = millis;
    }

    public DTM(int year, int month, int day) {
        this(year,month,day, 0,0,0,0 );
    }

    public DTM(int year, int month, int day, int hour, int minute, int seconds) {
        this(year,month,day, hour,minute,seconds,0 );
    }

    public DTM(int year, int month, int day, int hour, int minute, int seconds, TZ tz) {
        this(year,month,day, hour,minute,seconds,0, tz );
    }

    public DTM(int year, int month, int day, int hour, int minute, int seconds, int millis) {
        this(year,month,day, hour,minute,seconds,millis, UTC );
    }

    public DTM(int year, int month, int day, int hour, int minute, int seconds, int millis, TZ tz) {
        QA.argInclusiveBetween( 1, month, 12, "month" );
        QA.argInclusiveBetween( 1, day, 31, "day" );
        QA.argInclusiveBetween( 0, hour, 23, "hour" );
        QA.argInclusiveBetween( 0, minute, 59, "minutes" );
        QA.argInclusiveBetween( 0, seconds, 59, "seconds" );
        QA.argInclusiveBetween( 0, millis, 999, "millis" );

        Calendar c = GregorianCalendar.getInstance();

        c.setTimeZone( tz.toJDKTimeZone() );

        c.set( Calendar.YEAR,         year );
        c.set( Calendar.MONTH,        month-1 );
        c.set( Calendar.DAY_OF_MONTH, day );

        c.set( Calendar.HOUR_OF_DAY,  hour );
        c.set( Calendar.MINUTE,       minute );
        c.set( Calendar.SECOND,       seconds );

        c.set( Calendar.MILLISECOND,  millis );


        this.millis = c.getTimeInMillis();
    }


    public Calendar asJDKCalendar() {
        Calendar c = GregorianCalendar.getInstance();

        c.setTimeZone( UTC.toJDKTimeZone() );
        c.setTimeInMillis( millis );

        return c;
    }

    public int getYear() {
        return asJDKCalendar().get( Calendar.YEAR );
    }

    public int getMonth() {
        return asJDKCalendar().get( Calendar.MONTH ) + 1;
    }

    public int getDayOfMonth() {
        return asJDKCalendar().get( Calendar.DAY_OF_MONTH );
    }


    public int getHour() {
        return asJDKCalendar().get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes() {
        return asJDKCalendar().get(Calendar.MINUTE);
    }

    public int getSeconds() {
        return asJDKCalendar().get(Calendar.SECOND);
    }

    public int getMillis() {
        return asJDKCalendar().get(Calendar.MILLISECOND);
    }

    public long getMillisSinceEpoch() {
        return millis;
    }

    public DTM add( Duration d ) {
        return new DTM( this.millis + d.getMillis() );
    }

    /**
     * Subtract two date and times to give the length of time between the two dates.
     *
     * @return this - b
     */
    public Duration subtract( DTM b ) {
        return Duration.millis( this.millis - b.millis );
    }

//    public com.mosaic.lang.time.DTM withTime( int hours, int minutes, int seconds, TZ tz ) {
//        Calendar c = asJDKCalendar();
//
//        c.setTimeZone( tz.toJDKTimeZone() );
//
//        c.set( Calendar.HOUR_OF_DAY,  hours );
//        c.set( Calendar.MINUTE,       minutes );
//        c.set( Calendar.SECOND,       seconds );
//
//        c.set( Calendar.MILLISECOND,  0 );
//
//        return new DTM( c.getTimeInMillis() );
//    }
//
//    public com.mosaic.lang.time.DTM withTimeZone(TZ tz) {
//        if ( this.getTimeZone().equals(tz) ) {
//            return this;
//        }
//
//        Calendar c = (Calendar) calendar.clone();
//
//        c.setTimeZone( tz.toJDKTimeZone() );
//
//        return new com.mosaic.lang.time.DTM( c );
//    }

    public Date asJDKDate() {
        return new Date( millis );
    }

    @Override
    public int compareTo( DTM o ) {
        return ComparatorUtils.compare(this.millis, o.millis);
    }


    public boolean equals(Object o) {
        if ( this == o ) { return true; }
        if ( !(o instanceof DTM) ) { return false; }

        DTM other = (DTM) o;
        return this.millis == other.millis;
    }

    public String toString() {
        return DEFAULT_FORMATTER.format(this);
    }

    public int hashCode() {
        return (int) millis;
    }

}
