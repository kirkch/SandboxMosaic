package com.mosaic.lang.system;

import com.mosaic.io.filesystemx.disk.ActualFileSystem;
import com.mosaic.io.streams.NullCharacterStream;
import com.mosaic.io.streams.PrintStreamCharacterStream;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.time.SystemClock;

import java.io.File;


/**
 *
 */
public class LiveSystem extends SystemX {

    public static SystemX withNoLogging( File rootDir ) {
        return new LiveSystem( ReflectionUtils.getCallersClass().getSimpleName(), new SystemClock(), new NullCharacterStream(), rootDir );
    }

    public static SystemX newSystem( String systemName ) {
        SystemClock clock = new SystemClock();

        return new LiveSystem( systemName, clock );
    }

    public LiveSystem() {
        this( ReflectionUtils.getCallersClass().getSimpleName() );
    }

    public LiveSystem( String systemName ) {
        this( systemName, new SystemClock() );
    }

    public LiveSystem( String systemName, SystemClock clock ) {
        this( systemName, clock, false );
    }

    public LiveSystem( String systemName, SystemClock clock, boolean isVerbose ) {
        super(
            systemName,
            new ActualFileSystem(System.getProperty("user.dir")),
            clock,
            new PrintStreamCharacterStream( System.out ),  // stdout
            new PrintStreamCharacterStream( System.out ),  // stderr
            new PrintStreamCharacterStream( System.out ),  // dev
            new PrintStreamCharacterStream( System.out ),  // ops
            new PrintStreamCharacterStream( System.out ),  // users
            new LogWriter( clock, "warn", new PrintStreamCharacterStream( System.err ) ),
            new LogWriter( clock, "fatal", new PrintStreamCharacterStream( System.err ) )
        );
    }

    public LiveSystem( String systemName, SystemClock clock, NullCharacterStream noLogging, File rootDir ) {
        super(
            systemName,
            new ActualFileSystem( rootDir.getAbsolutePath() ),
            clock,
            noLogging, // stdout
            noLogging, // stderr
            noLogging, // dev
            noLogging, // ops
            noLogging, // users
            noLogging,
            noLogging
        );
    }
}
