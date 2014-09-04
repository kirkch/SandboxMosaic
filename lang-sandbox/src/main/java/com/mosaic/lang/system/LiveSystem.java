package com.mosaic.lang.system;

import com.mosaic.io.filesystemx.disk.ActualFileSystem;
import com.mosaic.io.streams.NullCharacterStream;
import com.mosaic.io.streams.PrintStreamCharacterStream;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.time.SystemClock;


/**
 *
 */
public class LiveSystem extends SystemX {

    public static SystemX withNoLogging() {
        return new LiveSystem( ReflectionUtils.getCallersClass().getSimpleName(), new SystemClock(), new NullCharacterStream() );
    }

    public static SystemX newSystem( String systemName ) {
        SystemClock clock = new SystemClock();

        LiveSystem liveSystem = new LiveSystem( systemName, clock );
        liveSystem.setCurrentWorkingDirectory( liveSystem.getDirectory(System.getProperty("user.dir")) );

        return liveSystem;
    }

    public LiveSystem() {
        this( ReflectionUtils.getCallersClass().getSimpleName() );
    }

    public LiveSystem( String systemName ) {
        this( systemName, new SystemClock() );

        setCurrentWorkingDirectory( getDirectory( System.getProperty( "user.dir" ) ) );
    }

    public LiveSystem( String systemName, SystemClock clock ) {
        this( systemName, clock, false );
    }

    public LiveSystem( String systemName, SystemClock clock, boolean isVerbose ) {
        super(
            systemName,
            new ActualFileSystem(),
            clock,
            new PrintStreamCharacterStream( System.out ),  // stdout
            new PrintStreamCharacterStream( System.out ),  // stderr
            new PrintStreamCharacterStream( System.out ),  // dev
            new PrintStreamCharacterStream( System.out ),  // ops
            new PrintStreamCharacterStream( System.out ),  // users
            new LogWriter( clock, "warn", new PrintStreamCharacterStream( System.err ) ),
            new LogWriter( clock, "fatal", new PrintStreamCharacterStream( System.err ) )
        );

        setCurrentWorkingDirectory( getDirectory( System.getProperty( "user.dir" ) ) );
    }

    public LiveSystem( String systemName, SystemClock clock, NullCharacterStream noLogging ) {
        super(
            systemName,
            new ActualFileSystem(),
            clock,
            noLogging, // stdout
            noLogging, // stderr
            noLogging, // dev
            noLogging, // ops
            noLogging, // users
            noLogging,
            noLogging
        );

        setCurrentWorkingDirectory( getDirectory( System.getProperty( "user.dir" ) ) );
    }
}
