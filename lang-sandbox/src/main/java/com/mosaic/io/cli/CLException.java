package com.mosaic.io.cli;

/**
 *
 */
public class CLException extends RuntimeException {
    public CLException( String message ) {
        super( message );
    }

    public CLException( String message, Throwable cause ) {
        super( message, cause );
    }
}
