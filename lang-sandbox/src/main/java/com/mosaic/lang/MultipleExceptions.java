package com.mosaic.lang;

import com.mosaic.utils.ListUtils;
import com.mosaic.utils.StringUtils;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Groups multiple exceptions together as a single exception.
 */
public class MultipleExceptions extends RuntimeException {

    private List<Throwable> exceptions = new ArrayList<>();


    public MultipleExceptions() {}

    public MultipleExceptions( List<Throwable> ex ) {
        this.exceptions.addAll( ex );
    }


    public void append( Throwable ex ) {
        exceptions.add(ex);
    }


    public String getMessage() {
        return StringUtils.concat( ListUtils.map( exceptions, Throwable::getMessage ), "[", ",", "]" );
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace( PrintStream s ) {
        s.append( "[Start of grouped exceptions "+System.identityHashCode(this)+"]" );

        for ( Throwable ex : exceptions ) {
            ex.printStackTrace(s);
        }

        s.append( "[End of grouped exceptions "+System.identityHashCode(this)+"]" );
    }

    public void printStackTrace( PrintWriter s ) {
        s.append( "[Start of grouped exceptions "+System.identityHashCode(this)+"]" );

        for ( Throwable ex : exceptions ) {
            ex.printStackTrace(s);
        }

        s.append( "[End of grouped exceptions " + System.identityHashCode( this ) + "]" );
    }

    public List<Throwable> getUnderlyingExceptions() {
        return exceptions;
    }

    public void throwIfNotEmpty() {
        if ( exceptions.size() > 0 ) {
            throw this;
        }
    }
}
