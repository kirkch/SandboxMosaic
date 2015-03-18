package com.mosaic.lang.text;

import com.mosaic.bytes.Bytes;
import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.ByteUtils;

import java.util.Arrays;


/**
 *
 */
public class UTF8  {


    private static byte[] toArray( Bytes b, long fromInc, long toExc ) {
        int    numBytes = Backdoor.toInt( toExc - fromInc );
        byte[] buf      = new byte[numBytes];

        b.readBytes( fromInc, toExc, buf, 0, numBytes );

        return buf;
    }

    private static byte[] toArray( Bytes2 b, long fromInc, long toExc ) {
        int    numBytes = Backdoor.toInt( toExc - fromInc );
        byte[] buf      = new byte[numBytes];

        b.readBytes( fromInc, toExc, buf, 0, numBytes );

        return buf;
    }


    private int    hashCode;
    private byte[] encodedCharacters;


    public UTF8( Bytes b, long fromInc, long toExc ) {
        this( toArray(b,fromInc,toExc) );
    }

    public UTF8( Bytes2 b, long fromInc, long toExc ) {
        this( toArray(b,fromInc,toExc) );
    }

    public UTF8( String target ) {
        this( target.getBytes(SystemX.UTF8) );
    }

    public UTF8( byte[] text ) {
        this.encodedCharacters = text;
    }

    public byte[] getBytes() {
        return encodedCharacters;
    }

    public int getByteCount() {
        return encodedCharacters.length;
    }

    public int hashCode() {
        int h = hashCode;
        if ( h == 0 ) {
            h = calcHashCode();

            this.hashCode = h;
        }

        return h;
    }

    public boolean equals( Object o ) {
        if ( !(o instanceof UTF8) ) {
            return false;
        }

        UTF8 other = (UTF8) o;
        return this.encodedCharacters.length == other.encodedCharacters.length
            && this.hashCode() == other.hashCode()
            && Arrays.equals( this.encodedCharacters, other.encodedCharacters );
    }

    public UTF8 copy() {
        // no need to copy yet as the constructor already copies the bytes..
        // in the future this will not be the case, so users of memory mapped files should copy
        // if they want to keep the string after closing the file
        return this;
    }

    /**
     * Creates a new string that is at most maxNumBytes.  The string will be truncated to the closest
     * full character that is less than maxNumBytes.  If the string is already smaller than the
     * specified number of bytes, then the UTF8 string will be returned unchanged.
     */
    public UTF8 truncateToNumOfBytes( int maxNumBytes ) {
        if ( encodedCharacters.length <= maxNumBytes ) {
            return this;
        }

        int toExc = UTF8Tools.findTruncationPoint(encodedCharacters, 0, maxNumBytes);

        return new UTF8( ByteUtils.copy(encodedCharacters, 0, toExc) );
    }

    public String toString() {
        return new String( encodedCharacters, SystemX.UTF8 );
    }

    private int calcHashCode() {
        int h = 7;

        for ( byte b : encodedCharacters ) {
            h = h*31 + b;
        }

        return h;
    }

}
