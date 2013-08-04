package com.mosaic.parsers;

import com.mosaic.io.IOUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 *
 */
public abstract class BasePushParser implements PushParser {

    private final String charset;


    protected BasePushParser( String charset ) {
        this.charset = charset;
    }

    public void push( String in ) {
        ByteBuffer buf = ByteBuffer.wrap(IOUtils.toByteArray(in, charset));

        push(buf);
    }


}
