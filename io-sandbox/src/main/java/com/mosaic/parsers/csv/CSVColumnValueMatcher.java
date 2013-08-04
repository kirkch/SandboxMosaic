package com.mosaic.parsers.csv;


import com.mosaic.io.CharBufferUtils;
import com.mosaic.parsers.BaseMatcher;
import com.mosaic.parsers.MatchResult;

import java.nio.CharBuffer;


/**
 *
 */
public class CSVColumnValueMatcher extends BaseMatcher {

    private char[] columnSeparators;

    public CSVColumnValueMatcher( char separator ) {
        this.columnSeparators = new char[] {separator,'\n','\r'};      
    }

    public int match( CharBuffer buf, MatchResult result, boolean isEOS ) {
        int limit = buf.limit();
        int start = buf.position();

        int pos = skipWhitespace( buf, start, limit );

        for ( int i=pos; i<limit; i++ ) {
            if ( isSeparatorAt(i, buf) ) {
                int endExc = trimRight(buf, pos, i);
                int len    = endExc - start;

                result.parsedValue = buf.subSequence(pos-start, len).toString();

                buf.position( i );

                return i - start;
            }
        }

        if ( isEOS ) {
            int endExc = trimRight(buf, pos, limit);
            int len = endExc - start;

            result.parsedValue = buf.subSequence(pos-start, len).toString();
            buf.position( limit );

            return limit - start;
        }

        return INCOMPLETE;
    }

    private int trimRight( CharBuffer buf, int from, int toExc ) {
        for ( int i=toExc-1; i>= from; i-- ) {
            char c = buf.get(i);

            if ( c != ' ' && c != '\t' ) {
                return i+1;
            }
        }

        return toExc;
    }

    private int skipWhitespace( CharBuffer buf, int startInc, int limitExc ) {
        for ( int i=startInc; i<limitExc; i++ ) {
            if ( !isWhitespaceAt(i, buf) ) {
                return i;
            }
        }

        return limitExc;
    }

    private boolean isWhitespaceAt( int pos, CharBuffer buf ) {
        char c = buf.get(pos);

        return c == ' ' || c == '\t';
    }

    private boolean isSeparatorAt( int pos, CharBuffer buf ) {
        return CharBufferUtils.isOneOf(pos, buf, columnSeparators);
    }

}

