package com.mosaic.lang.system;

import com.mosaic.io.filesystemx.disk.ActualFileSystem;
import com.mosaic.io.streams.PrintStreamWriterX;
import com.mosaic.lang.time.SystemClock;


/**
 *
 */
public class LiveSystem extends SystemX {

    public LiveSystem() {
        super(
            new ActualFileSystem(),
            new SystemClock(),
            new PrintStreamWriterX(System.out), // info,
            new PrintStreamWriterX(System.err), // warn,
            new PrintStreamWriterX(System.err), // error,
            new PrintStreamWriterX(System.out)  // debug
        );
    }

}
