package com.mosaic.io;

import java.io.IOException;

/**
 *
 */
public interface Formatter<T> {

    public void write( Appendable buf, T v ) throws IOException;

}
