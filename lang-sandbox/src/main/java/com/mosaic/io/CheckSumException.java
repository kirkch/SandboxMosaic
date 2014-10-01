package com.mosaic.io;


/**
 *
 */
public class CheckSumException extends RuntimeIOException {
    public CheckSumException() {
        super();
    }

    public CheckSumException( String message ) {
        super( message );
    }

    public CheckSumException( String message, Throwable cause ) {
        super( message, cause );
    }

    public CheckSumException( Throwable cause ) {
        super( cause );
    }
}
