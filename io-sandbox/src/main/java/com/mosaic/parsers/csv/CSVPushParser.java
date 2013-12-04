package com.mosaic.parsers.csv;

import com.mosaic.parsers.BasePushParser;
import com.mosaic.parsers.Matcher;
import com.mosaic.parsers.matchers.WhitespaceMatcher;

import java.util.List;

import static com.mosaic.parsers.matchers.ConsumeUpToMatcher.consumeUpToNewLineMatcher; // todo
import static com.mosaic.parsers.matchers.Matchers.*;


/**
 *
 */
public class CSVPushParser extends BasePushParser {
    private CSVParserCallback csvParserCallback;


    private Matcher csvColumn  = new CSVColumnValueMatcher(',');
    private Matcher csvColumns = commaSeparatedValues(csvColumn).withCallback("rowReceived");
    private Matcher csvRow     = and(csvColumns, skipWhitespace()).skip();
    private Matcher csvRows    = repeatOnceOrMore(csvRow);

    private int rowCount;

    // comment         = new CommentMatcher("#")

    // skip = or(whitespace, comment)


    public CSVPushParser( CSVParserCallback csvParserCallback ) {
        setInitialMatcher( csvRows );
        setSkipMatcher( WhitespaceMatcher.tabOrSpaceMatcher() );
        setErrorRecoverMatcher( consumeUpToNewLineMatcher() );

        this.csvParserCallback = csvParserCallback;
    }



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
