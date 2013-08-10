package com.mosaic.parsers.csv;

import com.mosaic.parsers.BasePushParser;

import java.nio.CharBuffer;


/**
 *
 */
public class CSVPushParser extends BasePushParser {
    private CSVParserCallback delegate;


    // csvColumn       = new CSVColumnValueMatcher(',').withName("csvColumn")
    // columnSeparator = constant(",")
    // comment         = new CommentMatcher("#")

    // csvRow          = listWithSeparator(csvColumn, columnSeparator).withCallback("rowReceived")


    // skip = or(whitespace, comment)


    public CSVPushParser( CSVParserCallback delegate ) {
        super( null );

        this.delegate = delegate;
    }


    public void pushStartOfFile() {
        delegate.start();
    }

    public void pushEndOfFile() {
        delegate.end();
    }

    public long push( CharBuffer buf ) {
        return 0;
    }



}
