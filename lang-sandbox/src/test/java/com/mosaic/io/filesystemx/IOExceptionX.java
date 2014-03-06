package com.mosaic.io.filesystemx;

/**
 *
 */
public class IOExceptionX extends RuntimeException {
    public IOExceptionX( String message ) {
        super( message );
    }

    public IOExceptionX( String message, Throwable cause ) {
        super( message, cause );
    }
}
