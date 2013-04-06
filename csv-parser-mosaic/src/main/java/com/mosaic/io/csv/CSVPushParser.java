package com.mosaic.io.csv;

import com.mosaic.io.CharacterStream;
import com.mosaic.io.Characters;
import com.mosaic.lang.Validate;
import com.mosaic.lang.function.VoidFunction2;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

import java.util.List;

import static com.mosaic.parsers.push.matchers.Matchers.*;

/**
 *
 */
public class CSVPushParser {

    private final Matcher<String> csvColumn = skipSpaceOrTab(new CSVColumnValueMatcher()).withName("csvColumn");
    private final Matcher<String> comma     = skipSpaceOrTab(constant(","));


    private final Matcher<List<String>> row  = listDemarcated( alwaysMatches(), csvColumn, comma, eol() ).withName("csvRow");
    private final Matcher               rows = zeroOrMore( issueCallbackWithLineNumberAndSkip( row, new VoidFunction2<Integer, List<String>>() {
        @Override
        public void invoke( Integer lineNumber, List<String> row ) {
            parsedRowCount++;

            if ( parsedRowCount == 1 ) {
                delegate.headerRead( lineNumber, row );
            } else {
                delegate.headerRead( lineNumber, row );
            }
        }
    } ) );


    private CharacterStream       inputStream;
    private CSVPushParserDelegate delegate;

    private int parsedRowCount;

    public CSVPushParser( CSVPushParserDelegate delegate ) {
        Validate.notNull( delegate, "delegate" );

        this.delegate = delegate;
    }

    public int appendCharacters( Characters newCharacters ) {
        if ( inputStream == null ) {
            inputStream = new CharacterStream();

            rows.withInputStream( inputStream );

            delegate.parsingStarted();
        }

        int beforeRowCount = parsedRowCount;

        inputStream.appendCharacters( newCharacters );

        MatchResult r = rows.processInput();

        int afterRowCount = parsedRowCount;

        return afterRowCount - beforeRowCount;
    }

}
