package com.mosaic.io;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 *
 */
public class ByteBufferUtils {

    public static Charset UTF8 = Charset.forName("UTF-8");


    public static ByteBuffer encode( String s, Charset cs ) {
        return ByteBuffer.wrap( s.getBytes(cs) );
    }

    /**
     * Are the bytes starting from pos equal to the targetBytes?
     */
    public static boolean startsWith( int pos, ByteBuffer buf, byte[] targetBytes ) {
        int remaining = buf.limit() - pos;

        if ( targetBytes.length > remaining ) {
            return false;
        }

        for ( int i=0; i<targetBytes.length; i++ ) {
            if ( targetBytes[i] != buf.get(i+pos) ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Do any of the targetByte arrays begin at position pos in buf?
     */
    public static boolean startsWith( int pos, ByteBuffer buf, byte[][] targetBytes ) {
        int limit     = buf.limit();
        int remaining = limit - pos;

        if ( remaining == 0 ) {
            return false;
        }

        byte b = buf.get(pos);
        for ( byte[] target : targetBytes ) {
            if ( remaining >= target.length && target[0] == b ) {
                if ( _byteArrayOverlaps(pos, 1, buf, target) ) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean _byteArrayOverlaps(int pos, int byteArrayStartIndex, ByteBuffer buf, byte[] target) {
        byte b;
        for ( int i=byteArrayStartIndex; i<target.length; i++ ) {
            b = buf.get(pos+i);

            if ( b != target[i] ) {
                return false;
            }
        }

        return true;
    }

    public static String toString( ByteBuffer buf, int startInc, int endExc, Charset charset) {
        int len = endExc - startInc;
        if ( len == 0 ) {
            return "";
        }

        byte[] bytes = new byte[len];

        for ( int i=0; i<len; i++ ) {
            bytes[i] = buf.get(startInc+i);
        }

        return new String( bytes, charset );
    }

}
