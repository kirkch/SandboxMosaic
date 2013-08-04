package com.mosaic.parsers.csv;

import com.mosaic.parsers.BasePushParser;

import java.nio.ByteBuffer;

/**
 *
 */
public class CSVPushParser extends BasePushParser {
    private CSVParserCallback delegate;


    // csvColumn       = new CSVColumnValueMatcher().withName("csvColumn")
    // columnSeparator = constant(",")
    // comment         = new CommentMatcher("#")

    // csvRow          = list(csvColumn, columnSeparator).withCallback("rowReceived")


    // skip = or(whitespace, comment)


    public CSVPushParser( String charset, CSVParserCallback delegate ) {
        super(charset);

        this.delegate = delegate;
    }


    public void pushSOF() {
        delegate.start();
    }

    public void pushEOF() {
        delegate.end();
    }

    public void push( ByteBuffer buf ) {
    }

}
