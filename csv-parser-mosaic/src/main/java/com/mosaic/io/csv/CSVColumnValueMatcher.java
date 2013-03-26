package com.mosaic.io.csv;

import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

/**
 *
 */
public class CSVColumnValueMatcher extends Matcher<String> {

    @Override
    protected MatchResult<String> _processInput() {
        int streamLength = inputStream.length();
        if ( streamLength == 0 ) {
            return createIncompleteMatch();
        }

        if ( inputStream.charAt(0) == '"' ) {
            return processQuotedColumn( streamLength );
        } else {
            return processUnquotedColumn( streamLength );
        }
    }

    private MatchResult<String> processUnquotedColumn( final int streamLength ) {
        for ( int i=0; i<streamLength; i++ ) {
            char c = inputStream.charAt( i );

            if ( c == ',' || c == '\n' || c == '\r' ) {
                String str = inputStream.consumeCharacters( i ).toString();

                return createHasResultStatus( str );
            }
        }

        if ( inputStream.hasReceivedEOS() ) {
            return createHasResultStatus( inputStream.consumeCharacters(streamLength).toString() );
        }

        return createIncompleteMatch();
    }

    private MatchResult<String> processQuotedColumn( final int streamLength ) {
        for ( int i=1; i<streamLength; i++ ) {
            char c = inputStream.charAt( i );

            if ( c == '"' ) {
                inputStream.skipCharacters( 1 );

                String str = inputStream.consumeCharacters(i-1).toString(); // extracts up to but excluding the quote
                inputStream.skipCharacters( 1 ); // the quote

                return createHasResultStatus( str );
            }
        }

        if ( inputStream.hasReceivedEOS() ) {
            return createHasResultStatus( inputStream.consumeCharacters(streamLength).toString() );
        }

        return createIncompleteMatch();
    }

}
