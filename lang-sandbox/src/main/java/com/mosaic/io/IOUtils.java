package com.mosaic.io;

import com.mosaic.lang.functional.TryNow;

import java.io.Closeable;
import java.io.Flushable;


/**
 * Set of utilities for working with java.io classes.
 */
public class IOUtils {
    public static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    public static void flush( Appendable o ) {
        if ( o instanceof Flushable ) {
            TryNow.tryNow( () -> {
                ((Flushable) o).flush();

                return null;
            } );
        }
    }

    public static void close( Appendable o ) {
        if ( o instanceof Closeable ) {
            TryNow.tryNow( () -> {
                ((Flushable) o).flush();

                return null;
            } );
        }
    }

}
