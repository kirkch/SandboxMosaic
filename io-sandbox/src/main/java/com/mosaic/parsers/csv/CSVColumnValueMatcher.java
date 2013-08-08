package com.mosaic.parsers.csv;


import com.mosaic.parsers.BaseMatcher;
import com.mosaic.parsers.MatchResult;

import java.nio.CharBuffer;

import static com.mosaic.io.CharBufferUtils.matchUptoOneOfOrEOS;
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
        int consumedRHS = matchUptoOneOfOrEOS(buf, columnSeparators, trimmedLHS, limit, isEOS);

        if ( consumedRHS < 0 ) {
            return INCOMPLETE;
        }

        int trimmedRHS      = trimRight(buf, trimmedLHS, consumedRHS);
        int numCharsMatched = consumedRHS - consumedLHS;

        result.parsedValue = buf.subSequence(trimmedLHS-consumedLHS, trimmedRHS-consumedLHS).toString();

        buf.position( consumedRHS );

        return numCharsMatched;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private int matchQuotedValue( CharBuffer buf, MatchResult result, boolean isEOS, int limit, int consumedLHS, int quoteLHS ) {
        String quotedText = consumeUpToClosingQuoteAndAdjustBuffer(buf, quoteLHS + 1, limit);
        if ( quotedText == null ) {
            if ( isEOS ) {
                result.reportError( quoteLHS, "expected csv column value to be closed by a quote" );
                return NO_MATCH;
            }

            return INCOMPLETE;
        }

        result.parsedValue = quotedText;

        int numCharsMatched = buf.position() - consumedLHS;
        return numCharsMatched;
    }

    private String consumeUpToClosingQuoteAndAdjustBuffer(CharBuffer buf, int fromInc, int limit) {
        boolean escapedQuoteDetected = false;

        for ( int i=fromInc; i<limit; i++ ) {
            char c = buf.get(i);
            if ( c == '\"' ) {
                if ( i+1 < limit && buf.get(i+1) == '\"') {
                    i = i+2;
                    escapedQuoteDetected = true;
                } else {
                    String v = buf.subSequence( fromInc-buf.position(), i-buf.position() ).toString();
                    if ( escapedQuoteDetected ) {
                        v = v.replaceAll("\"\"", "\"");
                    }

                    buf.position(i+1);

                    return v;
                }
            }
        }

        return null;
    }

}

