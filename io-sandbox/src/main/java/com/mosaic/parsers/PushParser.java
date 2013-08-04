package com.mosaic.parsers;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;

/**
 * An incremental parser.  Capable of processing input as it arrives without
 * holding on to memory.  Very useful for handling large volumes of data inbound
 * on an asynchronous network link.
 */
public interface PushParser {

    /**
     * Notifies the parser that a new file is about to start.
     */
    public void pushSOF();

    /**
     * Notifies the parser that the file has come to an end.
     */
    public void pushEOF();

    /**
     * Reads the characters from the buffer.  Parses as many of them as it can,
     * and then slides the characters (if any) that it could not parse to
     * the front of the buffer ready for the buffer to be appended to again.<p/>
     *
     * The data that has been parsed results in callbacks to process them.
     */
    public void push( ByteBuffer buf );






//    public void push( InputStream in ) throws IOException;

//    public void push( ReadableByteChannel in ) throws IOException;

    /**
     * Push the entire body of text to be parsed in one go.  Mostly a convenience
     * method.
     */
    public void push( String in );

}
