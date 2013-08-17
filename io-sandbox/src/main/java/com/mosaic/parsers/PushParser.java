package com.mosaic.parsers;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;


/**
 * An incremental parser.  Capable of processing input as it arrives without
 * holding on to memory.  Very useful for handling large volumes of data inbound
 * on an asynchronous network link.
 */
public interface PushParser {

    /**
     * Reset to the parsers initial state ready to begin parsing a new file.
     */
    public void reset();

    /**
     * Reads the characters from the buffer. The data that has been parsed
     * results in callbacks to process them.
     *
     * @return number of characters consumed from the buffer
     */
    public long push( CharBuffer buf, boolean isEOS );

    /**
     * Reads in as many characters from reader as it can match. Returns how
     * many characters were consumed.  If the reader blocks the thread then
     * this method will block waiting for input.
     */
    public long push( Reader in ) throws IOException;

}
