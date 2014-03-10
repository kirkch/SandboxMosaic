package com.mosaic.io.streams;

import com.mosaic.lang.QA;
import com.mosaic.lang.UTF8;

import java.util.ArrayList;
import java.util.List;


/**
 * Captures the information written to a List of Strings.  Useful for tests.
 */
public class CapturingWriter implements WriterX {

    public List<String> audit = new ArrayList<>();

    {
        audit.add("");
    }

    public void writeBoolean( boolean v ) {
        append( Boolean.toString(v) );
    }

    public void writeByte( byte v ) {
        append( Byte.toString(v) );
    }

    public void writeBytes( byte[] bytes ) {
        for ( byte b : bytes ) {
            writeByte( b );
        }
    }

    public void writeBytes( byte[] bytes, int fromIndexInc, int numBytes ) {
        for ( int i=fromIndexInc; i<fromIndexInc+numBytes; i++ ) {
            writeByte( bytes[i] );
        }
    }

    public void writeCharacter( char v ) {
        append( Character.toString(v) );
    }

    public void writeCharacters( char[] chars ) {
        for ( char c : chars ) {
            writeCharacter( c );
        }
    }

    public void writeCharacters( char[] chars, int fromIndexInc, int numChars ) {
        for ( int i=fromIndexInc; i<fromIndexInc+numChars; i++ ) {
            writeCharacter( chars[i] );
        }
    }

    public void writeShort( short v ) {
        append( Short.toString(v) );
    }

    public void writeUnsignedShort( int v ) {
        QA.isUnsignedShort( v, "v" );

        append( Integer.toString( v ) );
    }

    public void writeInt( int v ) {
        append( Integer.toString( v ) );
    }

    public void writeUnsignedInt( long v ) {
        QA.isUnsignedInt( v, "v" );

        append( Long.toString( v ) );
    }

    public void writeLong( long v ) {
        append( Long.toString( v ) );
    }

    public void writeFloat( float v ) {
        append( Float.toString( v ) );
    }

    public void writeDouble( double v ) {
        append( Double.toString( v ) );
    }

    public void writeString( String v ) {
        append( v );
    }

    public void writeLine( String v ) {
        append( v );

        nextLine();
    }

    public void writeUTF8( UTF8 v ) {
        append( v.toString() );
    }

    public void writeLine( UTF8 v ) {
        append( v.toString() );

        nextLine();
    }


    private void append( String v ) {
        int    index = audit.size() - 1;
        String line  = audit.get( index ) + v;

        audit.set( index, line );
    }

    private void nextLine() {
        audit.add("");
    }
}
