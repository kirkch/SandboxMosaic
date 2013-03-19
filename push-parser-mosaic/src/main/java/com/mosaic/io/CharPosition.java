package com.mosaic.io;

/**
 *
 */
public class CharPosition {

    private final int  lineNumber;
    private final int  columnNumber;
    private final long charOffset;

    public CharPosition() {
        this(0,0,0);
    }

    public CharPosition( int lineNumber, int columnNumber, long charOffset ) {
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
    public long getCharacterOffset() {
        return charOffset;
    }


    /**
     * Create a new CharPosition based on walking over the supplied characters.
     *
     * @param numCharacters how many characters to consume
     * @param characters the characters to walk overr
     */
    CharPosition walkCharacters( int numCharacters, Characters characters ) {
        int  line              = this.lineNumber;
        int  col               = this.columnNumber;
        long totalStreamOffset = this.charOffset;

        for ( int i=0; i<numCharacters; i++ ) {
            char c = characters.charAt( i );


            totalStreamOffset++;

            if ( c == '\n' ) {
                col = 0;
                line++;
            } else {
                col++;
            }
        }

        return new CharPosition( line, col, totalStreamOffset );
    }

    public CharPosition setCharacterOffset( long newStreamOffset ) {
        return new CharPosition( this.lineNumber, this.columnNumber, newStreamOffset );
    }

    public String toString() {
        return String.format("public CharPosition(lineNumber=%d, columnNumber=%d, charOffset=%d)",lineNumber,columnNumber, charOffset);
    }

    public int hashCode() {
        return (int) charOffset;
    }

    public boolean equals( Object o ) {
        if ( !(o instanceof CharPosition) ) {
            return false;
        }

        CharPosition other = (CharPosition) o;
        return this.lineNumber == other.lineNumber && this.columnNumber == other.columnNumber && this.charOffset == other.charOffset;
    }

}
