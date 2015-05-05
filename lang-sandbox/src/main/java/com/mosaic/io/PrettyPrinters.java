package com.mosaic.io;

import java.io.IOException;

/**
 *
 */
public class PrettyPrinters {

    public static final PrettyPrinter TO_STRING = new PrettyPrinter() {
        public void write( Appendable buf, Object v ) throws IOException {
            if ( v == null ) {
                buf.append( "null" );
            } else {
                buf.append( v.toString() );
            }
        }
    };

}
