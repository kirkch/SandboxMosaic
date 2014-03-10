package com.mosaic.lang;

import com.mosaic.lang.system.SystemX;


/**
 *
 */
public class UTF8 {

    public static UTF8 wrap( byte[] bytes ) {
        return new UTF8( bytes );
    }

    public static UTF8 wrap( String str ) {
        return wrap( str.getBytes( SystemX.UTF8) );
    }



    private byte[] bytes;

    public UTF8( byte[] bytes ) {

        this.bytes = bytes;
    }


    public int getSizeInBytes() {
        return bytes.length;
    }

    public byte[] asBytes() {
        return bytes;
    }

    public String toString() {
        return new String( bytes, SystemX.UTF8 );
    }
}
