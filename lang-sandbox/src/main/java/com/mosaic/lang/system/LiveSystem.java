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

    public LiveSystem() {
        this( new SystemClock() );
    }

    public LiveSystem( SystemClock clock ) {
        super(
            new ActualFileSystem(),
            clock,
            new PrintStreamCharacterStream(System.out), // stdout
            new PrintStreamCharacterStream(System.out), // stderr
            new NullCharacterStream(),                  // dev
            new PrintStreamCharacterStream(System.out), // ops
            new PrintStreamCharacterStream(System.out), // users
            new LogWriter(clock, "warn",  new PrintStreamCharacterStream(System.err)),
            new LogWriter(clock, "fatal", new PrintStreamCharacterStream(System.err))
        );
    }

}
