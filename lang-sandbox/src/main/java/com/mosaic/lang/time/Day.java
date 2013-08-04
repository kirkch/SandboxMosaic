package com.mosaic.lang.time;

import com.mosaic.lang.math.Orderable;

import java.util.Date;

/**
 *
 */
public class Day extends Orderable<Day> {
    private static final long serialVersionUID = 1289421478841L;

    private DTM whenDTM;

    public Day( int year, int month, int day ) {
        this( new DTM(year,month,day) );
    }
    
    public Day( String yyyyMMdd ) {
        String[] parts = yyyyMMdd.split("/");

        if ( parts.length != 3 ) {
            throw new IllegalArgumentException( "'" + yyyyMMdd + "' is malformed, expected date of form yyyy/mm/dd" );
        }

        try {
            int year  = Integer.parseInt( parts[0] );
            int month = Integer.parseInt( parts[1] );
            int day   = Integer.parseInt( parts[2] );

            this.whenDTM = new DTM( year, month, day );
        } catch ( NumberFormatException e ) {
            throw new IllegalArgumentException( "'" + yyyyMMdd + "' is malformed, expected date of form yyyy/mm/dd", e );
        }
    }

    public Day( DTM dtm ) {
        this.whenDTM = dtm;
    }

    public Day(Date d) {
        this( new DTM(d) );
    }

    public int getYear() {
        return whenDTM.getYear();
    }

    public int getMonthOfYear() {
        return whenDTM.getMonth();
    }

    public int getDayOfMonth() {
        return whenDTM.getDayOfMonth();
    }

    public Day plusDays( int numDays ) {
        return new Day( whenDTM.plusDays(numDays) );
    }


    public Date asJDKDate() {
        return whenDTM.asJDKDate();
    }

    public String toString() {
        return whenDTM.getYear() + "/" + whenDTM.getMonth() + "/" + whenDTM.getDayOfMonth();
    }

    public boolean equals( Object o ) {
        if ( o == this ) return true;
        if ( !(o instanceof Day) ) return false;

        Day other = (Day) o;
        return this.getYear() == other.getYear()
                && this.getMonthOfYear() == other.getMonthOfYear()
                && this.getDayOfMonth() == other.getDayOfMonth();
    }

    public int hashCode() {
        return whenDTM.hashCode();
    }

    public int compareTo(Day o) {
        return whenDTM.compareTo(o.whenDTM);
    }
}
