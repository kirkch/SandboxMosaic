package com.mosaic.lang.text;

import com.mosaic.io.bytes.InputBytes;


/**
 *
 */
public class ParseException extends RuntimeException {

    public static ParseException newParseException( InputBytes b, long position, String fileName, String message ) {
        return newParseException( b, position, fileName, message, null );
    }

    public static ParseException newParseException( InputBytes b, long position, String fileName, String message, Throwable
        ex ) {
        long lineNumber   = 1;
        long columnNumber = 1;

        long i=0;
        DecodedCharacter buf = new DecodedCharacter();
        while ( i<position ) {
            b.readUTF8Character( i, buf );

            char c = buf.c;
            if ( c == '\n' ) {
                lineNumber   += 1;
                columnNumber  = 1;
            } else {
                columnNumber += 1;
            }

            i += buf.numBytesConsumed;
        }

        return new ParseException( fileName, message, position, lineNumber, columnNumber, ex );
    }


    private String fileName;
    private long   offset;
    private long   lineNumber;
    private long   columnNumber;


    private ParseException( String fileName, String msg, long offset, long lineNumber, long columnNumber, Throwable ex ) {
        super( formatMessage(fileName,lineNumber,columnNumber,msg), ex );

        this.fileName     = fileName;
        this.offset       = offset;
        this.lineNumber   = lineNumber;
        this.columnNumber = columnNumber;
    }


    public String getFileName() {
        return fileName;
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


    private static String formatMessage( String fileName, long lineNumber, long columnNumber, String msg ) {
        return fileName + " ("+lineNumber+","+columnNumber+"): " + msg;
    }

}
