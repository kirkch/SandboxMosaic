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

    // todo research faster ways to write float and decimal numbers

    public void writeFloat( float v, int numDecimalPlaces ) {
        // this strangeness is because 3.145 (2dp) would be 3.14 without it :(
        // that is, 3.145 becomes 3.1449 and so would round the wrong way, so we add 0.00001
        // to correct for this
        float roundingFudge = 1/(float) Math.pow( 10, numDecimalPlaces+2 );

        append( String.format("%."+numDecimalPlaces+"f", v+roundingFudge) );
    }

    public void writeDouble( double v ) {
        append( Double.toString( v ) );
    }

    public void writeDouble( double v, int numDecimalPlaces ) {
        // decimals do not require rounding fudge as they are higher precision
        append( String.format("%."+numDecimalPlaces+"f", v) );
    }

    public void writeString( String v ) {
        append( v );
    }

    public void writeLine( String v ) {
        append( v );

        newLine();
    }

    public void writeUTF8( UTF8 v ) {
        append( v.toString() );
    }

    public void writeLine( UTF8 v ) {
        append( v.toString() );

        newLine();
    }

    public void writeException( Throwable ex ) {
        while ( ex != null ) {
            writeLine( ex.getClass().getSimpleName() + ": " + ex.getMessage() );

            ex = ex.getCause();
        }
    }

    public void writeException( String msg, Throwable ex ) {
        writeLine( msg );
        writeException( ex );
    }

    public void newLine() {
        audit.add("");
    }


    private void append( String v ) {
        int    index = audit.size() - 1;
        String line  = audit.get( index ) + v;

        audit.set( index, line );
    }
}
