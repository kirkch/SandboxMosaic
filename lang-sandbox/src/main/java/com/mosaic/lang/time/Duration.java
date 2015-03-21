package com.mosaic.lang.time;


import com.mosaic.lang.math.Orderable;
import com.mosaic.utils.ComparatorUtils;


/**
 *
 */
public class Duration extends Orderable<Duration> {

    private static final long ONE_SECOND_MILLIS = 1000;
    private static final long ONE_MINUTE_MILLIS = ONE_SECOND_MILLIS * 60;
    private static final long ONE_HOUR_MILLIS   = ONE_MINUTE_MILLIS * 60;
    private static final long ONE_DAY_MILLIS    = ONE_HOUR_MILLIS   * 24;


    public static final Duration INDEFINITE = millis( Long.MAX_VALUE );


    public static Duration millis( long millis ) {
        return new Duration(millis);
    }

    public static Duration seconds( long seconds ) {
        return millis(seconds * ONE_SECOND_MILLIS);
    }

    public static Duration minutes( long minutes ) {
        return millis(minutes * ONE_MINUTE_MILLIS);
    }

    public static Duration hours( long hours ) {
        return millis(hours * ONE_HOUR_MILLIS);
    }

    public static Duration days( long days ) {
        return millis(days * ONE_DAY_MILLIS);
    }


    private long millis;

    public Duration( long millis ) {
        this.millis = millis;
    }

    public Duration add( Duration d ) {
        return new Duration( this.millis + d.millis );
    }

    public Duration subtract( Duration d ) {
        return new Duration( this.millis + d.millis );
    }

    public long getMillis() {
        return millis;
    }


    public int compareTo( Duration d ) {
        return ComparatorUtils.compareAsc(this.millis, d.millis);
    }


    public int getDays() {
        return (int) (millis / ONE_DAY_MILLIS);
    }

    public int getHours() {
        return (int) (millis / ONE_HOUR_MILLIS);
    }

    public int getMinutes() {
        return (int) (millis / ONE_MINUTE_MILLIS);
    }

    public int getSeconds() {
        return (int) (millis / ONE_SECOND_MILLIS);
    }


    public int hashCode() {
        return (int) millis;
    }

    public boolean equals( Object o ) {
        if ( !(o instanceof Duration) ) {
            return false;
        } else if ( o == this ) {
            return true;
        }

        Duration other = (Duration) o;
        return this.millis == other.millis;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        long remainder = append( buf, millis,    ONE_DAY_MILLIS, "d" );
             remainder = append( buf, remainder, ONE_HOUR_MILLIS, "h" );
             remainder = append( buf, remainder, ONE_MINUTE_MILLIS, "m" );
             remainder = append( buf, remainder, ONE_SECOND_MILLIS, "s" );
                         append( buf, remainder, 1, "ms" );

        return buf.toString();
    }

    private long append( StringBuilder buf, long v, long denominator, String postfix ) {
        long quotient = v/denominator;

        if ( quotient > 0 ) {
            if ( buf.length() != 0 ) {
                buf.append( " " );
            }

            buf.append(quotient);
            buf.append(postfix);
        }

        return v % denominator;
    }

}
