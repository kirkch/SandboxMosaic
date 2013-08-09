package com.mosaic.parsers.csv;


import com.mosaic.parsers.BaseMatcher;
import com.mosaic.parsers.MatchResult;

import java.nio.CharBuffer;
import java.util.regex.Pattern;

import static com.mosaic.io.CharBufferUtils.skipWhitespace;
import static com.mosaic.io.CharBufferUtils.trimRight;


/**
 *
 */
public class CSVColumnValueMatcher extends BaseMatcher {

    private char separator;

    public CSVColumnValueMatcher( char separator ) {
        this.separator = separator;
    }

    public MatchResult match( CharBuffer buf, boolean isEOS ) {
        int limit       = buf.limit();
        int consumedLHS = buf.position();

        int trimmedLHS  = skipWhitespace(buf, consumedLHS, limit);

        if ( trimmedLHS != limit ) {
            char nextChar = buf.get(trimmedLHS);

            if ( nextChar == '\"' ) {
                return matchQuotedValue(buf, isEOS, limit, consumedLHS, trimmedLHS);
            }
        }

        return matchUnquotedValue(buf, isEOS, limit, consumedLHS, trimmedLHS);
    }

    private MatchResult matchUnquotedValue( CharBuffer buf, boolean isEOS, int limit, int consumedLHS, int trimmedLHS ) {
        int consumedRHS = matchUpToSeparator( buf, trimmedLHS, limit, isEOS );

        if ( consumedRHS < 0 ) {
            return MatchResult.incompleteMatch();
        }

        int trimmedRHS      = trimRight(buf, trimmedLHS, consumedRHS);
        int numCharsMatched = consumedRHS - consumedLHS;

        String parsedValue = buf.subSequence( trimmedLHS-consumedLHS, trimmedRHS-consumedLHS ).toString();

        buf.position( consumedRHS );

        return MatchResult.matched(numCharsMatched, parsedValue);
    }

    /**
     * NB performs the same job as CBU.matchUptoOneOfOrEOS(buf, columnSeparators, trimmedLHS, limit, isEOS);
     * except less generically.. however it shaved 30% off of the processing time thus we have this version here.
     */
    private int matchUpToSeparator(CharBuffer buf, int fromInc, int toExc, boolean isEOS) {
        for ( int i=fromInc; i<toExc; i++ ) {
            char c = buf.get(i);

            if ( c == separator || c == '\r' || c == '\n' ) {
                return i;
            }
        }

        return isEOS ? toExc : -1;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private MatchResult matchQuotedValue( CharBuffer buf, boolean isEOS, int limit, int consumedLHS, int quoteLHS ) {
        String quotedText = consumeUpToClosingQuoteAndAdjustBuffer(buf, quoteLHS + 1, limit);
        if ( quotedText == null ) {
            if ( isEOS ) {
                return MatchResult.errored(quoteLHS, "expected csv column value to be closed by a quote");
            }

            return MatchResult.incompleteMatch();
        }

        int numCharsMatched = buf.position() - consumedLHS;
        return MatchResult.matched(numCharsMatched, quotedText);
    }

    private static Pattern ESCAPED_QUOTE_REGEXP = Pattern.compile("\"\"");

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
                        v = ESCAPED_QUOTE_REGEXP.matcher(v).replaceAll("\"");
                    }

                    buf.position(i+1);

                    return v;
                }
            }
        }

        return null;
    }

}

