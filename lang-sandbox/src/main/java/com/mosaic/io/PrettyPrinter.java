package com.mosaic.io;

import com.mosaic.lang.system.SystemX;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static com.mosaic.lang.functional.TryNow.tryNow;


/**
 * Interface for objects that know how to pretty print an object into a textual destination.
 */
public interface PrettyPrinter<T> {

    public void write( Appendable buf, T v ) throws IOException;


    public default List<String> toText( T v ) {
        StringWriter    buf     = new StringWriter();
        IndentingWriter out     = new IndentingWriter( buf );;

        return tryNow( () -> {
            this.write( out, v );

            return Arrays.asList( buf.toString().split( SystemX.NEWLINE ) );
        } ).getResultNoBlock();
    }

}
