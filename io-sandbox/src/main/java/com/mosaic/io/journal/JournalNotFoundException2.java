package com.mosaic.io.journal;

/**
 *
 */
public class JournalNotFoundException2 extends RuntimeException {
    public JournalNotFoundException2( String journalName ) {
        super( journalName );
    }
}
