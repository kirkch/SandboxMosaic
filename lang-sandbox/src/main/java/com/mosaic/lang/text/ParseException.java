package com.mosaic.lang.text;

import com.mosaic.bytes.Bytes;


/**
 *
 */
public class ParseException extends RuntimeException {

    public static ParseException newParseException( Bytes b, long position, String message ) {
        return newParseException( b, position, message, null );
    }

    public static ParseException newParseException( Bytes b, long position, String message, Throwable ex ) {
        long lineNumber   = 1;
        long columnNumber = 1;

        long i=0;
        DecodedCharacter buf = new DecodedCharacter();
        while ( i<position ) {
            b.readUTF8Character( i, b.sizeBytes(), buf );

            char c = buf.c;
            if ( c == '\n' ) {
                lineNumber   += 1;
                columnNumber  = 1;
            } else {
                columnNumber += 1;
            }

            i += buf.numBytesConsumed;
        }

        return new ParseException( message, position, lineNumber, columnNumber, ex );
    }


    private long   offset;
    private long   lineNumber;
    private long   columnNumber;


    private ParseException( String msg, long offset, long lineNumber, long columnNumber, Throwable ex ) {
        super( formatMessage(lineNumber,columnNumber,msg), ex );

        this.offset       = offset;
        this.lineNumber   = lineNumber;
        this.columnNumber = columnNumber;
    }


    public long getOffset() {
        return offset;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public long getColumnNumber() {
        return columnNumber;
    }


    private static String formatMessage( long lineNumber, long columnNumber, String msg ) {
        return "("+lineNumber+","+columnNumber+"): " + msg;
    }

}
