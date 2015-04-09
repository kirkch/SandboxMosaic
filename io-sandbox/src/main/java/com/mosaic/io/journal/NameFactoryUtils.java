package com.mosaic.io.journal;

import com.mosaic.lang.ThreadSafe;
import com.mosaic.lang.functional.Function0;

import java.util.concurrent.atomic.AtomicLong;


/**
 * Utils for creating standardised names.
 */
public class NameFactoryUtils {

    /**
     * Create names that are differentiated by an incrementing number (postfix).  The names
     * are unique within this OS process.  The sequence starts from 1.
     */
    @ThreadSafe
    public static Function0<String> createSequenceNameFactory( String baseName ) {
        return createSequenceNameFactory( baseName, 1 );
    }

    /**
     * Create names that are differentiated by an incrementing number (postfix).  The names
     * are unique within this OS process.
     */
    @ThreadSafe
    public static Function0<String> createSequenceNameFactory( String baseName, long firstSeq ) {
        AtomicLong nextSeq = new AtomicLong( firstSeq );

        return () -> baseName + nextSeq.getAndIncrement();
    }

}
