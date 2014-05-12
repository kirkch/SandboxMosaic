package com.mosaic.lang.system;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.io.filesystemx.inmemory.InMemoryFileSystem;
import com.mosaic.io.streams.CapturingCharacterStream;
import com.mosaic.lang.text.PullParser;
import com.mosaic.lang.time.SystemClock;
import com.mosaic.utils.StringUtils;
import org.junit.ComparisonFailure;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;


/**
 *
 */
public class DebugSystem extends SystemX {

    /**
     * Format:
     *
     * [LEVEL nanosFromStart]: message
     */
    private final List<String> auditText;


    public DebugSystem() {
        this(
            new InMemoryFileSystem(),
            new SystemClock(),
            new Vector<String>(),
            System.nanoTime()
        );
    }


    private DebugSystem( InMemoryFileSystem system, SystemClock clock, List<String> auditText, long startTime ) {
        super(
            system,
            clock,
            new CapturingLogCharacterStream("DEBUG", auditText, startTime),
            new CapturingLogCharacterStream("AUDIT", auditText, startTime),
            new CapturingLogCharacterStream("INFO", auditText, startTime),
            new CapturingLogCharacterStream("WARN", auditText, startTime),
            new CapturingLogCharacterStream("ERROR", auditText, startTime),
            new CapturingLogCharacterStream("FATAL", auditText, startTime)
        );

        this.auditText = auditText;
    }


    public void tearDown() {
        fileSystem.deleteAll();
    }

    public void assertDebug( String expectedMessage ) {
        assertLogMessageContains( "DEBUG", expectedMessage );
    }

    public void assertAudit( String expectedMessage ) {
        assertLogMessageContains( "AUDIT", expectedMessage );
    }

    public void assertInfo( String expectedMessage ) {
        assertLogMessageContains( "INFO", expectedMessage );
    }

    public void assertWarn( String expectedMessage ) {
        assertLogMessageContains( "WARN", expectedMessage );
    }

    public void assertError( Class<? extends Throwable> expectedExceptionType, String expectedMessage ) {
        assertError( expectedExceptionType.getSimpleName() + ": " + expectedMessage );
    }

    public void assertError( String expectedMessage ) {
        assertLogMessageContains( "ERROR", expectedMessage );
    }

    private void assertNoLogMessages( String logLevel ) {
        String expectedPrefix = "["+logLevel+" ";

        int count = 0;

        for ( String msg : auditText ) {
            if ( msg.startsWith(expectedPrefix) ) {
                count++;
            }
        }

        if ( count > 0 ) {
            reportError( "Expected no "+logLevel.toLowerCase()+"s, instead found " + count + " of them" );
        }
    }

    public void assertNoDebugs() {
        assertNoLogMessages( "DEBUG" );
    }

    public void assertNoInfos() {
        assertNoLogMessages( "INFO" );
    }

    public void assertNoAudits() {
        assertNoLogMessages( "AUDIT" );
    }

    public void assertNoWarnings() {
        assertNoLogMessages( "WARN" );
    }

    public void assertNoErrors() {
        assertNoLogMessages( "ERROR" );
    }

    public void assertNoFatals() {
        assertNoLogMessages( "FATAL" );
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
        assertNoDebugs();
        assertNoInfos();
        assertNoAudits();
        assertNoWarnings();
        assertNoErrors();
        assertNoFatals();
    }

    private void assertLogMessageContains( String expectedLogLevel, String expectedMessage ) {
        String expectedPrefix = "["+expectedLogLevel+" ";

        expectedMessage = expectedMessage.trim();

        for ( String x : auditText ) {
            if ( x.startsWith(expectedPrefix) && x.endsWith(expectedMessage) ) {
                return;
            }
        }

        reportError(
            String.format(
                "Failed to find '%s' amongst the "+expectedLogLevel.toLowerCase()+" messages",
                expectedMessage
            )
        );
    }

    private void reportError( String msg ) {
        dumpLog();

        throw new AssertionError( msg );
    }

    public void dumpLog() {
        for ( String line : auditText ) {
            System.out.println( line );
        }
    }


    private static class CapturingLogCharacterStream extends CapturingCharacterStream {
        private String logLevel;

        private long startTimeNanos;

        public CapturingLogCharacterStream( String logLevel, List<String> auditText, long startTimeNanos ) {
            super(auditText);

            this.logLevel       = logLevel;
            this.startTimeNanos = startTimeNanos;
        }

        @Override
        protected String formatLine( String line ) {
            double time = (System.nanoTime() - startTimeNanos)/1000000.0;

            return "["+logLevel+" "+time+"]: "+ line.trim();
        }
    }
}


