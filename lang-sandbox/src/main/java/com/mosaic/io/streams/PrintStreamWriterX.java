package com.mosaic.io.streams;

import com.mosaic.lang.BigCashType;
import com.mosaic.lang.QA;
import com.mosaic.lang.SmallCashType;
import com.mosaic.lang.UTF8;
import com.mosaic.utils.MathUtils;

import java.io.PrintStream;


/**
 * PrintStream adapter for the WriterX interface.
 */
public class PrintStreamWriterX implements WriterX {

    private PrintStream out;


    public PrintStreamWriterX( PrintStream out ) {
        QA.argNotNull( out, "out" );

        this.out = out;
    }



    public void writeBoolean( boolean v ) {
        out.print( v );
    }

    public void writeByte( byte v ) {
        out.write( v );
    }

    public void writeBytes( byte[] bytes ) {
        out.write( bytes, 0, bytes.length );
    }

    public void writeBytes( byte[] bytes, int fromIndexInc, int numBytes ) {
        out.write( bytes, fromIndexInc, numBytes );
    }

    public void writeCharacter( char v ) {
        out.print( v );
    }

    public void writeCharacters( char[] chars ) {
        out.print( chars );
    }

    public void writeCharacters( char[] chars, int fromIndexInc, int numChars ) {
        for ( int i=fromIndexInc; i<fromIndexInc+numChars; i++ ) {
            out.print( chars[i] );
        }
    }

    public void writeShort( short v ) {
        out.print( v );
    }

    public void writeUnsignedShort( int v ) {
        QA.isUnsignedShort( v, "v" );

        out.print( v );
    }

    public void writeInt( int v ) {
        out.print( v );
    }

    public void writeFixedWidthInt( int v, int fixedWidth, byte paddingByte ) {
        int numBytes     = MathUtils.charactersLengthOf( v );
        int prefixLength = fixedWidth-numBytes;

        StringBuilder buf = new StringBuilder(numBytes);
        for ( int i=0; i<prefixLength; i++ ) {
            buf.append( (char) paddingByte );
        }

        buf.append( Integer.toString( v ) );

        writeString( buf.toString() );
    }

    public void writeSmallCashMajorUnit( int v ) {
        int major = SmallCashType.extractMajorComponent( v );
        int minor = SmallCashType.extractMinorComponent( v );

        writeInt(major);
        writeCharacter( '.' );
        writeFixedWidthInt( minor, 2, (byte) '0' );
    }

    public void writeSmallCashMinorUnit( int v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeBigCashMajorUnit( long v ) {
        long major = BigCashType.extractMajorComponent( v );
        long minor = BigCashType.extractMinorComponent( v );

        writeLong( major );
        writeCharacter( '.' );
        writeFixedWidthInt( (int) minor, 2, (byte) '0' );
    }

    public void writeBigCashMinorUnit( long v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeUnsignedInt( long v ) {
        QA.isUnsignedInt( v, "v" );

        out.print( v );
    }

    public void writeLong( long v ) {
        out.print( v );
    }

    public void writeFloat( float v ) {
        out.print( v );
    }

    public void writeFloat( float v, int numDecimalPlaces ) {
        // this strangeness is because 3.145 (2dp) would be 3.14 without it :(
        // that is, 3.145 becomes 3.1449 and so would round the wrong way, so we add 0.00001
        // to correct for this
        float roundingFudge = 1/(float) Math.pow( 10, numDecimalPlaces+2 );

        out.print( String.format("%."+numDecimalPlaces+"f", v+roundingFudge) );
    }

    public void writeDouble( double v ) {
        out.print( v );
    }

    public void writeDouble( double v, int numDecimalPlaces ) {
        // decimals do not require rounding fudge as they are higher precision

        out.print( String.format("%."+numDecimalPlaces+"f", v) );
    }

    public void writeString( String v ) {
        out.print( v );
    }

    public void writeLine( String v ) {
        out.println( v );
    }

    public void writeUTF8( UTF8 v ) {
        out.print( v );
    }

    public void writeLine( UTF8 v ) {
        out.println( v );
    }

    public void writeException( Throwable ex ) {
        ex.printStackTrace( out );
    }

    public void writeException( String msg, Throwable ex ) {
        writeLine( msg );
        ex.printStackTrace( out );
    }

    public void newLine() {
        out.println();
    }
}
