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
     * Notifies the parser that a new file is about to start. Start Of File.
     */
    public void pushStartOfFile();

    /**
     * Notifies the parser that the file has come to an end. End Of File.
     */
    public void pushEndOfFile();

    /**
     * Reads the characters from the buffer. The data that has been parsed
     * results in callbacks to process them.
     *
     * @return number of characters consumed from the buffer
     */
    public long push( CharBuffer buf );

    /**
     * Reads in as many characters from reader as it can match. Returns how
     * many characters were consumed.  If the reader blocks the thread then
     * this method will block waiting for input.
     */
    public long push( Reader in ) throws IOException;

    /**
     * Push the entire body of text to be parsed in one go.  Mostly a convenience
     * method.
     *
     * @return number of characters consumed
     */
    public long push( String in );

}
