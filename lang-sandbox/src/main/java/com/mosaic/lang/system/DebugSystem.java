package com.mosaic.lang.system;

import com.mosaic.io.filesystemx.inmemory.InMemoryFileSystem;
import com.mosaic.io.streams.CapturingWriter;
import com.mosaic.lang.time.SystemClock;


/**
 *
 */
public class DebugSystem extends SystemX {

    private CapturingWriter cInfo;  // files used to avoid ugly casting from the assert methods
    private CapturingWriter cWarn;
    private CapturingWriter cError;
    private CapturingWriter cDebug;


    public DebugSystem() {
        this(
            new InMemoryFileSystem(),
            new SystemClock(),
            new CapturingWriter(), // info,
            new CapturingWriter(), // warn,
            new CapturingWriter(), // error,
            new CapturingWriter()  // debug
        );
    }


    private DebugSystem( InMemoryFileSystem system, SystemClock clock, CapturingWriter info, CapturingWriter warn, CapturingWriter error, CapturingWriter debug) {
        super(system,clock,info,warn,error,debug);

        this.cInfo  = info;
        this.cWarn  = warn;
        this.cError = error;
        this.cDebug = debug;
    }


    public void tearDown() {
        fileSystem.deleteAll();
    }

    public void assertInfo( String expectedMessage ) {
        for ( String x : cInfo.audit ) {
            if ( x.equals(expectedMessage) ) {
                return;
            }
        }

        throw new AssertionError(
            String.format(
                "Failed to find '%s' amongst the error messages '%s'",
                expectedMessage,
                cInfo.audit
            )
        );
    }

    public void assertWarn( String expectedMessage ) {
        for ( String x : cWarn.audit ) {
            if ( x.equals(expectedMessage) ) {
                return;
            }
        }

        throw new AssertionError(
            String.format(
                "Failed to find '%s' amongst the error messages '%s'",
                expectedMessage,
                cWarn.audit
            )
        );
    }

    public void assertError( String expectedMessage ) {
        for ( String x : cError.audit ) {
            if ( x.equals(expectedMessage) ) {
                return;
            }
        }

        throw new AssertionError(
            String.format(
                "Failed to find '%s' amongst the error messages '%s'",
                expectedMessage,
                cError.audit
            )
        );
    }

    public void assertDebug( String expectedMessage ) {
        for ( String x : cDebug.audit ) {
            if ( x.equals(expectedMessage) ) {
                return;
            }
        }

        throw new AssertionError(
            String.format(
                "Failed to find '%s' amongst the error messages '%s'",
                expectedMessage,
                cDebug.audit
            )
        );
    }

}
