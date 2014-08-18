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

        LiveSystem liveSystem = new LiveSystem( clock );
        liveSystem.setCurrentWorkingDirectory( liveSystem.getDirectory(System.getProperty("user.dir")) );

        return liveSystem;
    }

    public LiveSystem() {
        this( new SystemClock() );

        setCurrentWorkingDirectory( getDirectory(System.getProperty("user.dir")) );
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

        setCurrentWorkingDirectory( getDirectory( System.getProperty( "user.dir" ) ) );
    }

}
