package com.mosaic.lang.text;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;

import java.util.Arrays;


/**
 *
 */
public class UTF8 implements Text {


    private static byte[] toArray( Bytes b, long fromInc, long toExc ) {
        int    numBytes = Backdoor.toInt( toExc - fromInc );
        byte[] buf      = new byte[numBytes];

        b.readBytes( fromInc, buf, 0, numBytes );

        return buf;
    }


    private int    hashCode;
    private byte[] encodedCharacters;


//    public UTF8( String text ) {
//
//    }
//
//    public UTF8( byte[] text, int fromInc, int toExc ) {
//
//    }

    public UTF8( Bytes b, long fromInc, long toExc ) {
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