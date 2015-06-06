package com.mosaic.lang.compiler;

public class CompilationException extends RuntimeException {
    public CompilationException() {
        super();
    }

    public CompilationException( String message ) {
        super( message );
    }

    public CompilationException( String message, Throwable cause ) {
        super( message, cause );
    }

    public CompilationException( Throwable cause ) {
        super( cause );
    }

    protected CompilationException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
