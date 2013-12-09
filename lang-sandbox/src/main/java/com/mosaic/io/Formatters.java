package com.mosaic.io;

import java.io.IOException;

/**
 *
 */
public class Formatters {

    public static final Formatter TO_STRING = new Formatter() {
        public void write( Appendable buf, Object v ) throws IOException {
            if ( v == null ) {
                buf.append( "null" );
            } else {
                buf.append( v.toString() );
            }
        }
    };

}
