package com.mosaic.lang.system;

import com.mosaic.io.filesystemx.disk.ActualFileSystem;
import com.mosaic.io.streams.NullWriter;
import com.mosaic.io.streams.PrintStreamWriterX;
import com.mosaic.lang.time.SystemClock;


/**
 *
 */
public class LiveSystem extends SystemX {

    public static SystemX newSystem() {
        SystemClock clock = new SystemClock();

        return new LiveSystem( clock );
    }

    public LiveSystem( SystemClock clock ) {
        super(
            new ActualFileSystem(),
            clock,
            new LogWriter(clock, "audit",  new PrintStreamWriterX(System.err)),
            new PrintStreamWriterX(System.out),
            new LogWriter(clock, "warn",  new PrintStreamWriterX(System.err)),
            new LogWriter(clock, "error", new PrintStreamWriterX(System.err)),
            new NullWriter() //new LogWriter(clock, "debug", new PrintStreamWriterX(System.out))
        );
    }

}
