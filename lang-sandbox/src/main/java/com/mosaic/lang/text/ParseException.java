package com.mosaic.lang.text;

/**
 *
 */
public class ParseException extends RuntimeException {

    private long offset;

    public ParseException( String message, long offset ) {
        super( message );

        this.offset = offset;
    }

    public long getOffset() {
        return offset;
    }

}
