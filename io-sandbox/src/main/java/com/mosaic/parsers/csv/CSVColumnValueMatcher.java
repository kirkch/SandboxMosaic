package com.mosaic.parsers.csv;


import com.mosaic.io.CharBufferUtils;
import com.mosaic.parsers.BaseMatcher;
import com.mosaic.parsers.MatchResult;

import java.nio.CharBuffer;

import static com.mosaic.io.CharBufferUtils.skipWhitespace;
import static com.mosaic.io.CharBufferUtils.trimRight;


/**
 *
 */
public class CSVColumnValueMatcher extends BaseMatcher {

    private char[] columnSeparators;

    public CSVColumnValueMatcher( char separator ) {
        this.columnSeparators = new char[] {separator,'\n','\r'};      
    }

    public int match( CharBuffer buf, MatchResult result, boolean isEOS ) {
        int limit       = buf.limit();
        int consumedLHS = buf.position();

        int trimmedLHS  = skipWhitespace(buf, consumedLHS, limit);

        if ( trimmedLHS != limit ) {
            char nextChar = buf.get(trimmedLHS);

            if ( nextChar == '\"' ) {
                return matchQuotedValue(buf, result, isEOS, limit, consumedLHS, trimmedLHS);
            }
        }

        return matchUnquotedValue(buf, result, isEOS, limit, consumedLHS, trimmedLHS);
    }

    private int matchUnquotedValue( CharBuffer buf, MatchResult result, boolean isEOS, int limit, int consumedLHS, int trimmedLHS ) {
        int consumedRHS = matchUptoSeparator( buf, trimmedLHS, limit, isEOS );

        if ( consumedRHS == INCOMPLETE ) {
            return INCOMPLETE;
        }

        int trimmedRHS      = trimRight(buf, trimmedLHS, consumedRHS);
        int numCharsMatched = consumedRHS - consumedLHS;

        result.parsedValue = buf.subSequence(trimmedLHS-consumedLHS, trimmedRHS-consumedLHS).toString();

        buf.position( consumedRHS );

        return numCharsMatched;
    }

    private int matchQuotedValue( CharBuffer buf, MatchResult result, boolean isEOS, int limit, int consumedLHS, int quoteLHS ) {
        int endQuote = matchUpToQuote( buf, quoteLHS+1, limit );
        if ( endQuote < 0 ) {
            if ( isEOS ) {
                result.reportError( quoteLHS, "expected csv column value to be closed by a quote" );
                return NO_MATCH;
            }

            return INCOMPLETE;
        }

        result.parsedValue = buf.subSequence(quoteLHS+1-consumedLHS, endQuote-consumedLHS).toString();
        buf.position( endQuote+1 );

        int numCharsMatched = endQuote+1 - consumedLHS;
        return numCharsMatched;
    }

    private int matchUpToQuote( CharBuffer buf, int fromInc, int limit ) {
        for ( int i=fromInc; i<limit; i++ ) {
            char c = buf.get(i);
            if ( c == '\"' ) {
                return i;
            }
        }

        return -1;
    }

    private int matchUptoSeparator( CharBuffer buf, int from, int limit, boolean isEOS ) {
        for ( int i=from; i<limit; i++ ) {
            if ( isSeparatorAt(i, buf) ) {
                return i;
            }
        }

        if ( isEOS ) {
            return limit;
        }

        return INCOMPLETE;
    }

    private boolean isSeparatorAt( int pos, CharBuffer buf ) {
        return CharBufferUtils.isOneOf(pos, buf, columnSeparators);
    }

}

