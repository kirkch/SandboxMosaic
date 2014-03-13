package com.mosaic.lang;

/**
 *
 */
public class IllegalStateExceptionX extends IllegalStateException {

    public IllegalStateExceptionX( String msg, String...args ) {
        super( String.format(msg, (Object[]) args) );
    }

}
