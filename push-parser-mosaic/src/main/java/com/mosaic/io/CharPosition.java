package com.mosaic.io;

/**
 *
 */
public class CharPosition {

    private final int lineNumber;
    private final int columnNumber;
    private final int charOffset;

    public CharPosition( int lineNumber, int columnNumber, int charOffset ) {
        this.lineNumber   = lineNumber;
        this.columnNumber = columnNumber;
        this.charOffset   = charOffset;
    }

    /**
     * The line number of where this matcher started matching from.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * The column number of where this matcher started matching from.
     */
    public int getColumnNumber() {
        return columnNumber;
    }

//    /**
//     * The byte offset from the start of the stream of where this matcher started matching from.
//     */
//    public int getByteOffset();

    /**
     * The character offset from the start of the stream where this matcher started to match from.
     */
    public int getCharacterOffset() {
        return charOffset;
    }

}
