package com.mosaic.collections.queue.journal;

/**
 *
 */
public class JournalNotFoundException extends RuntimeException {
    public JournalNotFoundException( String journalName ) {
        super( journalName );
    }
}
