package com.mosaic.io.streams;

import com.mosaic.lang.QA;
import com.mosaic.lang.UTF8;

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

    public void writeDouble( double v ) {
        out.print( v );
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

}
