package com.mosaic.lang.text;

/**
 *
 */
public class ParseException extends RuntimeException {

    private long offset;

    public ParseException( String fileName, String message, long offset ) {
        super( fileName + ": " + message );

        this.offset = offset;
    }

    public ParseException( String fileName, Throwable ex, long offset ) {
        super( fileName + ": " + ex.getMessage(), ex );

        this.offset = offset;
    }

    public long getOffset() {
        return offset;
    }

}
