package com.mosaic.lang.system;

import com.mosaic.io.filesystemx.disk.ActualFileSystem;
import com.mosaic.io.streams.NullCharacterStream;
import com.mosaic.io.streams.PrintStreamCharacterStream;
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
            new LogWriter(clock, "audit",  new PrintStreamCharacterStream(System.err)),
            new PrintStreamCharacterStream(System.out),
            new LogWriter(clock, "warn",  new PrintStreamCharacterStream(System.err)),
            new LogWriter(clock, "error", new PrintStreamCharacterStream(System.err)),
            new NullCharacterStream() //new LogWriter(clock, "debug", new PrintStreamWriterX(System.out))
        );
    }

}
