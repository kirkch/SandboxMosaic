package com.mosaic.bytes;

import com.mosaic.io.bytes.ArrayBytes;
import com.mosaic.lang.QA;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 *
 */
public class ClassPathBytes2 {

    /**
     * Loads the resource into an on-heap byte[].
     *
     * @return null if the resource was not found
     */
    public static Bytes2 loadFromClassPath( ClassLoader classLoader, String resourcePath ) throws IOException {
        if ( resourcePath.startsWith("/") ) {
            resourcePath = resourcePath.substring(1);
        }

        InputStream in = classLoader.getResourceAsStream( resourcePath );
        if ( in == null ) {
            return null;
        }

        BufferedInputStream bufferedInput = new BufferedInputStream( in );

        int    numBytes = countRemainingBytes( bufferedInput );
        byte[] bytes    = new byte[numBytes];

        bufferedInput.read( bytes );

        return new ArrayBytes2( bytes );
    }


    private static int countRemainingBytes( BufferedInputStream in ) throws IOException {
        in.mark( Integer.MAX_VALUE );

        long lastSkipCount;
        long count = 0;
        do {
            lastSkipCount = in.skip( 1024 );

            count += lastSkipCount;
        } while ( lastSkipCount == 1024);

        QA.isInt( count, "input stream is too large for an int" );

        in.reset();

        return (int) count;
    }

}
