package com.mosaic.io;

import java.io.IOException;

/**
 *
 */
public class RuntimeIOException extends RuntimeException {
    public static RuntimeException recast( IOException e ) {
//        if ( e instanceof UnknownHostException ) {
//            throw new UnknownHostRuntimeException(e);
//        }
        
        return new RuntimeIOException( e );
    }

    public RuntimeIOException() {
        super();
    }

    public RuntimeIOException(String message) {
        super(message);
    }

    public RuntimeIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeIOException(Throwable cause) {
        super(cause);
    }
}
