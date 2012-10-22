package com.mosaic.lang.time;

import com.mosaic.lang.Validate;
import com.mosaic.lang.math.Orderable;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 */
public class DTM extends Orderable<DTM> {
    private static final long serialVersionUID = 1289422175102L;

    public static final DTMFormatter DEFAULT_FORMATTER = new DTMFormatter( "yyyy-MM-dd HH:mm:ss z" );

    private static final TZ UTC = getTimeZone("UTC");

    public static TZ getTimeZone( String zoneId ) {
        return TZ.getTimeZone( zoneId );
    }


    private Calendar calendar;



    public DTM( Date d ) {
        Calendar c = GregorianCalendar.getInstance();

        c.setTime( d );
        c.setTimeZone(UTC.toJDKTimeZone());
        c.get( Calendar.HOUR_OF_DAY ); // called for its side effect

        this.calendar = c;

        c.get( Calendar.HOUR_OF_DAY );
    }

    public DTM(Calendar c) {
        this.calendar = c;

        c.get( Calendar.HOUR_OF_DAY );
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
        Validate.inclusiveBetween( 1, month,   12,  "month"   );
        Validate.inclusiveBetween( 1, day,     31,  "day"     );
        Validate.inclusiveBetween( 0, hour,    23,  "hour"    );
        Validate.inclusiveBetween( 0, minute,  59,  "minutes" );
        Validate.inclusiveBetween( 0, seconds, 59,  "seconds" );
        Validate.inclusiveBetween( 0, millis,  999, "millis"  );
        
        Calendar c = GregorianCalendar.getInstance();

        c.setTimeZone( tz.toJDKTimeZone() );

        c.set( Calendar.YEAR,         year );
        c.set( Calendar.MONTH,        month-1 );
        c.set( Calendar.DAY_OF_MONTH, day );

        c.set( Calendar.HOUR_OF_DAY,  hour );
        c.set( Calendar.MINUTE,       minute );
        c.set( Calendar.SECOND,       seconds );

        c.set( Calendar.MILLISECOND,  millis );


        // The following call to Calendar.get is called for its side effect. It causes
        // the calendar object to calculate its internal data structures from the data provided
        // above. Failure to make this call will cause problems if you try to change the timezone
        // without having yet read a single value from the calendar. See Calendar.calculate for more
        // details.
        c.get( Calendar.HOUR_OF_DAY );
        
        this.calendar = c;
    }

    public DTM(long millis) {
        Calendar c = GregorianCalendar.getInstance();

        c.setTimeInMillis( millis );
        c.get( Calendar.HOUR_OF_DAY );
        
        this.calendar = c;
    }

    public DTM(int year, int month, int day) {
        this(year,month,day, 0,0,0,0 );
    }

    public int getYear() {
        return calendar.get( Calendar.YEAR );
    }

    public int getMonth() {
        return calendar.get( Calendar.MONTH ) + 1;
    }

    public int getDayOfMonth() {
        return calendar.get( Calendar.DAY_OF_MONTH );
    }

    public Day getDate() {
        return new Day( this );
    }

    public DayOfWeek getDayOfWeek() {
        int v = calendar.get(Calendar.DAY_OF_WEEK);
        switch (v) {
            case Calendar.MONDAY:
                return DayOfWeek.MONDAY;
            case Calendar.TUESDAY:
                return DayOfWeek.TUESDAY;
            case Calendar.WEDNESDAY:
                return DayOfWeek.WEDNESDAY;
            case Calendar.THURSDAY:
                return DayOfWeek.THURSDAY;
            case Calendar.FRIDAY:
                return DayOfWeek.FRIDAY;
            case Calendar.SATURDAY:
                return DayOfWeek.SATURDAY;
            case Calendar.SUNDAY:
                return DayOfWeek.SUNDAY;
        }


        throw new IllegalArgumentException( v + " is an invalid day of the week" );
    }

    public int getHour() {
        return calendar.get( Calendar.HOUR_OF_DAY );
    }

    public int getMinutes() {
        return calendar.get( Calendar.MINUTE );
    }

    public int getSeconds() {
        return calendar.get( Calendar.SECOND );
    }

    public int getMillis() {
        return calendar.get( Calendar.MILLISECOND );
    }

    public DTM plusDays( int numDays ) {
        Calendar c = (Calendar) calendar.clone();

        c.add( Calendar.DAY_OF_YEAR, numDays );

        return new DTM( c );
    }

//    public DTM plus( Duration d ) {
//        return plusMillis( (int) d.getMillis() );
//    }

    public DTM plusMillis( int millis ) {
        Calendar c = (Calendar) calendar.clone();

        c.add( Calendar.MILLISECOND, millis );

        return new DTM( c );
    }

    public DTM plusMinutes( int numMinutes ) {
        Calendar c = (Calendar) calendar.clone();

        c.add( Calendar.MINUTE, numMinutes );

        return new DTM( c );
    }

    public DTM withTime( int hours, int minutes, int seconds, TZ tz ) {
        Calendar c = (Calendar) calendar.clone();

        c.setTimeZone( tz.toJDKTimeZone() );

        c.set( Calendar.HOUR_OF_DAY,  hours );
        c.set( Calendar.MINUTE,       minutes );
        c.set( Calendar.SECOND,       seconds );

        c.set( Calendar.MILLISECOND,  0 );

        return new DTM( c );
    }

    public DTM withTimeZone(TZ tz) {
        if ( this.getTimeZone().equals(tz) ) {
            return this;
        }
        
        Calendar c = (Calendar) calendar.clone();

        c.setTimeZone( tz.toJDKTimeZone() );

        return new DTM( c );
    }

    public Date asJDKDate() {
        return calendar.getTime();
    }

    @Override
    public int compareTo(DTM o) {
        long a = this.calendar.getTimeInMillis();
        long b = o.calendar.getTimeInMillis();

        if ( a < b ) {
            return -1;
        }

        return a > b ? 1 : 0;
    }

    public TZ getTimeZone() {
        return new TZ(calendar.getTimeZone());
    }

    public boolean equals(Object o) {
        if ( this == o ) { return true; }
        if ( !(o instanceof DTM) ) { return false; }

        DTM other = (DTM) o;
        return this.calendar.equals( other.calendar );
    }

    public String toString() {
        return DEFAULT_FORMATTER.format(this);
    }

    public int hashCode() {
        return 17 + this.getHour() + getMinutes() + getSeconds();
    }
}
