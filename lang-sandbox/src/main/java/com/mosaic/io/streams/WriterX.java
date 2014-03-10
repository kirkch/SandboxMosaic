package com.mosaic.io.streams;

import com.mosaic.lang.UTF8;


/**
 * Offers similar functionality to Java io streams; but without the heavy lashings
 * of synchronization.
 */
public interface WriterX {

    public void writeBoolean( boolean v );
    public void writeByte( byte v );
    public void writeBytes( byte[] bytes);
    public void writeBytes( byte[] bytes, int fromIndexInc, int numBytes );
    public void writeCharacter( char v );
    public void writeCharacters( char[] chars );
    public void writeCharacters( char[] chars, int fromIndexInc, int numChars );
    public void writeShort( short v );
    public void writeUnsignedShort( int v );
    public void writeInt( int v );
    public void writeUnsignedInt( long v );
    public void writeLong( long v );
    public void writeFloat( float v );
    public void writeDouble( double v );
    public void writeString( String v );
    public void writeLine( String v );
    public void writeUTF8( UTF8 v );
    public void writeLine( UTF8 v );

}
