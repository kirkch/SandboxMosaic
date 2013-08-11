package com.mosaic.parsers.csv;

import com.mosaic.parsers.BasePushParser;
import com.mosaic.parsers.Matcher;
import com.mosaic.parsers.matchers.WhitespaceMatcher;

import java.nio.CharBuffer;
import java.util.List;

import static com.mosaic.parsers.matchers.ConsumeUpToMatcher.consumeUpToNewLineMatcher;
import static com.mosaic.parsers.matchers.SeparatedListMatcher.commaSeparatedValues;


/**
 *
 */
public class CSVPushParser extends BasePushParser {
    private CSVParserCallback csvParserCallback;


    private Matcher csvColumn = new CSVColumnValueMatcher(',').withName("csvColumn");
    private Matcher csvRow    = commaSeparatedValues(csvColumn).withCallback("rowReceived");

    private int rowCount;

    // comment         = new CommentMatcher("#")

    // skip = or(whitespace, comment)


    public CSVPushParser( CSVParserCallback csvParserCallback ) {
        setInitialMatcher( csvRow );
        setSkipMatcher( WhitespaceMatcher.tabOrSpaceMatcher() );
        setErrorRecoverMatcher( consumeUpToNewLineMatcher() );

        this.csvParserCallback = csvParserCallback;
    }



//    public long push( CharBuffer buf, boolean isEOS ) {
//        return 0;
//    }


    @Override
    protected void parserStartedEvent() {
        super.parserStartedEvent();

        csvParserCallback.start();
    }

    @Override
    protected void parserFinishedEvent() {
        super.parserFinishedEvent();

        csvParserCallback.end();
    }

    @SuppressWarnings("UnusedDeclaration") // callback method; uses reflection
    private void rowReceived( List<String> row ) {
        rowCount++;

        if ( rowCount == 1 ) {
            csvParserCallback.headers(rowCount, row);
        } else {
            csvParserCallback.row(rowCount, row);
        }
    }

}
