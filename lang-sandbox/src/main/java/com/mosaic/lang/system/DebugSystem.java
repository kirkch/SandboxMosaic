package com.mosaic.lang.system;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.io.filesystemx.inmemory.InMemoryFileSystem;
import com.mosaic.io.streams.CapturingWriter;
import com.mosaic.lang.text.PullParser;
import com.mosaic.lang.time.SystemClock;
import com.mosaic.utils.StringUtils;
import org.junit.ComparisonFailure;

import java.util.Arrays;
import java.util.List;


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
        String trimmedExpectedMessage = expectedMessage.trim();

        for ( String x : cInfo.audit ) {
            if ( x.trim().equals( trimmedExpectedMessage ) ) {
                return;
            }
        }

        throw new AssertionError(
            String.format(
                "Failed to find '%s' amongst the error messages \n'%s'",
                expectedMessage,
                StringUtils.concat( cInfo.audit, "[\n", "\n", "]" )
            )
        );
    }

    public void assertWarn( String expectedMessage ) {
        String trimmedExpectedMessage = expectedMessage.trim();

        for ( String x : cWarn.audit ) {
            if ( x.trim().equals( trimmedExpectedMessage ) ) {
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

    public void assertError( Class<? extends Throwable> expectedExceptionType, String expectedMessage ) {
        assertError( expectedExceptionType.getSimpleName() + ": " + expectedMessage );
    }

    public void assertError( String expectedMessage ) {
        String trimmedExpectedMessage = expectedMessage.trim();

        for ( String x : cError.audit ) {
            if ( x.trim().equals( trimmedExpectedMessage ) ) {
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
        String trimmedExpectedMessage = expectedMessage.trim();

        for ( String x : cDebug.audit ) {
            if ( x.trim().equals( trimmedExpectedMessage ) ) {
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

    public void assertNoWarnings() {
        if ( this.cWarn.audit.size() > 1 || this.cWarn.audit.get(0).length() > 0 ) {
            throw new AssertionError( "Expected no warnings, instead found: " + this.cWarn.audit );
        }
    }

    public void assertNoErrors() {
        if ( this.cError.audit.size() > 1 || this.cError.audit.get(0).length() > 0 ) {
            throw new AssertionError( "Expected no errors, instead found: " + this.cError.audit );
        }
    }

    public void assertNoDebugs() {
        if ( this.cDebug.audit.size() > 1 || this.cDebug.audit.get(0).length() > 0 ) {
            throw new AssertionError( "Expected no debug messages, instead found: " + this.cDebug.audit );
        }
    }

    public void assertNoInfos() {
        if ( this.cInfo.audit.size() > 1 || this.cInfo.audit.get(0).length() > 0 ) {
            throw new AssertionError( "Expected no info messages, instead found: " + this.cInfo.audit );
        }
    }

    public void assertHasFile( String filePath, String...expectedLines ) {
        FileX f = fileSystem.getFile( filePath );

        if ( f == null ) {
            throw new AssertionError( "File not found: " + filePath );
        }

        Bytes fileContents = f.loadBytes( FileModeEnum.READ_ONLY );
        List<String> actualLines = PullParser.toLines( fileContents );

        assertEquals(
            "File " + filePath + " existed but the contents was not what we expected",
            Arrays.asList(expectedLines),
            actualLines
        );
    }

    public void assertEmptyDirectory( String dirPath ) {
        DirectoryX dir = fileSystem.getDirectory( dirPath );

        if ( dir != null ) {
            List<FileX> files = dir.files();

            if ( files.size() > 0 ) {
                throw new ComparisonFailure( "Expected "+dirPath+" to be empty", "[]", files.toString() );
            }
        }

    }

    private void assertEquals( String msg, List<String> expectedLines, List<String> actualLines ) {
        if ( !expectedLines.equals(actualLines) ) {
            throw new ComparisonFailure( msg, StringUtils.join(expectedLines,"\n"), StringUtils.join(actualLines,"\n") );
        }
    }

    public void assertNoMessages() {
        assertNoErrors();
        assertNoWarnings();
        assertNoDebugs();
        assertNoInfos();
    }
}
