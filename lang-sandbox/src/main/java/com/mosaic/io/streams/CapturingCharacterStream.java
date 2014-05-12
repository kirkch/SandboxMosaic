package com.mosaic.io.streams;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.BigCashType;
import com.mosaic.lang.QA;
import com.mosaic.lang.SmallCashType;
import com.mosaic.lang.text.UTF8;
import com.mosaic.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Captures the information written to a List of Strings.  Useful for tests.
 */
public class CapturingCharacterStream implements CharacterStream {

    public List<String> audit = new ArrayList<>();

    private final StringBuilder buf = new StringBuilder();


    public CapturingCharacterStream() {
        this( new ArrayList<String>() );
    }

    public CapturingCharacterStream( List<String> audit ) {
        this.audit = audit;
    }


    public boolean isEnabled() {
        return true;
    }

    public void writeBoolean( boolean v ) {
        append( Boolean.toString(v) );
    }

    public void writeByteAsNumber( byte v ) {
        append( Byte.toString(v) );
    }

    public void writeUTF8Bytes( Bytes bytes ) {
        append( new UTF8(bytes, 0, bytes.getEndIndexExc()).toString() );
    }

    public void writeUTF8Bytes( Bytes bytes, int fromIndexInc, int toExc ) {
        append( new UTF8(bytes, fromIndexInc, toExc).toString() );
    }

    public void writeUTF8Bytes( byte[] bytes ) {
        for ( byte b : bytes ) {
            writeByteAsNumber( b );
        }
    }

    public void writeUTF8Bytes( byte[] bytes, int fromIndexInc, int toExc ) {
        for ( int i=fromIndexInc; i<toExc; i++ ) {
            writeByteAsNumber( bytes[i] );
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

    public void writeFixedWidthInt( int v, int fixedWidth, byte paddingByte ) {
        int numBytes     = MathUtils.charactersLengthOf( v );
        int prefixLength = fixedWidth-numBytes;

        StringBuilder buf = new StringBuilder(numBytes);
        for ( int i=0; i<prefixLength; i++ ) {
            buf.append( (char) paddingByte );
        }

        buf.append( Integer.toString( v ) );

        append( buf.toString() );
    }

    public void writeSmallCashMajorUnit( int v ) {
        int major = SmallCashType.extractMajorComponent( v );
        int minor = SmallCashType.extractMinorComponent( v );

        writeInt(major);
        writeCharacter( '.' );
        writeFixedWidthInt( minor, 2, (byte) '0' );
    }

    public void writeSmallCashMinorUnit( int v ) {
        long major = v / 10;
        long minor = Math.abs(v%10);

        writeLong(major);
        writeCharacter( '.' );
        writeLong(minor);
    }

    public void writeBigCashMajorUnit( long v ) {
        long major = BigCashType.extractMajorComponent( v );
        long minor = BigCashType.extractMinorComponent( v );

        writeLong( major );
        writeCharacter( '.' );
        writeFixedWidthInt( (int) minor, 2, (byte) '0' );
    }

    public void writeBigCashMinorUnit( long v ) {
        long major = v / 100;
        long minor = Math.abs(v%100);

        writeLong(major);
        writeCharacter( '.' );
        writeFixedWidthInt( (int) minor, 2, (byte) '0' );
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

    public void writeFloat( float v, int numDecimalPlaces ) {
        // this strangeness is because 3.145 (2dp) would be 3.14 without it :(
        // that is, 3.145 becomes 3.1449 and so would round the wrong way, so we add 0.00001
        // to hack around this
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
        audit.add( formatLine(buf.toString()) );

        buf.setLength(0);
    }

    public void flush() {
        if ( buf.length() > 0 ) {
            audit.add( formatLine(buf.toString()) );

            buf.setLength(0);
        }
    }

    protected String formatLine( String s ) {
        return s;
    }


    private void append( String v ) {
        buf.append(v);
    }
}
