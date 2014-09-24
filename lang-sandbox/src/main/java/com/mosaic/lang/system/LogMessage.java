package com.mosaic.lang.system;

/**
 *
 */
public class LogMessage {

    private String msg;
    private long   displayCount;

    public LogMessage( String msg ) {
        this.msg = msg;
    }

    public long getDisplayCount() {
        return displayCount;
    }


    void incDisplayCount() {
        displayCount++;
    }

    String getFormattedMessage( Object...args ) {
        return String.format( msg, args );
    }

}
