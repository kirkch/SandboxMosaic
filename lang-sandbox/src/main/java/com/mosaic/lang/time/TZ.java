package com.mosaic.lang.time;

import com.mosaic.lang.Immutable;

import java.io.Serializable;
import java.util.TimeZone;

/**
 *
 */
public class TZ implements Serializable, Immutable {

    private static final long serialVersionUID = 1289461083540L;

    public static final TZ UTC    = getTimeZone("UTC");
    public static final TZ LONDON = getTimeZone("Europe/London");
    public static final TZ EST    = getTimeZone("EST");
    public static final TZ CET    = getTimeZone("CET");

    public static TZ getLocalMachineTimeZone() {
        return new TZ(TimeZone.getDefault());
    }

    public static TZ getTimeZone( String zoneId ) {
        return new TZ(zoneId);
    }


    private java.util.TimeZone tz;


    public TZ(String zoneId) {
        this( java.util.TimeZone.getTimeZone(zoneId) );
    }

    public TZ(java.util.TimeZone timeZone) {
        this.tz = timeZone;
    }

    java.util.TimeZone toJDKTimeZone() {
        return tz;
    }

    public boolean equals(Object o) {
        if ( this == o ) { return true; }
        if ( !(o instanceof TZ) ) { return false; }

        TZ other = (TZ) o;
        return this.tz.hasSameRules(other.tz);
    }

    public String toString() {
        return tz.getID();
    }

    public int hashCode() {
        return tz.hashCode();
    }

}
