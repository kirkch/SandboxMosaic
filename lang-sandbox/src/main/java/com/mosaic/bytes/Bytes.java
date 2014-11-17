package com.mosaic.bytes;

import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8;

import java.io.InputStream;


/**
 *
 */
public interface Bytes {

    public void release();
    public long sizeBytes();
    public void sync();

    public void resize( long newLength );
    public void fill( long from, long toExc, byte v );



    public boolean readBoolean( long offset, long maxExc );
    public void writeBoolean( long offset, long maxExc, boolean v );


    public byte readByte( long offset, long maxExc );
    public void writeByte( long offset, long maxExc, byte v );


    public short readShort( long offset, long maxExc );
    public void writeShort( long offset, long maxExc, short v );


    public char readCharacter( long offset, long maxExc );
    public void writeCharacter( long offset, long maxExc, char v );


    public int readInt( long offset, long maxExc );
    public void writeInt( long offset, long maxExc, int v );


    public long readLong( long offset, long maxExc );
    public void writeLong( long offset, long maxExc, long v );


    public float readFloat( long offset, long maxExc );
    public void writeFloat( long offset, long maxExc, float v );


    public double readDouble( long offset, long maxExc );
    public void writeDouble( long offset, long maxExc, double v );


    public short readUnsignedByte( long offset, long maxExc );
    public void writeUnsignedByte( long offset, long maxExc, short v );


    public int readUnsignedShort( long offset, long maxExc );
    public void writeUnsignedShort( long offset, long maxExc, int v );


    public long readUnsignedInt( long offset, long maxExc );
    public void writeUnsignedInt( long offset, long maxExc, long v );





    /**
     * Reads a single UTF-8 encoded character from the specified offset.  Uses
     * DecodedCharacter as an OUT param so that the character can be returned
     * with a count of how many bytes were used to hold the character without
     * having to allocateNewRecord any extra objects.
     */
    public void readUTF8Character( long offset, long maxExc, DecodedCharacter output );
    public int writeUTF8Character( long offset, long maxExc, char c );


    /**
     * Reads the specified string encoded in UTF-8 into the supplied StringBuilder.
     * UTF-8 is a variable width encoding, for most latin characters it uses
     * only one byte.  For the rest it will use two or three.
     *
     * @return the number of bytes read
     */
    public int readUTF8String( long offset, long maxExc, Appendable output );
    public UTF8 readUTF8String( long offset, long maxExc );
    public int writeUTF8String( long offset, long maxExc, CharSequence txt );
    public int writeUTF8String( long offset, long maxExc, UTF8 txt );

    public int writeNullTerminatedUTF8String( long offset, long maxExc, CharSequence txt );
    public int writeUTF8StringUndemarcated( long offset, long maxExc, CharSequence txt );

    /**
     * Copies as many bytes as are available, or will fit into the supplied
     * array.   Starts reading from the specified position, and will not modify
     * the current position.
     *
     * @return the number of bytes read
     */
    public int readBytes( long offset, long maxExc, byte[] destinationArray );
    public int writeBytes( long offset, long maxExc, byte[] sourceBytes );

    public int readBytes( long offset, long maxExc, Bytes destination );
    public int writeBytes( long offset, long maxExc, Bytes sourceBytes );

    public int readBytes( long offset, long maxExc, Bytes destination, long destinationInc, long destinationExc );
    public int writeBytes( long offset, long maxExc, Bytes sourceBytes, long sourceInc, long sourceExc );

    /**
     * Copies as many bytes that are specified into the supplied
     * array.  Starts reading from the current position, and will not
     * modify the current position.
     *
     * It is an error to request more bytes than are available.
     *
     * @param destinationArrayInc the offset to start writing to (inclusive)
     * @param destinationArrayExc   the offset to stop writing to (exclusive)
     */
    public int readBytes( long offset, long maxExc, byte[] destinationArray, long destinationArrayInc, long destinationArrayExc );
    public int writeBytes( long offset, long maxExc, byte[] sourceArray, long sourceArrayInc, long sourceArrayExc );

    /**
     * Copies numBytes from this buffer[offset] to the specified memory address.
     */
    public int readBytes( long offset, long maxExc, long toAddressBase, long toAddressInc, long toAddressExc );
    public int writeBytes( long offset, long maxExc, long fromAddressBase, long fromAddressInc, long fromAddressExc );




    public InputStream toInputStream();

    public byte[] toArray();


    public default void clear() {
        this.fill( 0, this.sizeBytes(), (byte) 0 );
    }

}
