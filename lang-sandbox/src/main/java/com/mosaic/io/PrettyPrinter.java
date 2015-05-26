package com.mosaic.io;

import java.io.IOException;
import java.io.StringWriter;

import static com.mosaic.lang.functional.TryNow.tryNow;


/**
 * Interface for objects that know how to pretty print an object into a textual destination.
 */
public interface PrettyPrinter<T> {

    public void write( Appendable buf, T v ) throws IOException;


    public default String toText( T v ) {
        StringWriter    buf     = new StringWriter();
        IndentingWriter out     = new IndentingWriter( buf );

        return tryNow( () -> {
            this.write( out, v );

            return buf.toString();
        } ).getResultNoBlock();
    }

}
