package com.mosaic.io.bytes;

import java.io.IOException;
import java.io.InputStream;


/**
 *
 */
public class InputStreamAdapter extends InputStream {

    private InputBytes bytes;

    public InputStreamAdapter( InputBytes bytes ) {
        this.bytes = bytes;
    }

    public int read() throws IOException {
        if ( bytes.remaining() == 0 ) {
            return -1;
        }

        return bytes.readByte();
    }

}
